/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.permission;

import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.config.BitsMinecraft;


/**
 * Represents a unique permission node used for access control within the Bits library.
 * <p>
 * Permissions are hierarchical strings (e.g., {@code bits.command.admin}) that can be
 * checked against an {@link net.kyori.adventure.audience.Audience} to determine
 * if they possess the required authorization.
 *
 * @since 0.0.10
 */
public final class Permission {
    private final String value;
    private final String description;


    private Permission(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static Permission of(String name) {
        return new Permission(name, "");
    }

    public static Permission of(String name, String description) {
        return new Permission(name, description);
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Permission that = (Permission)obj;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Appends a child node to this permission string.
     *
     * @param suffix the suffix to append (e.g., ".subnode")
     *
     * @since 0.0.10
     */
    public Permission append(String suffix) {
        return Permission.of(value + suffix);
    }

    /**
     * Returns a permission that represents the "root" or "all" permission bypass.
     *
     * @since 0.0.10
     */
    public static Permission all() {
        return new Permission("", "Grants all permissions");
    }

    /**
     * Checks if the specified audience possess this permission.
     *
     * @param audience the audience to check
     *
     * @return true if the audience has the permission, false otherwise
     *
     * @since 0.0.10
     */
    public boolean hasPermission(Audience audience) {
        return value.isEmpty() || BitsMinecraft.get().hasPermission(audience, this);
    }

    /**
     * Registers this permission with the underlying platform's permission registry.
     *
     * @since 0.0.10
     */
    public void register() {
        BitsMinecraft.get().registerPermission(this);
    }

}
