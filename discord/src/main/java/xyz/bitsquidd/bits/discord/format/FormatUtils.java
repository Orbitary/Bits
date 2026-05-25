/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.format;

import net.dv8tion.jda.api.entities.IMentionable;


public final class FormatUtils {
    private FormatUtils() {}

    public static String mention(long userId) {
        return "<@" + userId + ">";
    }

    public static String mentionRole(long roleId) {
        return "<@&" + roleId + ">";
    }

    public static String mentionChannel(long channelId) {
        return "<#" + channelId + ">";
    }

    /**
     * Convenience overload - accepts any {@link IMentionable} (User, Member, Role, Channel, etc.).
     */
    public static String mention(IMentionable mentionable) {
        return mentionable.getAsMention();
    }


    public static String bold(String text) {
        return "**" + text + "**";
    }

    public static String italic(String text) {
        return "*" + text + "*";
    }

    public static String underline(String text) {
        return "__" + text + "__";
    }

    public static String strikethrough(String text) {
        return "~~" + text + "~~";
    }

    public static String spoiler(String text) {
        return "||" + text + "||";
    }

    public static String code(String text) {
        return "`" + text + "`";
    }

    public static String codeBlock(String text) {
        return "```\n" + text + "\n```";
    }

    public static String codeBlock(String language, String text) {
        return "```" + language + "\n" + text + "\n```";
    }

}
