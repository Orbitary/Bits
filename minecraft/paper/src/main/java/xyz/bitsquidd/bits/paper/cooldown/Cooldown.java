/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.cooldown;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;


// TODO - duration cooldowns might be better async?
public record Cooldown(
  Key key,
  long ticks,
  @Nullable Consumer<UUID> onExpire
) {
    private static final CooldownManager COOLDOWN_MANAGER = CooldownManager.INSTANCE;

    public static Cooldown of(Key key, long ticks) {
        return new Cooldown(key, ticks, null);
    }

    public static Cooldown of(Key key, long ticks, Consumer<UUID> onExpire) {
        return new Cooldown(key, ticks, onExpire);
    }

    public static Cooldown ofDuration(Key key, Duration duration) {
        return new Cooldown(key, CooldownManager.toTicks(duration), null);
    }

    public static Cooldown ofDuration(Key key, Duration duration, Consumer<UUID> onExpire) {
        return new Cooldown(key, CooldownManager.toTicks(duration), onExpire);
    }


    public void apply(UUID uuid) {
        COOLDOWN_MANAGER.addCooldown(uuid, this);
    }

    public void remove(UUID uuid) {
        remove(uuid, false);
    }

    public void remove(UUID uuid, boolean ignoreExpire) {
        COOLDOWN_MANAGER.removeCooldown(uuid, this, ignoreExpire);
    }


    public boolean has(UUID uuid) {
        return COOLDOWN_MANAGER.isOnCooldown(uuid, this);
    }

    public long remainingTicks(UUID uuid) {
        return COOLDOWN_MANAGER.getRemainingTicks(uuid, this);
    }

    public Duration remainingDuration(UUID uuid) {
        return CooldownManager.fromTicks(COOLDOWN_MANAGER.getRemainingTicks(uuid, this));
    }


    public CooldownState state(UUID uuid) {
        return new CooldownState(uuid, this);
    }

    public record CooldownState(
      UUID uuid,
      Cooldown cooldown
    ) {
        public CooldownState ifNotReady(Runnable action) {
            if (cooldown.has(uuid)) action.run();
            return this;
        }

        public CooldownState ifReady(Runnable action) {
            if (!cooldown.has(uuid)) action.run();
            return this;
        }

        public CooldownState applyIfReady(Runnable action) {
            ifReady(
              () -> {
                  cooldown.apply(uuid);
                  action.run();
              }
            );
            return this;
        }

    }


    //region Java Overrides
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Cooldown other) && this.key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Cooldown:" + key;
    }
    //endregion

}
