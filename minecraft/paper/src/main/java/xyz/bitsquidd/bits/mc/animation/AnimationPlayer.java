/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.util.function.Consumer;


public final class AnimationPlayer<A extends Animatable> {
    private final Animation animation;

    private AnimationPose basePose = AnimationPose.identity();
    private long currentTick = 0;
    private @Nullable Consumer<AnimationPlayer<A>> onComplete;
    private @Nullable BukkitTask ticker;

    public AnimationPlayer(Animation animation) {
        this.animation = animation;
    }

    public AnimationPlayer<A> basePose(AnimationPose basePose) {
        this.basePose = basePose;
        return this;
    }

    public void play(A animatable) {
        ticker = Runnables.cleanup(ticker);
        currentTick = 0;
        ticker = Runnables.timer(() -> tick(animatable, currentTick++), 0, 1);
    }

    public void stop() {
        ticker = Runnables.cleanup(ticker);
        currentTick = 0;
        if (onComplete != null) onComplete.accept(this);
    }

    public AnimationPlayer<A> onComplete(Consumer<AnimationPlayer<A>> callback) {
        this.onComplete = callback;
        return this;
    }

    public void tick(A animatable, long tick) {
        AnimationData data = new AnimationData(tick);

        if (animation.isFinished(data)) {
            stop();
            return;
        }

        AnimationPose pose = new AnimationPose(
          new Vector3f(basePose.translation()),
          new Quaternionf(basePose.rotation()),
          new Vector3f(basePose.scale())
        );
        animation.mutate(pose, data);
        animatable.applyPose(pose);
    }

}
