package me.ehsanmna.disaster.model.hologram.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class HologramConfig {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, HologramData> holograms = new HashMap<>();

    public HologramConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        load();
    }

    public void load() {
        holograms.clear();
        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection hologramSection = section.getConfigurationSection(key);
            if (hologramSection != null) {
                holograms.put(key, new HologramData(hologramSection.getValues(false)));
            }
        }
    }

    public void save() {
        config.set("holograms", null);
        for (Map.Entry<String, HologramData> entry : holograms.entrySet()) {
            config.set("holograms." + entry.getKey(), entry.getValue().serialize());
        }
        plugin.saveConfig();
    }

    public Map<String, HologramData> getHolograms() {
        return new HashMap<>(holograms);
    }

    public void addHologram(String id, HologramData data) {
        holograms.put(id, data);
    }

    public void removeHologram(String id) {
        holograms.remove(id);
    }
}