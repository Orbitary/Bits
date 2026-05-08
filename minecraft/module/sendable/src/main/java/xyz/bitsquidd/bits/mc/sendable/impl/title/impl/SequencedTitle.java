/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title.impl;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class SequencedTitle extends AbstractTitle {

    private final List<TitlePhase> resolvedPhases;
    private final List<Integer> cumulativeTicks;
    private int lastPhaseIndex = -1; // tracks phase transitions


    protected SequencedTitle(List<TitlePhase> phases) {
        resolvedPhases = List.copyOf(phases);
        if (resolvedPhases.isEmpty()) throw new IllegalStateException("At least one TitlePhase must be provided");

        int sum = 0;
        List<Integer> cumulative = new ArrayList<>();
        for (TitlePhase phase : resolvedPhases) {
            sum += phase.durationTicks;
            cumulative.add(sum);
        }

        cumulativeTicks = Collections.unmodifiableList(cumulative);
    }

    private int currentPhaseIndex(SendableState state) {
        for (int i = 0; i < cumulativeTicks.size(); i++) {
            if (state.tick() < cumulativeTicks.get(i)) return i;
        }
        return resolvedPhases.size() - 1;
    }

    private TitlePhase currentPhase(SendableState state) {
        return resolvedPhases.get(currentPhaseIndex(state));
    }

    @Override
    public void onTick(SendableState state) {
        if (state.tick() > cumulativeTicks.getLast()) {
            state.handle().setTick(config().fadeOutStartTick); // A little jank to allow for nice fading out
            return;
        }

        int phaseIndex = currentPhaseIndex(state);
        TitlePhase phase = resolvedPhases.get(phaseIndex);

        if (phaseIndex != lastPhaseIndex) {
            if (lastPhaseIndex >= 0) resolvedPhases.get(lastPhaseIndex).onEnd.accept(state);
            phase.onStart.accept(state);
            lastPhaseIndex = phaseIndex;
        }

        phase.onTick.accept(state);
    }

    @Override
    public void onExpire(SendableState state) {
        if (lastPhaseIndex >= 0) resolvedPhases.get(lastPhaseIndex).onEnd.accept(state);
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