/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import net.minecraft.commands.arguments.selector.EntitySelector;

/**
 * Argument parser for EntitySelector values.
 */
public final class EntitySelectorArgumentParser extends PrimitiveArgumentParser<EntitySelector> {
    public EntitySelectorArgumentParser() {
        super(EntitySelector.class, "EntitySelector");
    }

}
