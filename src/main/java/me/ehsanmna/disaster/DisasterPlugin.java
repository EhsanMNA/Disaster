package me.ehsanmna.disaster;

import me.ehsanmna.disaster.command.DisasterAdminCommand;
import me.ehsanmna.disaster.command.DisasterDeveloperCommand;
import me.ehsanmna.disaster.command.DisasterMainCommand;
import me.ehsanmna.disaster.listener.*;
import me.ehsanmna.disaster.manager.*;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.hologram.HologramManager;
import me.ehsanmna.disaster.util.DependencyManager;
import me.ehsanmna.disaster.util.ErrorLogger;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisasterPlugin extends JavaPlugin {

    private static DisasterPlugin main;
    private DependencyManager dependencyManager;
    private ConfigManager configManager;
    private ArenaManager arenaManager;
    private PlayerManager playerManager;
    private DataManager dataManager;
    private LobbyManager lobbyManager;
    private HologramManager hologramManager;
    private PowerUpManager powerUpManager;
    private PowerUpMachineManager powerUpMachineManager;
    private ErrorLogger errorLogger;

    @Override
    public void onEnable() {
        main = this;
        saveDefaultConfig();

        dependencyManager = new DependencyManager();
        if (!dependencyManager.checkForDependency()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        TextUtils.initialize(this);

        errorLogger = new ErrorLogger(this);
        lobbyManager = new LobbyManager();
        arenaManager = new ArenaManager(this);
        configManager = new ConfigManager(this);
        dataManager = new DataManager(this);
        playerManager = new PlayerManager(this);
        hologramManager = new HologramManager(this, true);
        powerUpManager = new PowerUpManager(this);
        powerUpMachineManager = new PowerUpMachineManager(arenaManager);

        configManager.loadArenas();
        dataManager.loadData();

        DisasterMainCommand mainCommand = new DisasterMainCommand(this);
        DisasterDeveloperCommand developerCommand = new DisasterDeveloperCommand(this);
        DisasterAdminCommand adminCommand = new DisasterAdminCommand(this);
        getCommand("disasterdeveloper").setExecutor(developerCommand);
        getCommand("disasteradmin").setExecutor(adminCommand);
        getCommand("disaster").setExecutor(mainCommand);

        getCommand("disasterdeveloper").setTabCompleter(developerCommand);
        getCommand("disasteradmin").setTabCompleter(adminCommand);
        getCommand("disaster").setTabCompleter(mainCommand);

        getServer().getPluginManager().registerEvents(new DisasterSettingListener(), this);
        getServer().getPluginManager().registerEvents(new DisasterGameListener(playerManager,arenaManager), this);
        getServer().getPluginManager().registerEvents(new DisasterRulesListener(playerManager,arenaManager), this);
        getServer().getPluginManager().registerEvents(new DisasterEventsListener(this, playerManager,arenaManager), this);
        getServer().getPluginManager().registerEvents(new DisasterGameManageListener(playerManager,arenaManager), this);
        getServer().getPluginManager().registerEvents(new PowerUpListener(this), this);

        getLogger().info("Disaster has been loaded!");
    }

    @Override
    public void onDisable() {
        try {
            configManager.saveArenas();
            dataManager.save();
            for (Arena arena : arenaManager.getArenas())
                if (arena.isEnable() && arena.getArenaHandler().isRunning()) arena.getArenaHandler().getArenaService().finishGame(false);
                else arena.disable();
        }catch (NullPointerException ignored){}
    }

    public static DisasterPlugin getInstance(){
        return main;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public PowerUpMachineManager getPowerUpMachineManager() {
        return powerUpMachineManager;
    }

    public PowerUpManager getPowerUpManager(){
        return powerUpManager;
    }

    public ErrorLogger getErrorLogger() {
        return errorLogger;
    }
}
