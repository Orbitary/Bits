/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;


public abstract class AbstractTitle extends Sendable {


    @Override
    protected abstract TitleSendableConfig createConfig();

    @Override
    public TitleSendableConfig config() {
        return (TitleSendableConfig)super.config();
    }


    public abstract Component title(Receiver receiver, SendableState state);

    public abstract Component subTitle(Receiver receiver, SendableState state);


}
