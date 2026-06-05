/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.text.newe;

import net.kyori.adventure.text.Component;


@FunctionalInterface
public interface StyleTransformation {
    Component transform(Component root);

    static StyleTransformation prefix(Component prefix) {
        return root -> Component.empty().append(prefix).append(root);
    }

    static StyleTransformation suffix(Component suffix) {
        return root -> Component.empty().append(root).append(suffix);
    }

}