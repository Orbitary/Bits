/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.exception;

public class BitsException extends RuntimeException {
    public BitsException(String message) {
        super(message);
    }

    public static BitsException INSTANCE_ALREADY_EXISTS(Class<?> clazz) {
        return new BitsException("An instance of '" + clazz + "' already exists.");
    }

    public static BitsException INSTANCE_NOT_FOUND(Class<?> clazz) {
        return new BitsException("No instance found of '" + clazz + "'.");
    }


}
