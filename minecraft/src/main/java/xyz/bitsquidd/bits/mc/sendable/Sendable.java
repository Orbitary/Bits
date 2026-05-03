/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.config.BitsMinecraft;
import xyz.bitsquidd.bits.mc.permission.Permission;

import java.util.Collection;
import java.util.function.Predicate;


/**
 * Represents an object that can be dispatched to one or more {@link Audience} targets.
 *
 * @since 0.0.10
 */
public interface Sendable {

    /**
     * Sends this object to the specified target.
     *
     * @param target the audience to receive this object
     *
     * @since 0.0.10
     */
    void send(Audience target);

    /**
     * Sends this object to all audiences in the global pool that match the given predicate.
     *
     * @param predicate the condition to filter audiences
     *
     * @since 0.0.10
     */
    default void sendAll(Predicate<Audience> predicate) {
        BitsMinecraft.get().getAll().forEachAudience(audience -> {
            if (predicate.test(audience)) send(audience);
        });
    }

    /**
     * Sends this object to all audiences that hold the specified permission.
     *
     * @param permission the permission required to receive this object
     *
     * @since 0.0.10
     */
    default void sendAll(Permission permission) {
        sendAll(permission::hasPermission);
    }

    /**
     * Sends this object to a collection of target audiences.
     *
     * @param targets the collection of audiences to receive this object
     *
     * @since 0.0.10
     */
    default void sendAll(Collection<? extends Audience> targets) {
        targets.forEach(this::send);
    }

}
