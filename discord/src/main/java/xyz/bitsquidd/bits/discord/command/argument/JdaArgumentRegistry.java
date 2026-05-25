/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.argument;

import xyz.bitsquidd.bits.discord.command.argument.impl.*;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry mapping Java types to {@link JdaArgumentParser} instances.
 */
public class JdaArgumentRegistry {

    private final Map<Class<?>, JdaArgumentParser<?>> parsers = new HashMap<>();

    public JdaArgumentRegistry() {
        scanCustomParsers();
    }

    private void scanCustomParsers() {
        ReflectionUtils.General.createClassesInDir("*", JdaArgumentParser.class, ScannerFlags.DEFAULT)
            .forEach(p -> {
                if (parsers.containsKey(p.type())) return;
                parsers.put(p.type(), p);
                Logger.info("Registered custom argument parser: " + p.getClass().getSimpleName());
            });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> JdaArgumentParser<T> getParser(Class<T> type) {
        JdaArgumentParser<?> parser = parsers.get(type);

        if (parser == null) {
            if (type.isEnum())return (JdaArgumentParser<T>) new GenericEnumParser<>((Class<? extends Enum>) type);
            throw new IllegalArgumentException("No JdaArgumentParser registered for type: " + type.getName());
        }

        return (JdaArgumentParser<T>) parser;
    }

}
