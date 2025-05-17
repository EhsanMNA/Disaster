package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.config.ConfigRepository;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaConfig;
import me.ehsanmna.disaster.model.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfigManager {

    private final DisasterPlugin plugin;
    private final ConfigRepository configRepository;
    private final ArenaManager arenaManager;

    public ConfigManager(DisasterPlugin plugin) {
        this.plugin = plugin;
        arenaManager = plugin.getArenaManager();
        configRepository = new ConfigRepository(plugin);
    }

    public void loadArenas(){
        configRepository.setup();
        for (String arenaName : configRepository.getYamlConfiguration().getKeys(false)){
            Arena arena = loadArena(Objects.requireNonNull(configRepository.getYamlConfiguration().getConfigurationSection(arenaName)));
            arenaManager.addArena(arena);
            arena.enable();
        }
    }

    public void saveArenas(){
        for (Arena arena : arenaManager.getArenas())
            wrapArena(configRepository.getYamlConfiguration(),arena);
        configRepository.saveConfig();
    }

    private Arena loadArena(@NotNull ConfigurationSection section){
        String name = section.getString("name","null");
        String displayname = section.getString("display-name","null");
        String worldName = section.getString("world-name","null");
        int time = section.getInt("time",300);
        int waitingTime = section.getInt("waiting-time",300);
        int maxPlayers = section.getInt("max-players",16);
        ArenaConfig arenaConfig = new ArenaConfig(time,waitingTime,maxPlayers);
        Location spawn = loadLocation(Objects.requireNonNull(section.getConfigurationSection("spawn")));
        Region region = loadRegion(Objects.requireNonNull(section.getConfigurationSection("region")));

        Arena arena = new Arena(plugin, name);
        arena.setDisplayName(displayname);
        arena.setArenaConfig(arenaConfig);
        arena.setSpawn(spawn);
        arena.setArenaRegion(region);
        arena.setWorldName(worldName);

        return arena;
    }

    private Region loadRegion(@NotNull ConfigurationSection section){
        Location pos1 = loadLocation(Objects.requireNonNull(section.getConfigurationSection("pos1")));
        Location pos2 = loadLocation(Objects.requireNonNull(section.getConfigurationSection("pos2")));
        return new Region(pos1,pos2);
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

    private void wrapArena(ConfigurationSection section, Arena arena){
        section.set(arena.getName()+".name", arena.getName());
        ConfigurationSection arenaSection = section.getConfigurationSection(arena.getName());
        assert arenaSection != null;
        arenaSection.set("display-name", arena.getDisplayName());
        arenaSection.set("time",arena.getTime());
        arenaSection.set("waiting-time",arena.getArenaConfig().waitingTime());
        arenaSection.set("max-players", arena.getMaxPlayers());
        arenaSection.set("world-name", arena.getWorldName());
        wrapLocation(arenaSection.createSection("spawn"), arena.getRawSpawn(), true);
        wrapRegion(arenaSection.createSection("region"),arena.getArenaRegion());
    }

    private void wrapRegion(ConfigurationSection section, Region region){
        ConfigurationSection pos1Section = section.createSection("pos1");
        ConfigurationSection pos2Section = section.createSection("pos2");
        wrapLocation(pos1Section, region.getPos1(), false);
        wrapLocation(pos2Section, region.getPos2(), false);
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

    public ConfigRepository getConfigRepository() {
        return configRepository;
    }
}
