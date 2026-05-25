package xyz.bitsquidd.bits.util;

import xyz.bitsquidd.bits.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * <b>IMPORTANT: Features in this class must be implemented from first principals - no SERIALIZER etc.
 * It is run on classload. No libraries are available here.</b>
 * <p>
 * Utility helper for accessing system environment variables, properties and resources.
 * Provides methods to require or optionally retrieve values, with automatic
 * deserialization support for complex types.
 * <p>
 * Example usage:
 * <pre>{@code
 * String dbUrl = Resources.Var.require("DATABASE_URL");
 * int port = Resources.Var.require("SERVER_PORT", Integer.class);
 * Optional<String> maybeApiKey = Resources.Var.optional("API_KEY");
 * }</pre>
 *
 * @since 0.0.17
 */
public final class Resources {
    private Resources() {}

    public static final class Var {
        private Var() {}

        /**
         * Optionally retrieves a system property or environment variable as a String.
         *
         * @param key the name of the property or environment variable
         *
         * @return an Optional containing the value if set, or empty if not set
         *
         * @since 0.0.17
         */
        public static Optional<String> optional(String key) {
            String value = System.getenv(key);
            if (value != null) return Optional.of(value);
            value = System.getProperty(key);
            return Optional.ofNullable(value);
        }

        /**
         * Retrieves a system property or environment variable as a String.
         * First checks system properties, then environment variables.
         *
         * @param key the name of the property or environment variable
         *
         * @return the value of the property or environment variable
         *
         * @throws IllegalStateException if the variable is not set
         * @since 0.0.17
         */
        public static String require(String key) throws IllegalStateException {
            return optional(key).orElseThrow(() -> new IllegalStateException("Required env or system property '" + key + "' is not set"));
        }

        /**
         * Retrieves a system property or environment variable and deserializes it to the specified type.
         *
         * @param key   the name of the property or environment variable
         * @param clazz the class to deserialize the value to
         * @param <V>   the type of the deserialized value
         *
         * @return the deserialized value
         *
         * @throws IllegalStateException if the variable is not set or cannot be deserialized
         * @since 0.0.17
         */
        public static <V> V require(String key, Class<V> clazz) {
            String raw = require(key);
            if (clazz == String.class) return clazz.cast(raw);
            if (clazz == Integer.class || clazz == int.class) return clazz.cast(Integer.parseInt(raw));
            if (clazz == Boolean.class || clazz == boolean.class) {
                if (raw.equalsIgnoreCase("true")) return clazz.cast(Boolean.TRUE);
                if (raw.equalsIgnoreCase("false")) return clazz.cast(Boolean.FALSE);
                throw new IllegalStateException("Invalid boolean value for env var '" + key + "': " + raw);
            }
            if (clazz == Long.class || clazz == long.class) return clazz.cast(Long.parseLong(raw));
            if (clazz == Double.class || clazz == double.class) return clazz.cast(Double.parseDouble(raw));
            throw new IllegalStateException("Unsupported type for env var deserialisation: " + clazz.getName());
        }

        /**
         * Optionally retrieves a system property or environment variable and deserializes it to the specified type.
         *
         * @param key   the name of the property or environment variable
         * @param clazz the class to deserialize the value to
         * @param <V>   the type of the deserialized value
         *
         * @return an Optional containing the deserialized value if set and successfully deserialized, or empty otherwise
         *
         * @since 0.0.17
         */
        public static <V> Optional<V> optional(String key, Class<V> clazz) {
            try {
                return Optional.of(require(key, clazz));
            } catch (IllegalStateException e) {
                return Optional.empty();
            }
        }

    }

    /**
     * Reads a resource file from the classpath and returns its non-empty, non-comment lines as a list of strings.
     * <p>
     * Lines starting with '#' are considered comments and ignored. Lines are trimmed of whitespace.
     *
     * @param resourceFileName the name of the resource file to read
     *
     * @return a list of non-empty, non-comment lines from the resource file, or an empty list if the file cannot be read
     *
     * @since 0.0.17
     */
    public static List<String> stringList(String resourceFileName) {
        List<String> lines = new ArrayList<>();
        try (InputStream in = Resources.class.getClassLoader().getResourceAsStream(resourceFileName)) {
            if (in == null) throw new IOException("Resource not found: " + resourceFileName);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            Logger.warn("Failed to read resource file: " + resourceFileName + " - " + e.getMessage());
            return List.of();
        }

        return lines;
    }

}