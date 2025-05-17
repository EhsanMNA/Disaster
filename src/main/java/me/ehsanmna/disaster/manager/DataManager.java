package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.config.DataRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class DataManager {

    private final DisasterPlugin plugin;
    private final DataRepository dataRepository;
    private final LobbyManager lobbyManager;

    public DataManager(DisasterPlugin plugin) {
        this.plugin = plugin;
        lobbyManager = plugin.getLobbyManager();
        dataRepository = new DataRepository(plugin);
    }

    public void loadData(){
        dataRepository.setup();
        if (dataRepository.getYamlConfiguration().contains("spawn"))
            lobbyManager.setLobbySpawn(loadLocation(dataRepository.getYamlConfiguration().getConfigurationSection("spawn")));
    }

    private Location loadLocation(@NotNull ConfigurationSection section) {
        World world = Bukkit.getWorld(section.getString("world","world"));
        double x = section.getDouble("x",0);
        double y = section.getDouble("y",0);
        double z = section.getDouble("z",0);
        double yaw = section.getDouble("yaw",0);
        double pitch = section.getDouble("yaw",0);
        return new Location(world,x,y,z, (float) yaw, (float) pitch);
    }

    private void wrapLocation(ConfigurationSection section, Location location, boolean blocked){
        section.set("world",location.getWorld().getName());
        section.set("x",location.getX());
        section.set("y",location.getY());
        section.set("z",location.getZ());
        if (blocked){
            section.set("yaw",location.getYaw());
            section.set("pitch",location.getPitch());
        }
    }

    public void save() {
        dataRepository.getYamlConfiguration().createSection("spawn");
        wrapLocation(dataRepository.getYamlConfiguration().getConfigurationSection("spawn"), lobbyManager.getLobbySpawn(), false);
        dataRepository.saveConfig();
    }
}
