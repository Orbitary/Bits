/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.mc.world.Vec3;
import xyz.orbitary.bits.mc.world.volume.Volume;
import xyz.orbitary.bits.paper.util.listener.Listeners;
import xyz.orbitary.bits.paper.util.runnable.Runnables;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public final class EnterableRegion implements Listener {
    private final World world;
    private final Volume volume;
    private final Handler handler;
    private final Set<UUID> playersInside = new HashSet<>();

    private boolean paused;
    private @Nullable BukkitTask ticker;


    public EnterableRegion(World world, Volume volume, Handler handler) {
        this.world = world;
        this.volume = volume;
        this.handler = handler;
    }

    public EnterableRegion(World world, Volume volume) {
        this(world, volume, Handler.NONE);
    }


    //region Lifecycle
    public void spawn() {
        Listeners.register(this);
        ticker = Runnables.timer(this::tick, 0L, 1L);
        handler.onSpawn();
    }

    public void despawn() {
        Listeners.unregister(this);
        ticker = Runnables.cleanup(ticker);
        handler.onDespawn();
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }
    //endregion


    //region Presence
    private void tick() {
        if (!paused) detectPresence();
        handler.onTick();
    }

    private void detectPresence() {
        Set<UUID> current = new HashSet<>();
        for (Player player : world.getPlayers()) {
            if (handler.canEnter(player) && contains(Objects.requireNonNull(player.getLocation()))) current.add(player.getUniqueId());
        }

        Set<UUID> exited = new HashSet<>(playersInside);
        exited.removeAll(current);

        Set<UUID> entered = new HashSet<>(current);
        entered.removeAll(playersInside);

        playersInside.clear();
        playersInside.addAll(current);

        exited.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(handler::onExit));
        entered.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(handler::onEnter));
    }

    public void kickOutAllPlayers() {
        playersInside.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(handler::onExit));
        playersInside.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playersInside.remove(player.getUniqueId())) handler.onExit(player);
    }

    public boolean contains(Location location) {
        if (!world.equals(location.getWorld())) return false;
        return volume.contains(new Vec3(location.getX(), location.getY(), location.getZ()));
    }

    public Set<UUID> getPlayersInside() {
        return Set.copyOf(playersInside);
    }

    public World world() {
        return world;
    }

    public Volume volume() {
        return volume;
    }

    //endregion


    /**
     * Callbacks for an {@link EnterableRegion}. Every method has a no-op default, so implement only what you need.
     */
    public interface Handler {
        Handler NONE = new Handler() {};

        default boolean canEnter(Player player) {
            return true;
        }

        default void onEnter(Player player) {}

        default void onExit(Player player) {}

        default void onTick() {}

        /**
         * Called at the end of {@link EnterableRegion#spawn()}, once tracking has started.
         */
        default void onSpawn() {}

        /**
         * Called at the end of {@link EnterableRegion#despawn()}, once tracking has stopped.
         */
        default void onDespawn() {}

    }

}
