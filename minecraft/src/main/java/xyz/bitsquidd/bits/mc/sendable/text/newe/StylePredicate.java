/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.text.newe;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;


@FunctionalInterface
public interface StylePredicate {

    boolean test(Style style);


    static StylePredicate of(Predicate<Style> predicate) {
        return predicate::test;
    }


    static StylePredicate hasAnyDecoration(Collection<TextDecoration> decorations) {
        return style -> decorations.stream().anyMatch(style::hasDecoration);
    }

    static StylePredicate hasAnyDecoration() {
        return hasAnyDecoration(Set.of(TextDecoration.values()));
    }

    static StylePredicate hasAllDecorations(Collection<TextDecoration> decorations) {
        return style -> decorations.stream().allMatch(style::hasDecoration);
    }

    static StylePredicate hasAllDecorations() {
        return hasAllDecorations(Set.of(TextDecoration.values()));
    }


    static StylePredicate alwaysTrue() {
        return style -> true;
    }

    static StylePredicate alwaysFalse() {
        return style -> false;
    }

}
