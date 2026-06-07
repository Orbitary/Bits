/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title.impl;

import net.kyori.adventure.text.Component;

import org.jetbrains.annotations.Range;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class TitlePhase {

    @FunctionalInterface
    public interface CountdownContentProvider {
        Component apply(SendableState state, int number);

        static CountdownContentProvider basic() {
            return (s, n) -> Component.empty();
        }

    }

    @FunctionalInterface
    public interface CountdownAction {
        void accept(SendableState state, int number);

        static CountdownAction basic() {
            return (s, n) -> {};
        }

    }

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

    public static DelayedFactory delayed(List<Integer> delayTicks) {
        return new DelayedFactory(delayTicks);
    }

    public static final class Builder implements Buildable<TitlePhase> {
        private int durationTicks = 1;
        private Function<SendableState, Component> title = s -> Component.empty();
        private Function<SendableState, Component> subtitle = s -> Component.empty();
        private Consumer<SendableState> onStart = s -> {};
        private Consumer<SendableState> onEnd = s -> {};
        private Consumer<SendableState> onTick = s -> {};

        private Builder() {}

        public Builder duration(@Range(from = 1, to = Integer.MAX_VALUE) int durationTicks) {
            this.durationTicks = Math.clamp(durationTicks, 1, Integer.MAX_VALUE);
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
            return new TitlePhase(this);
        }

    }

    public static final class CountdownFactory implements Buildable<List<TitlePhase>> {
        private final int maxNumber;
        private final boolean countDown;
        private CountdownContentProvider title = CountdownContentProvider.basic();
        private CountdownContentProvider subtitle = CountdownContentProvider.basic();
        private CountdownAction onStart = CountdownAction.basic();
        private CountdownAction onEnd = CountdownAction.basic();
        private CountdownAction onTick = CountdownAction.basic();
        private int ticksPerNumber = 20;

        private final List<TitlePhase> post = new ArrayList<>();

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

        @Override
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

    public static final class DelayedFactory implements Buildable<List<TitlePhase>> {
        private final List<Integer> delayTicks;

        private CountdownContentProvider title = CountdownContentProvider.basic();
        private CountdownContentProvider subtitle = CountdownContentProvider.basic();
        private CountdownAction onStart = CountdownAction.basic();
        private CountdownAction onEnd = CountdownAction.basic();
        private CountdownAction onTick = CountdownAction.basic();

        private final List<TitlePhase> post = new ArrayList<>();

        public DelayedFactory(List<Integer> delayTicks) {
            this.delayTicks = List.copyOf(delayTicks);
        }


        public DelayedFactory title(CountdownContentProvider title) {
            this.title = title;
            return this;
        }

        public DelayedFactory subtitle(CountdownContentProvider subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public DelayedFactory onStart(CountdownAction onStart) {
            this.onStart = onStart;
            return this;
        }

        public DelayedFactory onEnd(CountdownAction onEnd) {
            this.onEnd = onEnd;
            return this;
        }

        public DelayedFactory onTick(CountdownAction onTick) {
            this.onTick = onTick;
            return this;
        }

        public DelayedFactory postPhase(TitlePhase phase) {
            this.post.add(phase);
            return this;
        }


        @Override
        public List<TitlePhase> build() {
            List<TitlePhase> phases = new ArrayList<>();

            for (int i = 0; i < delayTicks.size(); i++) {
                int index = i;
                int ticks = delayTicks.get(i);
                phases.add(TitlePhase.builder()
                  .duration(ticks)
                  .title(state -> title.apply(state, index))
                  .subtitle(state -> subtitle.apply(state, index))
                  .onStart(state -> onStart.accept(state, index))
                  .onEnd(state -> onEnd.accept(state, index))
                  .onTick(state -> onTick.accept(state, index))
                  .build());
            }

            phases.addAll(post);

            return phases;
        }

    }

}
