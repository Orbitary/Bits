/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.text.newe;


import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import net.kyori.adventure.text.minimessage.MiniMessage;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class TextStyle {
    private final ImmutableList<StyleModule> modules;
    private final ImmutableList<StyleTransformation> transformations;

    private TextStyle(Builder builder) {
        this.modules = ImmutableList.copyOf(builder.modules);
        this.transformations = ImmutableList.copyOf(builder.transformations);
    }


    public Component apply(Component root) {
        Component result = applyDeep(root, Collections.emptySet(), Style.empty());
        for (StyleTransformation t : transformations) {
            result = t.transform(result);
        }
        return result;
    }

    public Component apply(String raw) {
        return apply(MiniMessage.miniMessage().deserialize(raw));
    }

    private Component applyDeep(Component c, Set<Integer> covered, Style inherited) {
        Style effective = c.style().merge(inherited); // merge: c's explicit wins, inherited fills any NOT_SET gaps

        Component result = c;
        Set<Integer> childCovered = new HashSet<>(covered);

        for (int i = 0; i < modules.size(); i++) {
            if (covered.contains(i)) continue;
            StyleModule module = modules.get(i);
            if (module.predicate().test(effective)) {
                result = module.applier().transform(result);
                childCovered.add(i);
            }
        }

        return result.children(c.children().stream()
          .map(child -> applyDeep(child, childCovered, effective))
          .toList());
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<TextStyle> {
        private final List<StyleModule> modules = new ArrayList<>();
        private final List<StyleTransformation> transformations = new ArrayList<>();

        private Builder() {}


        public Builder module(StyleModule module) {
            modules.add(module);
            return this;
        }

        public Builder module(StyleApplier applier) {
            return module(new StyleModule(StylePredicate.alwaysTrue(), applier));
        }

        public Builder module(StylePredicate predicate, StyleApplier applier) {
            return module(new StyleModule(predicate, applier));
        }


        public Builder transform(StyleTransformation transformation) {
            transformations.add(transformation);
            return this;
        }

        @Override
        public TextStyle build() {
            return new TextStyle(this);
        }

    }


}
