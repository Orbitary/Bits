/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.animation;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import xyz.orbitary.bits.util.math.easing.Easings;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimationTest {
    @Test
    void heldFramesEvaluateOnlyOneCallback() {
        List<String> calls = new ArrayList<>();
        Animation animation = Animation.basic(10)
          .keyframe(0.2f, new AnimationKeyframe.Rotation(data -> {
              calls.add("first");
              return new Quaternionf();
          }), Easings.LINEAR)
          .keyframe(0.7f, new AnimationKeyframe.Rotation(data -> {
              calls.add("last");
              return new Quaternionf();
          }), Easings.LINEAR)
          .build();
        animation.mutate(AnimationPose.identity(), new AnimationData(2));
        animation.mutate(AnimationPose.identity(), new AnimationData(7));
        assertEquals(List.of("first", "last"), calls);
    }

    @Test
    void identicalFrameReferencesStillCrossfadeWithTwoOrderedEvaluations() {
        List<Long> calls = new ArrayList<>();
        AnimationKeyframe frame = new AnimationKeyframe.Rotation(data -> {
            calls.add(data.currentTick());
            return new Quaternionf().rotateY(calls.size());
        });
        Animation animation = Animation.basic(10)
          .keyframe(0, frame, Easings.LINEAR)
          .keyframe(1, frame, Easings.LINEAR)
          .build();
        AnimationPose pose = AnimationPose.identity();
        animation.mutate(pose, new AnimationData(5));
        assertEquals(List.of(5L, 5L), calls);
        assertEquals(new Quaternionf().rotateY(1).slerp(new Quaternionf().rotateY(2), 0.5f), pose.rotation());
    }

    @Test
    void completedPingPongAnimationHoldsTheFinalLoopFrame() {
        Animation animation = Animation.basic(4).loops(2).loop(AnimationLoopMode.PING_PONG)
          .keyframe(0, AnimationKeyframe.Translation.x(0), Easings.LINEAR)
          .keyframe(1, AnimationKeyframe.Translation.x(4), Easings.LINEAR)
          .build();
        assertFalse(animation.isFinished(new AnimationData(7)));
        assertTrue(animation.isFinished(new AnimationData(8)));
        AnimationPose pose = AnimationPose.identity();
        animation.mutate(pose, new AnimationData(100));
        assertEquals(0f, pose.translation().x);
    }

    @Test
    void emptyTimelineStillFailsAtBuildTime() {
        assertThrows(IllegalStateException.class, () -> Animation.basic(10).build());
    }
}
