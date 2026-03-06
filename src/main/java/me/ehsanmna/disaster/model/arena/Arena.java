package me.ehsanmna.disaster.model.arena;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.region.Region;
import me.ehsanmna.disaster.util.SlimeWorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Arena {

    private final DisasterPlugin plugin;

    private final String name;
    private String displayName;
    private String worldName;
    private boolean enable;
    private Location spawn;
    private ArenaConfig arenaConfig;
    private Region arenaRegion;
    private ArenaHandler arenaHandler;
    Map<String, PowerUpMachine> machines = new HashMap<>();

    public Arena(DisasterPlugin disasterPlugin, String name) {
        this.plugin = disasterPlugin;
        this.name = name;
        enable = false;
        arenaConfig = new ArenaConfig(300,30,16);
    }

    public String getName() {
        return name;
    }

    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Region getArenaRegion() {
        return arenaRegion;
    }

    public void setArenaRegion(Region arenaRegion) {
        this.arenaRegion = arenaRegion;
    }

    public int getMaxPlayers() {
        return arenaConfig.maxPlayers();
    }

    public int getTime() {
        return arenaConfig.time();
    }

    public Location getSpawn() {
        return new Location(Bukkit.getWorld(worldName+"-backup"), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
    }

    public Location getRawSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    public void setArenaConfig(ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Map<String, PowerUpMachine> getMachines() {
        return machines;
    }

    public boolean hasArenaRegion(){
        return arenaRegion != null;
    }

    public boolean isEnable() {
        return enable;
    }

    public void enable(){
        enable = true;
        arenaHandler = new ArenaHandler(plugin, this);
        arenaHandler.getArenaWorldHandler().checkWorld();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(name, arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public void disable() {
        enable = false;
        SlimeWorldUtils.deleteWorld(worldName);
        arenaHandler = null;
    }
}
