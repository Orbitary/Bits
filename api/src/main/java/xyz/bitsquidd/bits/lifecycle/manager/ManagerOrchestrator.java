/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.manager;

import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.exception.BitsException;


/**
 * Class which is called-back-to by a {@link ManagerContainer} to orchestrate the lifecycle of its registered managers.
 *
 * @since 0.0.14
 */
public class ManagerOrchestrator implements CoreManager {
    private static @Nullable ManagerOrchestrator instance;

    public ManagerOrchestrator() {
        if (instance != null) throw BitsException.INSTANCE_ALREADY_EXISTS(ManagerOrchestrator.class);
        instance = this;
    }

    public static ManagerOrchestrator get() {
        if (instance == null) throw BitsException.INSTANCE_NOT_FOUND(ManagerOrchestrator.class);
        return instance;
    }


    public void preStartup(CoreManager manager) {}

    public void postStartup(CoreManager manager) {}

    public void preShutdown(CoreManager manager) {}

    public void postShutdown(CoreManager manager) {}

    @Deprecated(forRemoval = true, since = "0.0.18")
    public void preInitialise(CoreManager manager) {}

    @Deprecated(forRemoval = true, since = "0.0.18")
    public void postInitialise(CoreManager manager) {}

    @Deprecated(forRemoval = true, since = "0.0.18")
    public void preCleanup(CoreManager manager) {}

    @Deprecated(forRemoval = true, since = "0.0.18")
    public void postCleanup(CoreManager manager) {}

}
