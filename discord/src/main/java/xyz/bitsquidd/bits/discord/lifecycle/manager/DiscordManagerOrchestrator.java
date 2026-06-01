/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.lifecycle.manager;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import xyz.bitsquidd.bits.BitsDiscord;
import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.lifecycle.manager.ManagerOrchestrator;


public class DiscordManagerOrchestrator extends ManagerOrchestrator {

    public static DiscordManagerOrchestrator get() {
        return (DiscordManagerOrchestrator)ManagerOrchestrator.get();
    }


    @Override
    public void preStartup(CoreManager manager) {
        super.preStartup(manager);
        if (manager instanceof ListenerAdapter listener) BitsDiscord.jda().addEventListener(listener);
    }

    @Override
    public void preInitialise(CoreManager manager) {
        super.preInitialise(manager);
        if (manager instanceof ListenerAdapter listener) BitsDiscord.jda().addEventListener(listener);
    }

    @Override
    public void postCleanup(CoreManager manager) {
        super.postCleanup(manager);
        if (manager instanceof ListenerAdapter listener) BitsDiscord.jda().removeEventListener(listener);
    }

    @Override
    public void postShutdown(CoreManager manager) {
        super.postShutdown(manager);
        if (manager instanceof ListenerAdapter listener) BitsDiscord.jda().removeEventListener(listener);
    }

}