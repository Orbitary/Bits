/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;
import xyz.orbitary.bits.mc.module.command.BrigadierCommandBuilder;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * Registers every {@link xyz.orbitary.bits.module.command.Command} in the classpath with a Fabric command dispatcher.
 * <p>
 * The platform decides when a dispatcher is available: {@code onDispatcher} is given the callback that receives
 * each dispatcher, and registers it with the platform's command registration event.
 *
 * @param <S> the Fabric command source type
 *
 * @since 0.1.6
 */
public final class FabricCommandManager<S> implements BitsModule {

    private final Consumer<BrigadierArguments<S>> customArguments;
    private final BiConsumer<S, Throwable> errorHandler;
    private final Consumer<Consumer<CommandDispatcher<S>>> onDispatcher;

    /**
     * @param customArguments adds argument types beyond the defaults, such as a mod's own types
     * @param errorHandler    sends an error to the source of a command that did not run
     * @param onDispatcher    subscribes to the platform's command registration, passing each dispatcher to the callback
     *
     * @since 0.1.6
     */
    public FabricCommandManager(
      Consumer<BrigadierArguments<S>> customArguments,
      BiConsumer<S, Throwable> errorHandler,
      Consumer<Consumer<CommandDispatcher<S>>> onDispatcher
    ) {
        this.customArguments = customArguments;
        this.errorHandler = errorHandler;
        this.onDispatcher = onDispatcher;
    }

    @Override
    public void startup() {
        BrigadierArguments<S> arguments = BrigadierArguments.withDefaults();
        customArguments.accept(arguments);

        List<CommandDefinition> definitions = CommandScanner.scanAll(type -> false);
        BrigadierCommandBuilder<S> builder = new BrigadierCommandBuilder<>(
          arguments,
          ignored -> null,
          errorHandler,
          runnable -> Bits.get().runLaterAsync(runnable, 0)
        );
        List<LiteralArgumentBuilder<S>> roots = builder.build(definitions);

        onDispatcher.accept(dispatcher -> roots.forEach(dispatcher::register));
        Logger.success("Registered " + definitions.size() + " commands with Fabric");
    }

}
