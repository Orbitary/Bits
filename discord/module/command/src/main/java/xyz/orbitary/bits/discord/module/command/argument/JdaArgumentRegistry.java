/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command.argument;

import xyz.orbitary.bits.discord.module.command.argument.impl.AttachmentParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.BooleanParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.ChannelParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.DoubleParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.GenericEnumParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.IntegerParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.LongParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.MemberParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.RoleParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.StringParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.UserParser;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Maps Java types to the {@link JdaArgumentParser} that converts them. Primitive types use their wrapper's parser.
 *
 * @since 0.1.6
 */
public final class JdaArgumentRegistry {

    private final Map<Class<?>, JdaArgumentParser<?>> parsers = new ConcurrentHashMap<>();

    /**
     * @return a registry with the parsers for the built-in Discord option types
     *
     * @since 0.1.6
     */
    public static JdaArgumentRegistry withDefaults() {
        JdaArgumentRegistry registry = new JdaArgumentRegistry();
        registry.register(new StringParser());
        registry.register(new BooleanParser());
        registry.register(new IntegerParser());
        registry.register(new LongParser());
        registry.register(new DoubleParser());
        registry.register(new AttachmentParser());
        registry.register(new ChannelParser());
        registry.register(new MemberParser());
        registry.register(new RoleParser());
        registry.register(new UserParser());
        return registry;
    }

    /**
     * Registers the parser for {@link JdaArgumentParser#type()}, replacing any existing one.
     *
     * @param parser the parser to register
     *
     * @since 0.1.6
     */
    public void register(JdaArgumentParser<?> parser) {
        parsers.put(parser.type(), parser);
    }

    /**
     * Returns the parser for a type. Enum types without a registered parser get a {@link GenericEnumParser}.
     *
     * @param type the Java type, primitive or wrapper
     *
     * @return the parser for the type
     *
     * @throws IllegalArgumentException if no parser handles the type
     *
     * @since 0.1.6
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JdaArgumentParser<?> parser(Class<?> type) {
        Class<?> key = ReflectionUtils.Primitive.fromOrSelf(type);
        JdaArgumentParser<?> parser = parsers.get(key);

        if (parser == null && key.isEnum()) {
            parser = parsers.computeIfAbsent(key, k -> new GenericEnumParser((Class<? extends Enum>) k));
        }
        if (parser != null) return parser;
        throw new IllegalArgumentException("No JdaArgumentParser registered for type: " + type.getName());
    }

}
