/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.lifecycle.manager;

import xyz.orbitary.bits.lifecycle.manager.CoreManager;
import xyz.orbitary.bits.lifecycle.manager.ManagerOrchestrator;
import xyz.orbitary.bits.velocity.util.velocity.listener.Listener;
import xyz.orbitary.bits.velocity.util.velocity.listener.Listeners;
import xyz.orbitary.bits.velocity.util.velocity.listener.PermanentListener;


public class VelocityManagerOrchestrator extends ManagerOrchestrator {

    public static VelocityManagerOrchestrator get() {
        return (VelocityManagerOrchestrator)ManagerOrchestrator.get();
    }


    @Override
    public void preStartup(CoreManager manager) {
        super.preStartup(manager);
        if (manager instanceof Listener listener) Listeners.register(listener);
    }

    @Override
    public void preInitialise(CoreManager manager) {
        super.preInitialise(manager);
        if (manager instanceof Listener listener && !(manager instanceof PermanentListener)) Listeners.register(listener);
    }

    @Override
    public void postCleanup(CoreManager manager) {
        super.postCleanup(manager);
        if (manager instanceof Listener listener && !(manager instanceof PermanentListener)) Listeners.unregister(listener);
    }

    @Override
    public void postShutdown(CoreManager manager) {
        super.postShutdown(manager);
        if (manager instanceof Listener listener) Listeners.unregister(listener);
    }

}