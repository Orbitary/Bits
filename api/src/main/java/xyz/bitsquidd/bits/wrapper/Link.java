/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


/**
 * A wrapper for string representations of URLs or URIs.
 * <p>
 * This is primarily used as a distinct type for command argument parsing.
 *
 * @since 0.0.10
 */
public final class Link {
    public final String value;

    private Link(String value) {
        this.value = value;
    }

    public static Link of(String value) {
        return new Link(value);
    }

    /**
     * Converts the wrapped string value into a {@link URL}.
     *
     * @return the parsed URL
     *
     * @throws MalformedURLException if the string is not a valid URL
     * @since 0.0.10
     */
    public URL toURL() throws MalformedURLException {
        return URI.create(value).toURL();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Link other) && other.value.equals(this.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
