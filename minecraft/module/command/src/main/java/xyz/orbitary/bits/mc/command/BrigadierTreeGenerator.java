/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.mc.command.debugging.TreeDebugger;
import xyz.orbitary.bits.mc.command.exception.CommandBuildException;
import xyz.orbitary.bits.mc.command.util.BitsCommandBuilder;
import xyz.orbitary.bits.mc.command.util.BitsCommandContext;
import xyz.orbitary.bits.mc.command.util.BitsCommandSourceContext;
import xyz.orbitary.bits.mc.command.util.CommandInvoker;
import xyz.orbitary.bits.mc.command.util.CommandMethodInfo;
import xyz.orbitary.bits.mc.command.util.CommandParameterInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * Utility responsible for constructing Brigadier command trees from defined command builders.
 * <p>
 * This handles parsing arguments, applying requirements, and wiring execution logic from the
 * abstracted command definitions into Brigadier's native node structure.
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public final class BrigadierTreeGenerator<T> {
    private final BitsCommandManager<T> commandManager;

    /**
     * Constructs a new Brigadier tree generator.
     *
     * @param commandManager the command manager managing the registry and context
     *
     * @since 0.0.10
     */
    public BrigadierTreeGenerator(BitsCommandManager<T> commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Creates and returns a list of literal command nodes based on the provided builder.
     *
     * @param commandBuilder the command builder defining the structure and logic
     *
     * @return a list of generated literal nodes
     *
     * @since 0.0.10
     */
    public List<LiteralCommandNode<T>> createNodes(
      BitsCommandBuilder commandBuilder
    ) {
        // We create a "dummy_root" to be able to split core aliases.
        LiteralArgumentBuilder<T> dummyRoot = commandManager.createLiteral("dummy_root");
        processCommandClass(dummyRoot, commandBuilder, new ArrayList<>());

        List<LiteralCommandNode<T>> nodes = dummyRoot.getArguments().stream()
          .filter(node -> node instanceof LiteralCommandNode)
          .map(node -> (LiteralCommandNode<T>)node)
          .toList();

        if (Bits.get().isDevelopment()) Logger.info(new TreeDebugger<T>().visualizeCommandTree(nodes));
        return nodes;
    }

    // Returns a non-empty list of branches that will be built upon. This includes command aliases.
    // TODO look into PaperBrigadier copyLiteral
    private List<ArgumentBuilder<T, ?>> createNextBranches(
      final BitsCommandBuilder commandBuilder,
      final @Nullable ArgumentBuilder<T, ?> root
    ) {
        List<ArgumentBuilder<T, ?>> commandBranches = new ArrayList<>();

        String commandName = commandBuilder.getCommandName();
        List<String> commandAliases = new ArrayList<>(commandBuilder.getCommandAliases());
        commandAliases.add(commandName);

        // Note: Aliases are not created for commands without names.
        if (commandName.isEmpty()) {
            if (root == null) throw new CommandBuildException("Root command class must have a name.");
            commandBranches.add(root);
        } else {
            List<LiteralArgumentBuilder<T>> nextBranches = commandAliases.stream()
              .map(commandManager::createLiteral)
              .toList();

            nextBranches.forEach(argumentBuilder -> {
                mergeRequirement(
                  argumentBuilder, ctx -> {
                      BitsCommandSourceContext<T> source = commandManager.createSourceContext(ctx);
                      return commandBuilder.getPermissions().stream().anyMatch(permission ->
                        permission.hasPermission(source.getSender())
                      );
                  }
                );
            });

            commandBranches.addAll(nextBranches);
        }

        return commandBranches;
    }

    @SuppressWarnings("unchecked")
    private void processCommandClass(
      final ArgumentBuilder<T, ?> branch,
      final BitsCommandBuilder commandBuilder,
      final List<CommandParameterInfo> addedParameters
    ) {
        // Calculate requirements required for this branch
        mergeRequirement(
          branch,
          ctx -> {
              BitsCommandSourceContext<T> source = commandManager.createSourceContext(ctx);
              return commandBuilder.getRequirements().stream().allMatch(requirement -> requirement.test(source));
          }
        );

        // Create parameters needed for this class.
        List<Parameter> classParamList = commandBuilder.getParameters();
        List<CommandParameterInfo> classParameters = new ArrayList<>();
        for (int i = 0; i < classParamList.size(); i++) {
            classParameters.add(new CommandParameterInfo(classParamList.get(i), i));
        }
        List<CommandParameterInfo> nonMutatedParameters = new ArrayList<>(addedParameters);
        nonMutatedParameters.addAll(classParameters);

        // Create next branches with aliases
        List<ArgumentBuilder<T, ?>> nextBranches = createNextBranches(commandBuilder, branch);

        nextBranches.forEach(nextBranch -> {
            List<ArgumentBuilder<T, ?>> addedParamBranches = new ArrayList<>();

            // Add brigadier branches for all the new parameters
            classParameters.forEach(param -> {
                addedParamBranches.addAll(param.createBrigadierArguments());
            });

            ArgumentBuilder<T, ?> workingBranch = nextBranch;
            if (!addedParamBranches.isEmpty()) workingBranch = addedParamBranches.getLast();

            for (Class<? extends BitsCommand> nestedClass : commandBuilder.getSubcommandClasses()) {
                processCommandClass(workingBranch, new BitsCommandBuilder(nestedClass), nonMutatedParameters);
            }

            for (Method method : commandBuilder.getCommandMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    Logger.warn("Skipping non-public command method: " + method.getName() + " for command: " + commandBuilder.getCommandName());
                    continue;
                }
                processCommandMethod(workingBranch, commandBuilder, new CommandMethodInfo(method, nonMutatedParameters, commandBuilder.getCorePermission()));
            }

            // Populate each child fully, then attach it to the parent
            if (!Objects.equals(workingBranch, nextBranch)) nextBranch.then(chain(addedParamBranches));
            if (!Objects.equals(nextBranch, branch)) branch.then(nextBranch);
        });
    }


    // Builds executions onto the root.
    private void processCommandMethod(
      final ArgumentBuilder<T, ?> nextBranch,
      final BitsCommandBuilder commandBuilder,
      final CommandMethodInfo<T> methodInfo
    ) {
        // Add all extra parameters to the branch.
        List<ArgumentBuilder<T, ?>> paramBranch = new ArrayList<>();

        // Add literal name if it exists.
        // Note we currently don't support aliases for method literals.
        if (!methodInfo.literalName().isEmpty()) {
            paramBranch.add(commandManager.createLiteral(methodInfo.literalName()));
        }

        methodInfo.getMethodParameters().forEach(param -> {
            paramBranch.addAll(param.createBrigadierArguments());
        });

        ArgumentBuilder<T, ?> workingBranch;
        if (!paramBranch.isEmpty()) {
            workingBranch = paramBranch.getLast();
        } else {
            workingBranch = nextBranch;
        }

        // Add method requirements
        mergeRequirement(
          workingBranch, ctx -> {
              BitsCommandSourceContext<T> source = commandManager.createSourceContext(ctx);
              return methodInfo.getRequirements().stream()
                .allMatch(requirement -> requirement.test(source));
          }
        );
        workingBranch.executes(createCommandExecution(commandBuilder, methodInfo));

        if (!Objects.equals(workingBranch, nextBranch)) nextBranch.then(chain(paramBranch));
    }


    // Creates a command execution when no more parameters need to be added.
    @SuppressWarnings("NullableProblems")
    private Command<T> createCommandExecution(
      final BitsCommandBuilder commandBuilder,
      final CommandMethodInfo<T> methodInfo
    ) {
        final CommandInvoker invoker = new CommandInvoker(commandBuilder, methodInfo);

        return ctx -> {
            final BitsCommandContext<T> bitsCtx = commandManager.createContext(ctx);

            // Create the list of arguments needed to call the method.
            List<@Nullable Object> parsedArguments = new ArrayList<>();
            for (CommandParameterInfo parameter : methodInfo.getAllParameters()) {
                parsedArguments.add(parameter.parse(ctx, bitsCtx));
            }

            // Execute the command with the required, parsed arguments
            commandManager.executeCommand(methodInfo.isAsync(), () -> invoker.invoke(parsedArguments, bitsCtx));

            return Command.SINGLE_SUCCESS;
        };
    }


    // Links the branches into a single chain (each the child of the previous) and returns the head.
    private ArgumentBuilder<T, ?> chain(List<ArgumentBuilder<T, ?>> branches) {
        if (branches.isEmpty()) throw new CommandBuildException("No more branches to add.");

        for (int i = branches.size() - 2; i >= 0; i--) {
            branches.get(i).then(branches.get(i + 1));
        }
        return branches.getFirst();
    }

    private <S> void mergeRequirement(ArgumentBuilder<S, ?> builder, Predicate<S> newRequirement) {
        Predicate<S> previousRequirement = builder.getRequirement();
        builder.requires(ctx -> previousRequirement.test(ctx) && newRequirement.test(ctx));
    }

}
