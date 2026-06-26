/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.paper.effect.Effect;

import java.util.UUID;


public record EffectInstance(
  Effect effect,
  EffectModifier modifier,
  LivingEntity target,
  UUID uuid,
  long startTick
) {
    @ApiStatus.Internal
    public EffectInstance {}


    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EffectInstance other && other.uuid.equals(uuid);
    }


    public EffectInstance transform(StateTransform transform) {
        return new EffectInstance(effect, transform.transform(modifier), target, uuid, startTick);
    }


    // TODO: If transforms are LONGER, this will cause it to be cancelled prematurely.
    public boolean isExpired(long currentTick) {
        return modifier.durationTicks() >= 0 && (currentTick - startTick) >= modifier.durationTicks();
    }

}
