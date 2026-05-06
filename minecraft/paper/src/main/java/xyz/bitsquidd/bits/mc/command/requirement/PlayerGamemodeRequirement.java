/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement;

import org.bukkit.GameMode;

import xyz.bitsquidd.bits.mc.command.PaperBitsCommandSourceContext;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;


@Deprecated(forRemoval = true) // Not working!
public class PlayerGamemodeRequirement extends PlayerSenderRequirement {
    public static final PlayerGamemodeRequirement CREATIVE = new PlayerGamemodeRequirement(GameMode.CREATIVE);
    public static final PlayerGamemodeRequirement SURVIVAL = new PlayerGamemodeRequirement(GameMode.SURVIVAL);
    public static final PlayerGamemodeRequirement ADVENTURE = new PlayerGamemodeRequirement(GameMode.ADVENTURE);
    public static final PlayerGamemodeRequirement SPECTATOR = new PlayerGamemodeRequirement(GameMode.SPECTATOR);

    private final GameMode requiredGamemode;

    private PlayerGamemodeRequirement(GameMode requiredGamemode) {
        super();
        this.requiredGamemode = requiredGamemode;
    }

    @Override
    public boolean test(BitsCommandSourceContext<?> ctx) {
        return super.test(ctx) && ((PaperBitsCommandSourceContext)(ctx)).requirePlayer().getGameMode().equals(requiredGamemode);
    }

}