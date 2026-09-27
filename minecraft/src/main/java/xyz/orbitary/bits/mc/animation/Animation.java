/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.animation;

import org.joml.Quaternionf;

import xyz.orbitary.bits.lifecycle.builder.Buildable;
import xyz.orbitary.bits.util.math.easing.Easing;
import xyz.orbitary.bits.wrapper.collection.pair.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public sealed interface Animation {

    void mutate(AnimationPose pose, AnimationData data);

    boolean isFinished(AnimationData data);

    default Animation and(Animation other) {
        List<Animation> flattened = new ArrayList<>();
        if (this instanceof Compound c) {
            flattened.addAll(c.animations);
        } else {
            flattened.add(this);
        }
        if (other instanceof Compound c) {
            flattened.addAll(c.animations);
        } else {
            flattened.add(other);
        }
        return new Compound(flattened);
    }


    static Basic.Builder basic(long ticks) {
        return new Basic.Builder(ticks);
    }

    static Constant constant(AnimationKeyframe frame) {
        return new Constant(frame);
    }

    static Spin spin(float degreesPerTick) {
        return new Spin(degreesPerTick);
    }


    final class Basic implements Animation {
        private record KeyframeRecord(
          // Keyframe and its proportion
          List<Pair<AnimationKeyframe, Float>> keyframes
        ) {}

        private final List<KeyframeRecord> baked;
        private final long loops;
        private final AnimationLoopMode loopMode;

        private Basic(List<KeyframeRecord> baked, long loops, AnimationLoopMode loopMode) {
            this.baked = baked;
            this.loops = loops;
            this.loopMode = loopMode;
        }


        @Override
        public void mutate(AnimationPose pose, AnimationData data) {
            int size = baked.size();
            long tick = data.currentTick();

            // Once finished: hold on the last frame of the final loop instead of wrapping back to the start.
            if (isFinished(data)) tick = (size * loops) - 1;

            int effectiveIndex = loopMode.effectiveIndex(tick, size);

            KeyframeRecord record = baked.get(effectiveIndex);
            List<Pair<AnimationKeyframe, Float>> keyframes = record.keyframes();

            // Crossfade tick: blend the two active keyframes directly rather than composing two independently-faded values, which is not equivalent for rotation.
            if (keyframes.size() == 2) {
                AnimationKeyframe from = keyframes.get(0).getFirst();
                AnimationKeyframe to = keyframes.get(1).getFirst();
                float proportion = keyframes.get(1).getSecond();
                from.applyBlended(pose, data, to, proportion);
            } else {
                keyframes.forEach(pair -> pair.getFirst().applyTo(pose, data, pair.getSecond()));
            }
        }

        @Override
        public boolean isFinished(AnimationData data) {
            return loops > 0 && data.currentTick() >= (baked.size() * loops);
        }


        public static final class Builder implements Buildable<Basic> {
            private final long ticks;
            private final List<TimelineEntry> entries = new ArrayList<>();

            private long loops = -1;
            private AnimationLoopMode loopMode = AnimationLoopMode.STRAIGHT;

            private Builder(long ticks) {
                this.ticks = ticks;
            }

            private record TimelineEntry(
              long effectiveTick,
              AnimationKeyframe frame,
              Easing easing
            ) {}


            public Builder keyframe(float proportion, AnimationKeyframe frame, Easing easing) {
                entries.add(new TimelineEntry((long)(proportion * ticks), frame, easing));
                return this;
            }

            public Builder loops(long loops) {
                this.loops = loops;
                return this;
            }

            public Builder loop(AnimationLoopMode loopMode) {
                this.loopMode = loopMode;
                return this;
            }


            @Override
            public Basic build() {
                if (entries.isEmpty()) throw new IllegalStateException("Cannot build an empty Animation.Basic");

                List<TimelineEntry> sorted = new ArrayList<>(entries);
                sorted.sort(Comparator.comparingLong(TimelineEntry::effectiveTick));

                TimelineEntry first = sorted.getFirst();
                TimelineEntry last = sorted.getLast();

                List<KeyframeRecord> baked = new ArrayList<>((int)ticks);
                int segment = 0;

                for (long t = 0; t < ticks; t++) {
                    if (t <= first.effectiveTick()) {
                        baked.add(new KeyframeRecord(List.of(Pair.immutable(first.frame(), 1f))));
                        continue;
                    }
                    if (t >= last.effectiveTick()) {
                        baked.add(new KeyframeRecord(List.of(Pair.immutable(last.frame(), 1f))));
                        continue;
                    }

                    while (segment < sorted.size() - 2 && t > sorted.get(segment + 1).effectiveTick()) {
                        segment++;
                    }

                    TimelineEntry from = sorted.get(segment);
                    TimelineEntry to = sorted.get(segment + 1);
                    long span = to.effectiveTick() - from.effectiveTick();
                    float local = span == 0 ? 1f : (t - from.effectiveTick()) / (float)span;
                    float eased = to.easing().progress(local);

                    baked.add(new KeyframeRecord(List.of(
                      Pair.immutable(from.frame(), 1f - eased),
                      Pair.immutable(to.frame(), eased)
                    )));
                }

                return new Basic(baked, loops, loopMode);
            }

        }

    }

    final class Compound implements Animation {
        private final List<? extends Animation> animations;

        private Compound(List<? extends Animation> animations) {
            this.animations = animations;
        }

        @Override
        public void mutate(AnimationPose pose, AnimationData data) {
            for (Animation animation : animations) {
                animation.mutate(pose, data);
            }
        }

        @Override
        public boolean isFinished(AnimationData data) {
            return animations.stream().allMatch(a -> a.isFinished(data));
        }

    }

    final class Constant implements Animation {
        private final AnimationKeyframe frame;

        private Constant(AnimationKeyframe frame) {
            this.frame = frame;
        }

        @Override
        public void mutate(AnimationPose pose, AnimationData data) {
            frame.applyTo(pose, data, 1f);
        }

        @Override
        public boolean isFinished(AnimationData data) {
            return false;
        }

    }

    final class Spin implements Animation {
        private final float degreesPerTick;

        private Spin(float degreesPerTick) {
            this.degreesPerTick = degreesPerTick;
        }

        @Override
        public void mutate(AnimationPose pose, AnimationData data) {
            float degrees = degreesPerTick * data.currentTick();
            pose.rotation().mul(new Quaternionf().rotateY((float)Math.toRadians(degrees)));
        }

        @Override
        public boolean isFinished(AnimationData data) {
            return false;
        }

    }

}
