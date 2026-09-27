/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.provider;

import xyz.orbitary.bits.mc.command.BitsCommand;
import xyz.orbitary.bits.mc.command.argument.parser.ArgumentParser;
import xyz.orbitary.bits.wrapper.collection.AddableSet;


public interface BitsCommandProvider {
    default AddableSet<BitsCommand> getCommands() {
        return AddableSet.empty();
    }

    default AddableSet<ArgumentParser<?, ?>> getArguments() {
        return AddableSet.empty();
    }

}