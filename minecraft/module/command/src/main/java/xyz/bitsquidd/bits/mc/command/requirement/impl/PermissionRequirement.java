/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement.impl;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;
import xyz.bitsquidd.bits.mc.permission.Permission;
import xyz.bitsquidd.bits.mc.sendable.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A command requirement that checks if the command sender has the specified permissions.
 * <p>
 * <b>Developer Note:</b> Although this can't be directly instantiated via annotation, it is
 * still useful to have a constructor for manual requirement registration.
 *
 * @since 0.0.10
 */
public class PermissionRequirement extends BitsCommandRequirement {
    public final List<Permission> permissions = new ArrayList<>();

    /**
     * @param permissions the collection of required permissions
     *
     * @since 0.0.10
     */
    protected PermissionRequirement(Collection<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    /**
     * Creates a new permission requirement from a collection of permissions.
     *
     * @param permissions the collection of permissions
     *
     * @return the new permission requirement
     *
     * @since 0.0.10
     */
    public static PermissionRequirement of(Collection<Permission> permissions) {
        return new PermissionRequirement(permissions);
    }

    /**
     * Creates a new permission requirement from an array of permissions.
     *
     * @param permissions the array of permissions
     *
     * @return the new permission requirement
     *
     * @since 0.0.10
     */
    public static PermissionRequirement of(Permission... permissions) {
        return new PermissionRequirement(List.of(permissions));
    }


    @Override
    public boolean test(BitsCommandSourceContext<?> ctx) {
        return permissions.stream().allMatch(permission -> permission.hasPermission(ctx.getSender()));
    }

    @Override
    public @Nullable Text getFailureMessage(BitsCommandSourceContext<?> ctx) {
        return Text.of(Component.text("You are lacking permissions to use this command."));
    }

}