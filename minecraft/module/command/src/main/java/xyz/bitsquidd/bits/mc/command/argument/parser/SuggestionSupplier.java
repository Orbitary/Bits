/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;

import java.util.List;


@FunctionalInterface
public interface SuggestionSupplier<T> {

    List<String> getSuggestions(BitsCommandContext<T> ctx);

}
