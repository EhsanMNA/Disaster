package me.ehsanmna.disaster.model.hologram.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

public class ModernTextHologram extends BaseHologram implements TextHologram {
    private TextDisplay display;
    private String text;

    public ModernTextHologram(JavaPlugin plugin, String id, Location location, String text) {
        super(plugin, id, location);
        this.text = text;
    }

    @Override
    public void spawn() {
        if (isSpawned()) return;

        display = location.getWorld().spawn(location, TextDisplay.class, entity -> {
            entity.setText(text);
            entity.setDisplayWidth(10); // Prevent text clipping
            entity.setSeeThrough(true);
            entity.setDefaultBackground(false);
            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
        });

        spawned = true;
    }

    @Override
    public void despawn() {
        if (!isSpawned()) return;

        display.remove();
        display = null;
        spawned = false;
    }

    @Override
    public void update() {
        if (!isSpawned()) return;

        display.teleport(location);
        display.setText(text);
    }

    @Override
    protected void updateVisibilityFor(Player player) {
        if (visibleByDefault && !hiddenPlayers.contains(player.getUniqueId())) {
            player.showEntity(plugin, display);
        } else {
            player.hideEntity(plugin, display);
        }
    }

    @Override
    public void setText(String text) {
        this.text = text;
        if (isSpawned()) {
            display.setText(text);
        }
    }

    @Override
    public String getText() {
        return text;
    }
}
