/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;
import xyz.bitsquidd.bits.paper.effect.state.EffectModifier;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public final class EffectManager implements CoreManager {
    public static final EffectManager INSTANCE = new EffectManager();

    private final Map<UUID, Map<Effect, EffectInstance>> activeEffects = new HashMap<>();

    private long tick = 0L;
    private @Nullable BukkitTask ticker;


    @Override
    public void startup() {
        ticker = Runnables.buildTimer(this::tick, 0, 1)
          .forced()
          .run();
    }

    @Override
    public void shutdown() {
        ticker = Runnables.cleanup(ticker);
    }

    @Override
    public void cleanup() {
        activeEffects.forEach((uuid, effects) ->
          Optional.ofNullable(Bukkit.getEntity(uuid)).ifPresent(_ ->
            effects.values().forEach(instance -> instance.effect().unapply(instance))
          )
        );
        activeEffects.clear();
    }


    private void tick() {
        tick++;
        List.copyOf(activeEffects.values()).stream()
          .flatMap(m -> m.values().stream())
          .forEach(instance -> instance.effect().tick(instance, tick));
    }


    @ApiStatus.Internal
    public void attemptApplyTo(LivingEntity livingEntity, Effect effect, EffectModifier modifier) {
        EffectInstance instance = new EffectInstance(effect, modifier, livingEntity, UUID.randomUUID(), tick);
        instance.effect().apply(instance);
    }

    @ApiStatus.Internal
    public EffectInstance applyEffectInternal(EffectInstance instance) {
        activeEffects.computeIfAbsent(instance.target().getUniqueId(), _ -> new HashMap<>()).put(instance.effect(), instance);
        return instance;
    }

    @ApiStatus.Internal
    public void attemptUnapplyFrom(LivingEntity livingEntity, Effect effect) {
        EffectInstance instance = activeEffects.getOrDefault(livingEntity.getUniqueId(), Collections.emptyMap()).remove(effect);
        if (instance == null) return;
        instance.effect().unapply(instance);
    }

    @ApiStatus.Internal
    public void unapplyEffectInternal(EffectInstance instance) {
        LivingEntity entity = instance.target();
        Map<Effect, EffectInstance> effects = activeEffects.get(entity.getUniqueId());
        if (effects == null) return;
        effects.remove(instance.effect());
    }


}
