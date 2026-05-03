/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.config.BitsConfig;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.argument.BrigadierArgumentMapping;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.debugging.TreeDebugger;
import xyz.bitsquidd.bits.mc.command.exception.CommandBuildException;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.mc.command.util.CommandMethodInfo;
import xyz.bitsquidd.bits.mc.command.util.CommandParameterInfo;
import xyz.bitsquidd.bits.wrapper.GreedyString;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

        if (BitsConfig.get().isDevelopment()) Logger.info(new TreeDebugger<T>().visualizeCommandTree(nodes));
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
                  argumentBuilder, ctx ->
                    commandBuilder.getPermissions().stream().anyMatch(permission ->
                      permission.hasPermission(commandManager.createSourceContext(ctx).getSender())
                    )
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
          ctx -> commandBuilder.getRequirements().stream()
            .allMatch(requirement -> requirement.test(commandManager.createSourceContext(ctx)))
        );

        // Create parameters needed for this class.
        List<CommandParameterInfo> classParameters = commandBuilder.getParameters().stream().map(CommandParameterInfo::new).toList();
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
                processCommandMethod(workingBranch, commandBuilder, new CommandMethodInfo(method, nonMutatedParameters));
            }

            // Populate each child fully, then attach it to the parent
            if (!Objects.equals(workingBranch, nextBranch)) nextBranch.then(buildBackward(addedParamBranches));
            if (!Objects.equals(nextBranch, branch)) branch.then(buildBackward(List.of(nextBranch)));
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
          workingBranch, ctx ->
            methodInfo.getRequirements(commandBuilder.getCorePermission()).stream()
              .allMatch(requirement -> requirement.test(commandManager.createSourceContext(ctx)))
        );
        workingBranch.executes(createCommandExecution(commandBuilder, methodInfo));

        if (!Objects.equals(workingBranch, nextBranch)) nextBranch.then(buildBackward(paramBranch));
    }


    // Creates a command execution when no more parameters need to be added.
    @SuppressWarnings("NullableProblems")
    private Command<T> createCommandExecution(
      final BitsCommandBuilder commandBuilder,
      final CommandMethodInfo<T> methodInfo
    ) {
        return ctx -> {
            final BitsCommandContext<T> bitsCtx = commandManager.createContext(ctx);

            // Create the list of arguments needed to call the method.
            ArrayList<@Nullable Object> parsedArguments = new ArrayList<>();

            for (CommandParameterInfo parameter : methodInfo.getAllParameters()) {
                AbstractArgumentParser<?> parser = parameter.getParser();

                // Collect primitive objects for the parameter
                ArrayList<@Nullable Object> primitiveObjects = new ArrayList<>();
                for (int i = 0; i < parser.getInputTypes().size(); i++) {
                    BrigadierArgumentMapping holder = parameter.getHeldArguments().get(i);

                    Object primitiveValue;
                    try {
                        Class<?> checkType = holder.typeSignature().toRawType();
                        if (checkType == GreedyString.class) checkType = String.class; // Special case for greedy strings as they require a GreedyString input, but parse into String

                        primitiveValue = ctx.getArgument(holder.argumentName(), checkType);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Failed to get argument: " + parameter, e);
                    }

                    primitiveObjects.add(primitiveValue);
                }

                Object value;
                try {
                    if (primitiveObjects.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("One or more primitive arguments are null.");

                    value = commandManager.getArgumentRegistry().parseArguments(parser, primitiveObjects, commandManager.createContext(ctx));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Failed to get argument: " + parameter, e);
                }

                parsedArguments.add(value);
            }


            // Execute the command with the required, parsed arguments
            Runnable commandExecution = () -> {
                try {
                    Constructor<?> commandClass = commandBuilder.toConstructor();
                    int constructorParamCount = commandBuilder.getParameters().size();

                    Object[] constructorArgs = parsedArguments.subList(0, constructorParamCount).toArray();

                    if (commandBuilder.requiresOuterInstance()) {
                        Class<?> outerClass = commandBuilder.getCommandClass().getDeclaringClass();
                        Object outerInstance = outerClass.getDeclaredConstructor().newInstance();

                        Object[] argsWithOuter = new Object[constructorArgs.length + 1];
                        argsWithOuter[0] = outerInstance;
                        System.arraycopy(constructorArgs, 0, argsWithOuter, 1, constructorArgs.length);
                        constructorArgs = argsWithOuter;
                    }

                    Object instance = commandClass.newInstance(constructorArgs);

                    List<@Nullable Object> methodArguments = constructorParamCount < parsedArguments.size()
                                                             ? new ArrayList<>(parsedArguments.subList(constructorParamCount, parsedArguments.size()))
                                                             : new ArrayList<>();

                    if (methodInfo.requiresContext()) methodArguments.addFirst(bitsCtx);

                    methodInfo.getMethod().invoke(instance, methodArguments.toArray());

                } catch (Exception e) {
                    throw new CommandBuildException("Failed to execute command method: " + methodInfo.getMethod().getName() + " ", e);
                }
            };

            commandManager.executeCommand(methodInfo.isAsync(), commandExecution);

            return Command.SINGLE_SUCCESS;
        };
    }


    private ArgumentBuilder<T, ?> buildBackward(List<ArgumentBuilder<T, ?>> toAdd) {
        if (toAdd.isEmpty()) throw new CommandBuildException("No more branches to add.");
        if (toAdd.size() == 1) return toAdd.getFirst();

        ArgumentBuilder<T, ?> first = toAdd.getFirst();
        List<ArgumentBuilder<T, ?>> rest = toAdd.subList(1, toAdd.size());
        first.then(buildBackward(new ArrayList<>(rest)));
        return first;
    }

    private <S> void mergeRequirement(ArgumentBuilder<S, ?> builder, Predicate<S> newRequirement) {
        Predicate<S> previousRequirement = builder.getRequirement();
        builder.requires(ctx -> previousRequirement.test(ctx) && newRequirement.test(ctx));
    }

}
