package me.ehsanmna.disaster.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class DataRepository {

    JavaPlugin plugin;
    File file;
    YamlConfiguration yamlConfiguration;

    public DataRepository(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup(){
        file = new File(plugin.getDataFolder(),"data.yml");
        if (!file.exists())
            plugin.saveResource("data.yml",true);

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
