package me.ehsanmna.disaster.config;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigRepository {

    JavaPlugin plugin;
    File file;
    YamlConfiguration yamlConfiguration;

    public ConfigRepository(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup(){
        file = new File(plugin.getDataFolder(),"arenas.yml");
        if (!file.exists())
            plugin.saveResource("arenas.yml",true);

        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(){
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }
}
