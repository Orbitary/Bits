/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */
package xyz.orbitary.bits.fabric;

import net.fabricmc.api.ModInitializer;
import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.ServerBitsFabric;

public class BitsServerInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerBitsFabric.generic();
        Bits.get().startup();
        Bits.get().initialise();
    }

}
