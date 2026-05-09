/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import xyz.bitsquidd.bits.format.Time;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;

import java.time.Duration;


public abstract class AbstractTitle extends Sendable {


    @Override
    protected TitleConfig.Builder createConfig() {
        return new TitleConfig.Builder()
          .maxTicks(40)
          .fadeIn(5)
          .fadeOut(5);
    }

    @Override
    public final TitleConfig config() {
        return (TitleConfig)super.config();
    }


    public abstract Component title(SendableState state);

    public abstract Component subtitle(SendableState state);

    public final Title.Times getTimes(SendableState state) {
        if (state.tick() < config().fadeInEndTick) {
            // Currently fading in
            return Title.Times.times(Time.FROM_TICKS(config().fadeInDuration), Duration.ofHours(1), Duration.ZERO);
        } else if (state.tick() < config().fadeOutStartTick) {
            // Currently staying (fully visible)
            return Title.Times.times(Duration.ZERO, Duration.ofHours(1), Duration.ZERO);
        } else {
            // Currently fading out
            return Title.Times.times(Duration.ZERO, Duration.ZERO, Time.FROM_TICKS(config().fadeOutDuration));
        }
    }


    @Override
    public final boolean needsRender(SendableState state) {
        // Only render if we're within the active lifecycle of the title.
        return super.needsRender(state) && (state.tick() >= config().fadeInEndTick && state.tick() <= config().fadeOutStartTick);
    }


    // TODO not a massive fan of this being here. Consider repositioning.
    public final void gracefullyExpire(SendableHandle<AbstractTitle> handle) {
        handle.setTick(config().fadeOutStartTick);
    }

}
