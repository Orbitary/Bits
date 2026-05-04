/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;


import com.google.common.util.concurrent.Runnables;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.mc.sendable.impl.AbstractSendable;
import xyz.bitsquidd.bits.util.core.LockHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * A manager for sendables that handles per-player and global sendables.
 * Developer Note: Sendable updates should be optimised within sendable implementations - the full collection is sent by default.
 * <p>
 * Required Functionality To Add:<ul>
 * <li>add(BiomePlayer, S, D)</li>
 * <li>addGlobal(S, D)</li>
 * </ul>
 */
public abstract class AbstractSendableManager<S extends AbstractSendable, C extends AbstractSendableCollection<S>> implements CoreManager {
    protected record Flags(
      // Whether global sendables should be stacked before or after player sendables.
      boolean globalsBeforePlayer,
      int keepAlive
    ) {
        public static Flags defaults() {
            return new Flags(true, -1);
        }

        public Flags withGlobalsBeforePlayer(boolean globalsBeforePlayer) {
            return new Flags(globalsBeforePlayer, keepAlive);
        }

        public Flags withKeepAlive(int keepAlive) {
            return new Flags(globalsBeforePlayer, keepAlive);
        }

    }

    private final C globals;
    private final Map<UUID, C> sendables = new ConcurrentHashMap<>();
    protected final Supplier<C> collectionSupplier;
    protected final Flags flags;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private @Nullable BukkitTask ticker;
    private final AtomicInteger globalTick = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    protected AbstractSendableManager(Supplier<C> collectionSupplier, Flags flags) {
        this.collectionSupplier = collectionSupplier;
        this.globals = collectionSupplier.get();
        this.flags = flags;
    }


    @Override
    public void startup() {
        ticker = Runnables.buildTimer(
            () -> {
                if (!paused.get()) tick();
            }, 1, 1
          )
          .async().forced().run();
    }

    @Override
    public void cleanup() {
        clearAll();
    }

    @Override
    public void shutdown() {
        ticker = Runnables.cleanup(ticker);
        clearAll();
    }

    private void tick() {
        // Getall safe outside lock
        // We call getall before updating, we may remove sendables during the update phase. This ensures we dont tick sendables that havent been sent.
        Set<S> all = getAll(s -> true);
        all.forEach(AbstractSendable::tick);
        all.stream()
          .filter(AbstractSendable::needsExpire)
          .forEach(this::expire);

        final boolean keepAlive = flags.keepAlive() > 0 && globalTick.get() % flags.keepAlive() == 0;

        // Snapshot data under lock
        LockHelper.read(
          lock, () -> BiomePlayer.online().forEach(player -> {
              UUID uuid = player.getUniqueId();
              C sendables = getCombinedSendablesUnsafe(uuid);
              if (sendables.needsUpdate() || keepAlive) {
                  // IMPORTANT:
                  // Ensure we dont require any writing on update player display.
                  // All writing should be performed on the tick().
                  updatePlayerDisplay(player, sendables);
                  sendables.getAll().forEach(s -> s.setNeedsUpdate(false));
              }
          })
        );

        sendables.values().forEach(collection -> collection.setNeedsForcedUpdate(false));
        globals.setNeedsForcedUpdate(false);

        globalTick.incrementAndGet();
    }

    // Important: this method assumes the lock is already held!
    private C getCombinedSendablesUnsafe(UUID uuid) {
        C merged = collectionSupplier.get();
        // Usage of merge() is "techincally" unsafe.
        // However, as we carefully control the collections, this should be fine.

        if (flags.globalsBeforePlayer()) {
            merged.merge(globals);
            merged.merge(sendables.computeIfAbsent(uuid, k -> collectionSupplier.get()));
        } else {
            merged.merge(sendables.computeIfAbsent(uuid, k -> collectionSupplier.get()));
            merged.merge(globals);
        }

        return merged;
    }


    public void clearAll() {
        LockHelper.write(
          lock, () -> {
              sendables.forEach((uuid, collection) -> remove(uuid, s -> true));
              globals.clear();
          }
        );
    }


    //region Required Functionality
    public final Set<S> getAll(SendableFilter<S> filter) {
        return LockHelper.read(
          lock, () -> {
              Set<S> all = sendables.values().stream()
                .flatMap(collection -> collection.getAll(filter).stream())
                .collect(Collectors.toSet());
              all.addAll(globals.getAll(filter));
              return all;
          }
        );
    }

    public final void remove(UUID uuid, SendableFilter<S> filter) {
        LockHelper.write(
          lock, () -> {
              List<S> removed = getPlayerCollection(uuid).getAll(filter);
              removed.forEach(sendable -> onRemove(uuid, sendable, getPlayerCollection(uuid)));
              getPlayerCollection(uuid).remove(filter);
          }
        );
    }

    public final void removeGlobal(SendableFilter<S> filter) {
        LockHelper.write(
          lock, () -> {
              List<S> removed = globals.getAll(filter);
              BiomePlayer.online().forEach(player -> removed.forEach(sendable -> onRemove(player.getUniqueId(), sendable, globals)));
              globals.remove(filter);
          }
        );
    }

    public final List<S> get(UUID uuid, SendableFilter<S> filter) {
        // Note: We dont return data here.
        return LockHelper.read(
          lock, () -> getPlayerCollection(uuid).getAll(filter)
        );
    }

    public final List<S> getGlobals(SendableFilter<S> filter) {
        return LockHelper.read(
          lock, () -> globals.getAll(filter)
        );
    }


    public final void expire(S sendable) {
        removeGlobal(s -> s == sendable);
        sendables.forEach((uuid, collection) -> remove(uuid, s -> s == sendable));
    }


    //region Update Hooks
    protected void onRemove(UUID uuid, S sendable, C collection) {
        BiomePlayer.of(uuid).ifPresent(sendable::onRemove);
        onUpdate(uuid);
    }

    protected void onAdd(UUID uuid, S sendable) {
        BiomePlayer.of(uuid).ifPresent(sendable::onAdd);
        onUpdate(uuid);
    }

    private void onUpdate(UUID uuid) {
        List<S> all = LockHelper.read(lock, () -> getCombinedSendablesUnsafe(uuid).getAll());
        if (all.isEmpty()) BiomePlayer.of(uuid).ifPresent(this::onCollectionEmpty);
    }
    //endregion
    //endregion


    //region Sendable Getters
    protected final C getPlayerCollection(UUID uuid) {
        return LockHelper.read(
          lock, () -> sendables.computeIfAbsent(uuid, k -> collectionSupplier.get())
        );
    }

    protected final C getGlobalCollection() {
        return LockHelper.read(
          lock, () -> globals
        );
    }
    //endregion


    //region Update Functionality

    /**
     * Requset an update to a player's display.
     */
    protected abstract void updatePlayerDisplay(BiomePlayer player, C sendables);

    protected void onCollectionEmpty(BiomePlayer player) {
        // Override to handle empty state
    }
    //endregion


    //region Initialisation & Cleanup
    protected void initialisePlayer(UUID uuid) {
        LockHelper.write(
          lock, () -> {
              sendables.putIfAbsent(uuid, collectionSupplier.get());
          }
        );
    }

    protected void cleanupPlayer(UUID uuid) {
        LockHelper.write(
          lock, () -> {
              sendables.remove(uuid);
          }
        );
    }


    @EventHandler
    public final void onPlayerJoin(BiomePlayerJoinEvent event) {
        initialisePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public final void onPlayerCleanup(BiomePlayerQuitEvent event) {
        cleanupPlayer(event.getPlayer().getUniqueId());
    }
    //endregion

}
