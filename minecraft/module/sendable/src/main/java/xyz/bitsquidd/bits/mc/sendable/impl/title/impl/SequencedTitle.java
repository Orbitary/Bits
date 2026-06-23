/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title.impl;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class SequencedTitle extends AbstractTitle {
    private static final Key LAST_STARTED_PHASE = Key.key("bits", "sequenced_title_last_started_phase");

    private final List<TitlePhase> resolvedPhases;
    private final List<Long> cumulativeTicks;

    protected SequencedTitle(List<TitlePhase> phases) {
        resolvedPhases = List.copyOf(phases);
        if (resolvedPhases.isEmpty()) throw new IllegalStateException("At least one TitlePhase must be provided");

        long sum = 0;
        List<Long> cumulative = new ArrayList<>();
        for (TitlePhase phase : resolvedPhases) {
            sum += phase.durationTicks;
            cumulative.add(sum);
        }

        cumulativeTicks = Collections.unmodifiableList(cumulative);
    }

    private TitlePhase currentPhase(SendableState state) {
        return resolvedPhases.get(phaseIndexAtTick(state.tick()));
    }

    private int phaseIndexAtTick(long tick) {
        for (int i = 0; i < cumulativeTicks.size(); i++) {
            if (tick < cumulativeTicks.get(i)) return i;
        }
        return resolvedPhases.size() - 1;
    }

    @Override
    public void onTick(SendableState state) {
        if (state.tick() >= cumulativeTicks.getLast()) {
            state.handle().setTick(config().fadeOutStartTick);
            return;
        }
        int phaseIndex = phaseIndexAtTick(state.tick());
        TitlePhase phase = resolvedPhases.get(phaseIndex);

        int prevPhaseIndex = state.tick() > 0 ? phaseIndexAtTick(state.tick() - 1) : -1;
        if (phaseIndex != prevPhaseIndex) {
            if (prevPhaseIndex >= 0) resolvedPhases.get(prevPhaseIndex).onEnd.accept(state);
            state.handle().data(LAST_STARTED_PHASE, phaseIndex);
            phase.onStart.accept(state);
        }
        phase.onTick.accept(state);
    }

    @Override
    public void onExpire(SendableState state) {
        Object lastStarted = state.handle().getData(LAST_STARTED_PHASE);
        int phaseIndex = lastStarted instanceof Integer i ? i : phaseIndexAtTick(state.tick());
        resolvedPhases.get(phaseIndex).onEnd.accept(state);
    }


    @Override
    public Component title(SendableState state) {
        return currentPhase(state).title.apply(state);
    }

    @Override
    public Component subtitle(SendableState state) {
        return currentPhase(state).subtitle.apply(state);
    }

}