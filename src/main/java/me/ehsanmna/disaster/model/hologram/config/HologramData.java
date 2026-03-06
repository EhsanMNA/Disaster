package me.ehsanmna.disaster.model.hologram.config;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class HologramData implements ConfigurationSerializable {
    private final String id;
    private final Location location;
    private final String text;
    private final ItemStack item;
    private final boolean isText;

    public HologramData(String id, Location location, String text) {
        this.id = id;
        this.location = location;
        this.text = text;
        this.item = null;
        this.isText = true;
    }

    public HologramData(String id, Location location, ItemStack item) {
        this.id = id;
        this.location = location;
        this.text = null;
        this.item = item;
        this.isText = false;
    }

    public HologramData(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.location = (Location) map.get("location");
        this.isText = (boolean) map.get("isText");
        if (isText) {
            this.text = (String) map.get("content");
            this.item = null;
        } else {
            this.text = null;
            this.item = (ItemStack) map.get("content");
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("location", location);
        map.put("isText", isText);
        map.put("content", isText ? text : item);
        return map;
    }

    // Getters
    public Location getLocation() { return location.clone(); }
    public String getText() { return text; }
    public ItemStack getItem() { return item != null ? item.clone() : null; }
    public boolean isText() { return isText; }
    public String getId() {
        return id;
    }
}