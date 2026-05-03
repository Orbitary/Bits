/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.Link;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;


//TODO add better validation for this, enum for a type? WWW. HTTPS:// etc.?

/**
 * Argument parser designed to structure command inputs into safe {@link Link} representations.
 * <p>
 * This automatically suggests prefixes like {@code https://} to the command sender.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class LinkArgumentParser extends AbstractArgumentParser<Link> {
    public LinkArgumentParser() {
        super(TypeSignature.of(Link.class), "URL");
    }

    @Override
    public Link parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);
        return Link.of(inputString);
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        return () -> Stream.of("https://", "http://", "www.").toList();
    }

}
