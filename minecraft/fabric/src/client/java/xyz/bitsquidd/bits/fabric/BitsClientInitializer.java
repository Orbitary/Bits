/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.LoggerFactory;

import xyz.bitsquidd.bits.ClientBitsFabric;


@Environment(EnvType.CLIENT)
public class BitsClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new ClientBitsFabric(LoggerFactory.getLogger("Bits")).startup();
    }

}
