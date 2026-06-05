/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.text.newe;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;


@FunctionalInterface
public interface StyleApplier {

    Component transform(Component input);


    //region Atomic
    static StyleApplier color(TextColor color) {
        return input -> input.colorIfAbsent(color);
    }

    static StyleApplier shadowColor(ShadowColor color) {
        return input -> input.shadowColorIfAbsent(color);
    }


    static StyleApplier obfuscated() {
        return input -> input.decorationIfAbsent(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE);
    }

    static StyleApplier bold() {
        return input -> input.decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.TRUE);
    }

    static StyleApplier strikethrough() {
        return input -> input.decorationIfAbsent(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE);
    }

    static StyleApplier underlined() {
        return input -> input.decorationIfAbsent(TextDecoration.UNDERLINED, TextDecoration.State.TRUE);
    }

    static StyleApplier italic() {
        return input -> input.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.TRUE);
    }
    //endregion

}
