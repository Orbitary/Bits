/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.manager;

import xyz.bitsquidd.bits.util.Safety;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * A container that orchestrates the lifecycle of multiple {@link CoreManager} instances.
 * <p>
 * This class allows for bulk registration of managers and ensures that lifecycle
 * events (startup, initialise, etc.) are propagated to all registered members
 * in the order they were added.
 *
 * @since 0.0.10
 */
public abstract class ManagerContainer implements CoreManager {
    private final Set<CoreManager> managers = new LinkedHashSet<>();

    /**
     * Registers a new manager to be handled by this container.
     *
     * @param <T>     the specific type of manager
     * @param manager the manager instance to register
     *
     * @return the registered manager instance
     *
     * @since 0.0.10
     */
    protected final <T extends CoreManager> T registerManager(T manager) {
        managers.add(manager);
        return manager;
    }

    protected final void registerManagers(Iterable<? extends CoreManager> managers) {
        managers.forEach(this::registerManager);
    }


    /**
     * Returns a read-only list of all currently registered managers.
     *
     * @return the list of managers
     *
     * @since 0.0.10
     */
    public final List<CoreManager> getAllManagers() {
        return List.copyOf(managers);
    }

    /**
     * Retrieves a manager of the specified type, if present.
     *
     * @param <T>          the specific type of manager
     * @param managerClass the class object representing the manager type
     *
     * @return an optional containing the manager if found, or empty if not found
     *
     * @since 0.0.14
     */
    public final <T extends CoreManager> Optional<T> getManager(Class<T> managerClass) {
        return getAllManagers().stream()
          .filter(managerClass::isInstance)
          .map(managerClass::cast)
          .findFirst();
    }


    @Override
    public void startup() {
        getAllManagers().forEach(this::startupManager);
    }

    protected void startupManager(CoreManager manager) {
        Safety.safeExecute(manager.getClass().getSimpleName(), manager::startup);
    }

    @Override
    public void initialise() {
        getAllManagers().forEach(this::initialiseManager);
    }

    protected void initialiseManager(CoreManager manager) {
        Safety.safeExecute(manager.getClass().getSimpleName(), manager::initialise);
    }

    @Override
    public void cleanup() {
        getAllManagers().forEach(this::cleanupManager);
    }

    protected void cleanupManager(CoreManager manager) {
        Safety.safeExecute(manager.getClass().getSimpleName(), manager::cleanup);
    }

    @Override
    public void shutdown() {
        getAllManagers().forEach(this::shutdownManager);
    }

    protected void shutdownManager(CoreManager manager) {
        Safety.safeExecute(manager.getClass().getSimpleName(), manager::shutdown);
    }

}
