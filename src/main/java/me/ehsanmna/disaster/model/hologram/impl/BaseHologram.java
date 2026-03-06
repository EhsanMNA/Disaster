package me.ehsanmna.disaster.model.hologram.impl;

import me.ehsanmna.disaster.model.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public abstract class BaseHologram implements Hologram {
    protected final JavaPlugin plugin;
    String id;
    protected Location location;
    protected boolean spawned = false;
    protected boolean visibleByDefault = true;
    protected Set<UUID> hiddenPlayers = new HashSet<>();

    protected BaseHologram(JavaPlugin plugin, String id, Location location) {
        this.plugin = plugin;
        this.location = location.clone();
        this.id = id;
    }

    @Override
    public abstract void spawn();

    @Override
    public abstract void despawn();

    @Override
    public abstract void update();

    @Override
    public void showTo(Player player) {
        hiddenPlayers.remove(player.getUniqueId());
        if (isSpawned()) {
            updateVisibilityFor(player);
        }
    }

    @Override
    public void hideFrom(Player player) {
        hiddenPlayers.add(player.getUniqueId());
        if (isSpawned()) {
            updateVisibilityFor(player);
        }
    }

    protected abstract void updateVisibilityFor(Player player);

    @Override
    public void move(Location location) {
        this.location = location.clone();
        if (isSpawned()) {
            update();
        }
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public boolean isSpawned() {
        return spawned;
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        this.visibleByDefault = visible;
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public String getId() {
        return id;
    }
}