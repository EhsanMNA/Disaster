package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.config.ConfigRepository;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaConfig;
import me.ehsanmna.disaster.model.hologram.Hologram;
import me.ehsanmna.disaster.model.hologram.impl.TextHologram;
import me.ehsanmna.disaster.model.machine.BasePowerUpMachine;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.machine.PowerUpMachineImpl;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import me.ehsanmna.disaster.model.powerup.requirement.ItemRequirement;
import me.ehsanmna.disaster.model.powerup.requirement.PowerUpRequirement;
import me.ehsanmna.disaster.model.powerup.requirement.XPRequirement;
import me.ehsanmna.disaster.model.region.Region;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

        if (section.contains("machines")) loadMachines(Objects.requireNonNull(section.getConfigurationSection("machines")), arena);

        return arena;
    }

    private void loadMachines(@NotNull ConfigurationSection section, Arena arena) {
        for (String machine : section.getKeys(false)){
            try {
                PowerUpMachine powerUpMachine = loadMachine(section);
                powerUpMachine.setArena(arena);
                arena.getMachines().put(machine,powerUpMachine);
            }catch (Exception error){
                plugin.getLogger().severe("Error on loading machines for "+arena.getName()+"! Machine: "+machine+ ", Error: "+error.getMessage());
                plugin.getErrorLogger().logError("Failed to load machine! Arena:"+arena.getName()+", Machine: "+machine, error);
            }
        }
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
        double pitch = section.getDouble("pitch",0);
        return new Location(world,x,y,z, (float) yaw, (float) pitch);
    }

    private PowerUpMachine loadMachine(@NotNull ConfigurationSection section){
        PowerUpMachine powerUpMachine = new PowerUpMachineImpl(
                section.getString("name","null"),
                section.getString("description","null"),
                PowerUpType.valueOf(section.getString("powerup","none").toUpperCase()), plugin);

        if (section.contains("requirements")){
            for (String requirement : section.getConfigurationSection("requirements").getKeys(false)){
                ConfigurationSection requirementSection = section.getConfigurationSection("requirements."+requirement);
                PowerUpRequirement powerUpRequirement = loadPowerupRequirement(requirementSection);
                powerUpMachine.getRequirements().add(powerUpRequirement);
            }
        }
        if (section.contains("region"))
            powerUpMachine.setRegion(loadRegion(Objects.requireNonNull(section.getConfigurationSection("region"))));
        if (section.contains("holograms")){
//            for (String hologram : section.getConfigurationSection("holograms").getKeys(false)){
//                ConfigurationSection hologramsSection = section.getConfigurationSection("holograms."+hologram);
//                Hologram holo = loadHologram(hologramsSection);
//                holo.despawn();
//
//            }
        }

        return powerUpMachine;
    }

    private Hologram loadHologram(@NotNull ConfigurationSection section){
        if (section.getString("type","text").equalsIgnoreCase("text")){
            return plugin.getHologramManager().createTextHologram(
                    section.getString("id","null"+new Random().nextInt(1000)),
                    loadLocation(section.getConfigurationSection("location")),
                    section.getString("text","null"), false
            );
        } else {
            return plugin.getHologramManager().createItemHologram(
                    section.getString("id","null"+new Random().nextInt(1000)),
                    loadLocation(section.getConfigurationSection("location")),
                    loadItem(section.getConfigurationSection("item")), false
            );
        }
    }

    private PowerUpRequirement loadPowerupRequirement(@NotNull ConfigurationSection section){
        PowerUpRequirement powerUpRequirement;
        if (section.getString("type","item").equalsIgnoreCase("item")) {
            Material material = Material.valueOf(section.getString("material","STONE"));
            int amount = section.getInt("amount",1);
            int customModelData = section.getInt("customModelData",0);
            powerUpRequirement = new ItemRequirement(plugin, material ,amount,customModelData);
        }
        else {
            int amount = section.getInt("amount",1);
            powerUpRequirement = new XPRequirement(plugin, amount);
        }
        return powerUpRequirement;
    }

    private ItemStack loadItem(@NotNull ConfigurationSection section){
        ItemStack itemStack = new ItemStack(Material.STONE);
        itemStack.setType(Material.valueOf(section.getString("material","STONE")));
        itemStack.setAmount(section.getInt("amount",1));
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(TextUtils.toComponent(section.getString("displayname","Ich bin Nein")));
            itemMeta.setCustomModelData(section.getInt("customModelData",0));
            if (section.contains("lore")) itemMeta.lore(TextUtils.toComponent(section.getStringList("lore")));
        });
        return itemStack;
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
        wrapMachines(arenaSection.createSection("machines"),arena);
    }

    private void wrapRegion(ConfigurationSection section, Region region){
        ConfigurationSection pos1Section = section.createSection("pos1");
        ConfigurationSection pos2Section = section.createSection("pos2");
        wrapLocation(pos1Section, region.getPos1(), false);
        wrapLocation(pos2Section, region.getPos2(), false);
    }

    private void wrapLocation(ConfigurationSection section, Location location, boolean blocked){
        section.set("world",location.getWorld() != null ?location.getWorld().getName(): "world");
        section.set("x",location.getX());
        section.set("y",location.getY());
        section.set("z",location.getZ());
        if (blocked){
            section.set("yaw",location.getYaw());
            section.set("pitch",location.getPitch());
        }
    }

    private void wrapMachines(@NotNull ConfigurationSection section, Arena arena) {
        for (String machineName : arena.getMachines().keySet()){
            BasePowerUpMachine machine = (BasePowerUpMachine) arena.getMachines().get(machineName);
            ConfigurationSection machineSection = section.createSection(machineName);;
            machineSection.set("name",machine.getName());
            machineSection.set("description",machine.getDescription());
            machineSection.set("powerup",machine.getPowerUp().getType().name());

            wrapLocation(machineSection.createSection("button-location"), machine.getButtonLocation(), false);

            if (!machine.getRequirements().isEmpty()){
                machineSection.createSection("requirements");
                for (PowerUpRequirement requirement : machine.getRequirements()){
                    ConfigurationSection requirementSection = machineSection.getConfigurationSection("requirements").createSection(requirement.getName());
                    wrapRequirement(requirementSection, arena, requirement);
                }
            }

            if (machine.getRegion() != null) wrapRegion(machineSection.createSection("region"), machine.getRegion());

            machineSection.createSection("holograms");
//            if (machine.getTextHologram() != null) wrapHologram(machineSection.getConfigurationSection("holograms").createSection(machine.getTextHologram().getId()), machine.getTextHologram());
//            if (machine.getItemHologram() != null) wrapHologram(machineSection.getConfigurationSection("holograms").createSection(machine.getItemHologram().getId()), machine.getItemHologram());
        }
    }

    private void wrapHologram(@NotNull ConfigurationSection section, Hologram hologram) {
    }

    private void wrapRequirement(@NotNull ConfigurationSection section, Arena arena, PowerUpRequirement requirement){
        section.set("name",requirement.getName());
        if (requirement instanceof ItemRequirement itemRequirement){
            section.set("material",itemRequirement.getItemType());
            section.set("amount",itemRequirement.getAmount());
            section.set("customModelData",itemRequirement.getCustomModelData());
        }else if (requirement instanceof XPRequirement xpRequirement){
            section.set("amount",xpRequirement.getAmount());
        }
    }

    public ConfigRepository getConfigRepository() {
        return configRepository;
    }
}
