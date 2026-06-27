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


/**
 * Captures the runtime state of an active {@link Effect}: the effect definition, the
 * {@link EffectModifier} at registration time, the target entity, a unique identifier,
 * and the server tick at which the effect started.
 *
 * @param effect    the effect definition
 * @param modifier  the modifier active when the effect was registered
 * @param target    the entity the effect is applied to
 * @param uuid      the unique identifier for this instance
 * @param startTick the server tick at which this effect started
 *
 * @since 0.0.21
 */
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


    /**
     * Returns a new instance with the modifier replaced by the result of applying the given transform.
     *
     * @param transform the transform to apply to the current modifier
     * @return a new instance with the transformed modifier
     *
     * @since 0.0.21
     */
    public EffectInstance transform(EffectTransform transform) {
        return new EffectInstance(effect, transform.transform(modifier), target, uuid, startTick);
    }


    /**
     * Returns the absolute server tick at which this effect expires,
     * computed as {@code startTick + modifier().durationTicks()}.
     *
     * @return the expiry tick
     *
     * @since 0.0.21
     */
    public long endTick() {
        return startTick + modifier.durationTicks();
    }

}
