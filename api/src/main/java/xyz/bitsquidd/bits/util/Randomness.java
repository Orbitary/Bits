/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;


/**
 * Note: it should be obvious, but don't use this util for security-sensitive purposes (even if the seed is changed, use {@link java.security.SecureRandom} instead).
 */

public final class Randomness {
    private Randomness() {}

    public static final RandomGenerator RANDOM = new SplittableRandom();
    public static final Random SECONDARY_RANDOM = new Random();


    //region Collections

    /**
     * Shuffles a collection and returns a new list with the elements in random order.
     *
     * @since 0.0.13
     */
    public static <T> List<T> shuffle(Collection<T> list) {
        List<T> copied = new ArrayList<>(list);
        Collections.shuffle(copied);
        return copied;
    }

    /**
     * Gets random element in a collection.
     */
    private static <T> T getRandomElement(Collection<T> collection, RandomGenerator random) {
        if (collection.isEmpty()) throw new IllegalArgumentException("Collection is empty, cannot get random element.");

        int index = random.nextInt(collection.size());
        return collection.stream()
          .skip(index)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Collection is empty, cannot get random element."));
    }


    public static <T> T getRandomElement(List<T> collection, int seed) {
        return getRandomElement(collection, new SplittableRandom(seed));
    }

    public static <T> T getRandomElement(Collection<T> collection) {
        return getRandomElement(collection, RANDOM);
    }

    public static <T> T getRandomElement(T[] array) {
        return getRandomElement(Arrays.asList(array), RANDOM);
    }

    public static <T> Optional<T> getRandomElement(Stream<T> stream) {
        List<T> list = stream.toList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(getRandomElement(list));
    }
    //endregion


    //region Seeded
    public static Random getRandom(String... strings) {
        return new Random(getSeed(strings));
    }

    public static Long getSeed(String... strings) {
        long seed = 0;
        for (String str : strings) {
            seed ^= str.hashCode();
        }
        return seed;
    }
    //endregion


    //region Randomness Functions
    @SuppressWarnings("IdentityBinaryExpression")
    public static double triangle(double center, double maxDeviation) {
        return center + maxDeviation * (RANDOM.nextDouble() - RANDOM.nextDouble());
    }
    //endregion


}
