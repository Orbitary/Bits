/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.exception.BitsException;
import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.lifecycle.manager.ManagerContainer;
import xyz.bitsquidd.bits.lifecycle.manager.ManagerOrchestrator;
import xyz.bitsquidd.bits.wrapper.collection.AddableList;

import java.util.Optional;


/**
 * The main configuration class for the Bits library.
 * <p>
 * This must be created for correct Bits functionality. It acts as the central hub
 * for accessing core services like the logger and command manager.
 *
 * @since 0.0.10
 */
public abstract class Bits extends ManagerContainer {
    private static @Nullable Bits instance;

    protected boolean developmentMode = false;

    /**
     * @throws BitsException if an instance already exists
     * @since 0.0.10
     */
    protected Bits() {
        if (instance != null) throw BitsException.INSTANCE_ALREADY_EXISTS(Bits.class);
        instance = this;

        createManagerOrchestrator();

        registerManagers(modules().build());
    }

    public static Bits generic(String platformName) {
        return new GenericBitsConfig(platformName);
    }


    /**
     * Retrieves the active configuration instance.
     *
     * @return the configuration instance
     *
     * @throws BitsException if the instance has not been created
     * @since 0.0.10
     */
    public static Bits get() {
        if (instance == null) throw BitsException.INSTANCE_NOT_FOUND(Bits.class);
        return instance;
    }

    public static void init() {
        get().startup();
        get().initialise();
    }

    public static <M extends BitsModule> Optional<M> getModule(Class<M> clazz) {
        return get().getManager(clazz);
    }


    /**
     * Defines the modules to be registered with the library.
     *
     * @since 0.0.14
     */
    protected AddableList<BitsModule> modules() {
        return new AddableList<>();
    }


    public static Key key(String key) {
        return Key.key("bits", key);
    }


    /**
     * Indicates whether the library is operating in development mode.
     * This is used for showing increased debugging.
     *
     * @return true if development mode is active, false otherwise
     *
     * @since 0.0.10
     */
    public boolean isDevelopment() {
        return developmentMode;
    }

    protected ManagerOrchestrator createManagerOrchestrator() {
        return new ManagerOrchestrator();
    }


    public abstract void runLater(Runnable runnable, long delayMs);

    public void runLaterAsync(Runnable runnable, long delayMs) {
        runLater(runnable, delayMs); // By default, all threads are "async"
    }

}
