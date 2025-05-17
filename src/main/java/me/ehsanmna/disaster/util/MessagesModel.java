package me.ehsanmna.disaster.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessagesModel {

    public String prefix;
    private final Map<String, String> messages = new HashMap<>();

    private final JavaPlugin plugin;

    public MessagesModel(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessagesFromConfig();
    }

    private void loadMessagesFromConfig() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists())
            plugin.saveResource("messages.yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(true))
            if (!config.isConfigurationSection(key))
                messages.put(key, config.getString(key, "no value"));

        prefix = messages.getOrDefault("prefix", "&a&lDISASTERS &f|&r ");
        plugin.getLogger().info("Total messages loaded: " + messages.size());
    }

    // Reload messages from config
    public void reloadMessages() {
        messages.clear();
        loadMessagesFromConfig();
    }

    public String get(String key) {
        return messages.getOrDefault(key, "null");
    }
}
