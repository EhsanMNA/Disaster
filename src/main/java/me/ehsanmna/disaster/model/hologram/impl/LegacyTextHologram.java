package me.ehsanmna.disaster.model.hologram.impl;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyTextHologram extends BaseHologram implements TextHologram {
    private ArmorStand armorStand;
    private String text;

    public LegacyTextHologram(JavaPlugin plugin,String id, Location location, String text) {
        super(plugin, id, location);
        this.text = text;
    }

    @Override
    public void spawn() {
        if (isSpawned()) return;

        armorStand = location.getWorld().spawn(location, ArmorStand.class, entity -> {
            entity.setVisible(false);
            entity.setGravity(false);
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
            entity.setSmall(true);
            entity.setMarker(true);
        });

        spawned = true;
    }

    @Override
    public void despawn() {
        if (!isSpawned()) return;

        armorStand.remove();
        armorStand = null;
        spawned = false;
    }

    @Override
    public void update() {
        if (!isSpawned()) return;

        armorStand.teleport(location);
        armorStand.setCustomName(text);
    }

    @Override
    protected void updateVisibilityFor(Player player) {
        if (visibleByDefault && !hiddenPlayers.contains(player.getUniqueId())) {
            player.showEntity(plugin, armorStand);
        } else {
            player.hideEntity(plugin, armorStand);
        }
    }

    @Override
    public void setText(String text) {
        this.text = text;
        if (isSpawned()) {
            armorStand.setCustomName(text);
        }
    }

    @Override
    public String getText() {
        return text;
    }
}