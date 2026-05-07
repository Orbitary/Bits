/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title.impl;/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public abstract class SequencedTitle extends AbstractTitle {

    public static class TitlePhase {
        public final int durationTicks;
        public final Function<SendableState, Component> title;
        public final Function<SendableState, Component> subtitle;
        public final Consumer<SendableState> onStart;
        public final Consumer<SendableState> onEnd;
        public final Consumer<SendableState> onTick;

        private TitlePhase(Builder builder) {
            this.durationTicks = builder.durationTicks;
            this.title = builder.title;
            this.subtitle = builder.subtitle;
            this.onStart = builder.onStart;
            this.onEnd = builder.onEnd;
            this.onTick = builder.onTick;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static CountdownFactory countdown(int maxNumber, boolean countDown) {
            return new CountdownFactory(maxNumber, countDown);
        }

        public static final class Builder implements Buildable<TitlePhase> {
            private int durationTicks = 0;
            private Function<SendableState, Component> title = s -> Component.empty();
            private Function<SendableState, Component> subtitle = s -> Component.empty();
            private Consumer<SendableState> onStart = s -> {};
            private Consumer<SendableState> onEnd = s -> {};
            private Consumer<SendableState> onTick = s -> {};

            private Builder() {}

            public Builder duration(int durationTicks) {
                this.durationTicks = durationTicks;
                return this;
            }

            public Builder title(Function<SendableState, Component> title) {
                this.title = title;
                return this;
            }

            public Builder title(Component title) {
                this.title = s -> title;
                return this;
            }

            public Builder subtitle(Function<SendableState, Component> subtitle) {
                this.subtitle = subtitle;
                return this;
            }

            public Builder subtitle(Component subtitle) {
                this.subtitle = s -> subtitle;
                return this;
            }

            public Builder onStart(Consumer<SendableState> onStart) {
                this.onStart = onStart;
                return this;
            }

            public Builder onEnd(Consumer<SendableState> onEnd) {
                this.onEnd = onEnd;
                return this;
            }

            public Builder onTick(Consumer<SendableState> onTick) {
                this.onTick = onTick;
                return this;
            }

            @Override
            public TitlePhase build() {
                if (durationTicks <= 0) throw new IllegalStateException("TitlePhase duration must be > 0");
                return new TitlePhase(this);
            }

        }

        public static final class CountdownFactory {
            private final int maxNumber;
            private final boolean countDown;
            private CountdownContentProvider title = (s, n) -> Component.empty();
            private CountdownContentProvider subtitle = (s, n) -> Component.empty();
            private CountdownAction onStart = (s, n) -> {};
            private CountdownAction onEnd = (s, n) -> {};
            private CountdownAction onTick = (s, n) -> {};
            private int ticksPerNumber = 20;

            private List<TitlePhase> post = new ArrayList<>();

            @FunctionalInterface
            public interface CountdownContentProvider {
                Component apply(SendableState state, int number);

            }

            @FunctionalInterface
            public interface CountdownAction {
                void accept(SendableState state, int number);

            }


            private CountdownFactory(int maxNumber, boolean countDown) {
                this.maxNumber = maxNumber;
                this.countDown = countDown;
            }

            public CountdownFactory title(CountdownContentProvider title) {
                this.title = title;
                return this;
            }

            public CountdownFactory subtitle(CountdownContentProvider subtitle) {
                this.subtitle = subtitle;
                return this;
            }

            public CountdownFactory onStart(CountdownAction onStart) {
                this.onStart = onStart;
                return this;
            }

            public CountdownFactory onEnd(CountdownAction onEnd) {
                this.onEnd = onEnd;
                return this;
            }

            public CountdownFactory onTick(CountdownAction onTick) {
                this.onTick = onTick;
                return this;
            }

            public CountdownFactory ticksPerNumber(int ticks) {
                this.ticksPerNumber = ticks;
                return this;
            }

            public CountdownFactory postPhase(TitlePhase phase) {
                this.post.add(phase);
                return this;
            }

            public List<TitlePhase> build() {
                List<Integer> numbers = new ArrayList<>();
                for (int i = 0; i < maxNumber; i++) {
                    numbers.add(countDown ? maxNumber - i : i + 1);
                }

                List<TitlePhase> phases = new ArrayList<>();

                phases.addAll(numbers.stream()
                  .map(number -> TitlePhase.builder()
                    .duration(ticksPerNumber)
                    .title(state -> title.apply(state, number))
                    .subtitle(state -> subtitle.apply(state, number))
                    .onStart(state -> onStart.accept(state, number))
                    .onEnd(state -> onEnd.accept(state, number))
                    .onTick(state -> onTick.accept(state, number))
                    .build()
                  )
                  .toList());

                phases.addAll(post);

                return phases;
            }

        }

    }

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