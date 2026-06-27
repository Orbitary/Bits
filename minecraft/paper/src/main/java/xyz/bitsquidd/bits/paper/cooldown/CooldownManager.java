/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.cooldown;

import net.kyori.adventure.key.Key;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public final class CooldownManager implements CoreManager {
    public static final CooldownManager INSTANCE = new CooldownManager();

    private final Map<UUID, Map<Key, CooldownEntry>> cooldowns = new ConcurrentHashMap<>();

    private @Nullable BukkitTask tickTask;

    private static final class CooldownEntry {
        private long remainingTicks;
        private @Nullable Consumer<UUID> onExpire;

        CooldownEntry(long remainingTicks, @Nullable Consumer<UUID> onExpire) {
            this.remainingTicks = remainingTicks;
            this.onExpire = onExpire;
        }

    }

    private CooldownManager() {}

    @Override
    public void startup() {
        tickTask = Runnables.buildTimer(this::tick, 0, 1).forced().run();
    }

    @Override
    public void shutdown() {
        tickTask = Runnables.cleanup(tickTask);
    }

    private void tick() {
        for (Map.Entry<UUID, Map<Key, CooldownEntry>> playerEntry : cooldowns.entrySet()) {
            UUID uuid = playerEntry.getKey();
            Map<Key, CooldownEntry> playerCooldowns = playerEntry.getValue();
            Iterator<Map.Entry<Key, CooldownEntry>> it = playerCooldowns.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<Key, CooldownEntry> entry = it.next();
                CooldownEntry cd = entry.getValue();

                if (--cd.remainingTicks <= 0) {
                    it.remove();
                    if (cd.onExpire != null) cd.onExpire.accept(uuid);
                }
            }

            if (playerCooldowns.isEmpty()) cooldowns.remove(playerEntry.getKey(), playerCooldowns);
        }
    }

    @Override
    public void cleanup() {
        cooldowns.clear();
    }

    public void addCooldown(UUID uuid, Cooldown cooldown) {
        cooldowns
          .computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
          .put(cooldown.key(), new CooldownEntry(cooldown.ticks(), cooldown.onExpire()));
    }

    public void removeCooldown(UUID uuid, Cooldown cooldown, boolean ignoreExpire) {
        Map<Key, CooldownEntry> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return;

        CooldownEntry entry = playerCooldowns.remove(cooldown.key());
        if (entry == null) return;

        if (!ignoreExpire && entry.onExpire != null) entry.onExpire.accept(uuid);
    }

    public boolean isOnCooldown(UUID uuid, Cooldown cooldown) {
        return getRemainingTicks(uuid, cooldown) > 0;
    }

    public long getRemainingTicks(UUID uuid, Cooldown cooldown) {
        Map<Key, CooldownEntry> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0L;

        CooldownEntry entry = playerCooldowns.get(cooldown.key());
        return entry != null ? Math.max(0L, entry.remainingTicks) : 0L;
    }


    public void removeAllCooldowns(UUID uuid, boolean ignoreExpire) {
        Map<Key, CooldownEntry> playerCooldowns = cooldowns.remove(uuid);
        if (playerCooldowns == null) return;

        if (!ignoreExpire) {
            for (CooldownEntry entry : playerCooldowns.values()) {
                if (entry.onExpire != null) entry.onExpire.accept(uuid);
            }
        }
    }

    //region Static Helpers

    /**
     * Converts a {@link Duration} to server ticks (20 ticks/sec).
     */
    public static long toTicks(Duration duration) {
        return Math.max(1L, duration.toMillis() / 50L);
    }

    /**
     * Converts ticks back to a {@link Duration}.
     */
    public static Duration fromTicks(long ticks) {
        return Duration.ofMillis(ticks * 50L);
    }
    //endregion

}