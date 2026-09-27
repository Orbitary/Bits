/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import org.jetbrains.annotations.Range;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * Defines how an animation should play.
 *
 * @since 0.0.13
 */
public enum AnimationLoopMode {
    // Loops back to the start
    STRAIGHT(
      progress -> progress % 1,
      (tick, size) -> (int)(tick % size)
    ),
    // Reverses direction each cycle
    PING_PONG(
      progress -> {
          int cycle = (int)((float)progress);
          float cycleProgress = progress - cycle;
          if (cycle % 2 == 1) {
              return 1 - cycleProgress;
          } else {
              return cycleProgress;
          }
      },
      (tick, size) -> {
          long cycle = tick / size;
          int cycleTick = (int)(tick % size);
          return (cycle % 2 == 1) ? size - 1 - cycleTick : cycleTick;
      }
    ),
    ;

    private final Function<Float, Float> progressTransformer;
    private final BiFunction<Long, Integer, Integer> indexTransformer;

    AnimationLoopMode(Function<Float, Float> progressTransformer, BiFunction<Long, Integer, Integer> indexTransformer) {
        this.progressTransformer = progressTransformer;
        this.indexTransformer = indexTransformer;
    }

    public final @Range(from = 0, to = 1) float transform(@Range(from = 0, to = 1) float progress) {
        return progressTransformer.apply(progress);
    }

    public final int effectiveIndex(long tick, int size) {
        return indexTransformer.apply(tick, size);
    }

}