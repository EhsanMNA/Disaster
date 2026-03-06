package me.ehsanmna.disaster.model.hologram;

import me.ehsanmna.disaster.model.hologram.config.HologramConfig;
import me.ehsanmna.disaster.model.hologram.config.HologramData;
import me.ehsanmna.disaster.model.hologram.impl.ItemHologram;
import me.ehsanmna.disaster.model.hologram.impl.TextHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class HologramManager {
    private final JavaPlugin plugin;
    private final HologramFactory factory;
    private final HologramConfig config;
    private final Map<String, Hologram> activeHolograms = new HashMap<>();

    public HologramManager(JavaPlugin plugin, boolean useModernEntities) {
        this.plugin = plugin;
        this.factory = new HologramFactory(plugin, useModernEntities);
        this.config = new HologramConfig(plugin);
        loadAllHolograms();
    }

    /*
    * --- Plugin initialization
    * HologramManager hologramManager = new HologramManager(this, true);
    *
    * --- Creating holograms
    * hologramManager.createTextHologram("welcome", new Location(world, 100, 64, 200), "Welcome to our server!");
    * hologramManager.createItemHologram("reward", new Location(world, 103, 64, 200), new ItemStack(Material.DIAMOND));
    *
    * --- Updating holograms
    * hologramManager.updateHologramText("welcome", "Welcome back!");
    *
    * --- Moving holograms
    * hologramManager.moveHologram("welcome", new Location(world, 105, 65, 200));
    *
    * --- Player-specific visibility
    * Player player = ...;
    * hologramManager.hideHologramFrom("welcome", player);
     */

    private void loadAllHolograms() {
        for (Map.Entry<String, HologramData> entry : config.getHolograms().entrySet()) {
            HologramData data = entry.getValue();
            Hologram hologram;

            if (data.isText()) {
                hologram = factory.createTextHologram(data.getLocation(), data.getId(), data.getText());
            } else {
                hologram = factory.createItemHologram(data.getLocation(), data.getId(), data.getItem());
            }

            hologram.spawn();
            activeHolograms.put(entry.getKey(), hologram);
        }
    }

    public Hologram createTextHologram(String id, Location location, String text) {
        return createTextHologram(id, location, text, true);
    }

    public Hologram createTextHologram(String id, Location location, String text, boolean save) {
        if (activeHolograms.containsKey(id)) {
            throw new IllegalArgumentException("Hologram with id " + id + " already exists");
        }

        TextHologram hologram = factory.createTextHologram(location, id, text);
        hologram.spawn();
        activeHolograms.put(id, hologram);
        if (save){
            config.addHologram(id, new HologramData(id, location, text));
            config.save();
        }
        return hologram;
    }

    public Hologram createItemHologram(String id, Location location, ItemStack item) {
        return createItemHologram(id,location,item,true);
    }

    public Hologram createItemHologram(String id, Location location, ItemStack item, boolean save) {
        if (activeHolograms.containsKey(id)) {
            throw new IllegalArgumentException("Hologram with id " + id + " already exists");
        }

        ItemHologram hologram = factory.createItemHologram(location, id, item);
        hologram.spawn();
        activeHolograms.put(id, hologram);
        if (save){
            config.addHologram(id, new HologramData(id, location, item));
            config.save();
        }
        return hologram;
    }

    public void removeHologram(String id) {
        Hologram hologram = activeHolograms.remove(id);
        if (hologram != null) {
            hologram.despawn();
            config.removeHologram(id);
            config.save();
        }
    }

    public void updateHologramText(String id, String newText) {
        Hologram hologram = activeHolograms.get(id);
        if (hologram instanceof TextHologram) {
            ((TextHologram) hologram).setText(newText);
            // Update config
            config.addHologram(id, new HologramData(id, hologram.getLocation(), newText));
            config.save();
        } else {
            throw new IllegalArgumentException("Hologram with id " + id + " is not a text hologram");
        }
    }

    public void updateHologramItem(String id, ItemStack newItem) {
        Hologram hologram = activeHolograms.get(id);
        if (hologram instanceof ItemHologram) {
            ((ItemHologram) hologram).setItem(newItem);
            // Update config
            config.addHologram(id, new HologramData(id, hologram.getLocation(), newItem));
            config.save();
        } else {
            throw new IllegalArgumentException("Hologram with id " + id + " is not an item hologram");
        }
    }

    public void moveHologram(String id, Location newLocation) {
        Hologram hologram = activeHolograms.get(id);
        if (hologram != null) {
            hologram.move(newLocation);
            // Update config
            HologramData oldData = config.getHolograms().get(id);
            if (oldData.isText()) {
                config.addHologram(id, new HologramData(id, newLocation, oldData.getText()));
            } else {
                config.addHologram(id, new HologramData(id, newLocation, oldData.getItem()));
            }
            config.save();
        }
    }

    public void showHologramTo(String id, Player player) {
        Hologram hologram = activeHolograms.get(id);
        if (hologram != null) {
            hologram.showTo(player);
        }
    }

    public void hideHologramFrom(String id, Player player) {
        Hologram hologram = activeHolograms.get(id);
        if (hologram != null) {
            hologram.hideFrom(player);
        }
    }

    public void reload() {
        activeHolograms.values().forEach(Hologram::despawn);
        activeHolograms.clear();
        config.load();
        loadAllHolograms();
    }
}