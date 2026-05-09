/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry managing internal mappings for {@link BitsCommandRequirement} instances.
 * <p>
 * This handles requirement retrieval for commands instantiated with particular requirement classes.
 *
 * @param <T> the base requirement type
 *
 * @since 0.0.10
 */
public class BitsRequirementRegistry<T> {
    private final Map<Class<? extends BitsCommandRequirement>, BitsCommandRequirement> requirementInstances = new HashMap<>();

    public BitsRequirementRegistry() {
        Map<Class<? extends BitsCommandRequirement>, BitsCommandRequirement> initialRequirements = new HashMap<>(initialiseParsers());
        requirementInstances.putAll(initialRequirements);
    }

    /**
     * Provides the initial map of requirements to register upon instantiation.
     *
     * @return a map of initial requirement classes to instances
     *
     * @since 0.0.10
     */
    protected Map<Class<? extends BitsCommandRequirement>, BitsCommandRequirement> initialiseParsers() {
        return new HashMap<>();
    }

    /**
     * Retrieves a registered requirement instance by its class.
     *
     * @param requirementClass the class of the requirement to retrieve
     *
     * @return the corresponding requirement instance
     *
     * @throws IllegalArgumentException if no such requirement is registered
     * @since 0.0.10
     */
    public BitsCommandRequirement getRequirement(Class<? extends BitsCommandRequirement> requirementClass) {
        BitsCommandRequirement requirement = requirementInstances.get(requirementClass);
        if (requirement == null) throw new IllegalArgumentException("No requirement registered for class: " + requirementClass.getName());
        return requirement;
    }

}