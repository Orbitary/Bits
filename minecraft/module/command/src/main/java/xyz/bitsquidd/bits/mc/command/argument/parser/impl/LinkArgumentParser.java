/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.BasicArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.Link;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;


//TODO add better validation for this, enum for a type? WWW. HTTPS:// etc.?

/**
 * Argument parser designed to structure command inputs into safe {@link Link} representations.
 * <p>
 * This automatically suggests prefixes like {@code https://} to the command sender.
 *
 * @since 0.0.10
 */

public final class LinkArgumentParser extends BasicArgumentParser<Link> {
    public LinkArgumentParser() {
        super(TypeSignature.of(Link.class), "URL");
    }

    @Override
    public Link parse(String data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return Link.of(data);
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return ctx -> List.of("https://", "http://", "www.");
    }

}
