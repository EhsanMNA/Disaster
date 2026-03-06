package me.ehsanmna.disaster.model.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {
    void spawn();
    void despawn();
    void update();
    void showTo(Player player);
    void hideFrom(Player player);
    void move(Location location);
    Location getLocation();
    boolean isSpawned();
    void setVisibleByDefault(boolean visible);
    boolean isVisibleByDefault();
    String getId();
}

