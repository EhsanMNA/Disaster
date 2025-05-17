package me.ehsanmna.disaster;

import me.ehsanmna.disaster.command.DisasterAdminCommand;
import me.ehsanmna.disaster.command.DisasterDeveloperCommand;
import me.ehsanmna.disaster.command.DisasterMainCommand;
import me.ehsanmna.disaster.listener.DisasterGameListener;
import me.ehsanmna.disaster.manager.*;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisasterPlugin extends JavaPlugin {

    private static DisasterPlugin main;
    private ConfigManager configManager;
    private ArenaManager arenaManager;
    private PlayerManager playerManager;
    private DataManager dataManager;
    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        main = this;
        saveDefaultConfig();

        TextUtils.initialize(this);

        lobbyManager = new LobbyManager();
        arenaManager = new ArenaManager(this);
        configManager = new ConfigManager(this);
        dataManager = new DataManager(this);
        playerManager = new PlayerManager(lobbyManager);

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

        getServer().getPluginManager().registerEvents(new DisasterGameListener(playerManager,arenaManager), this);

        getLogger().info("Disaster has been loaded!");
    }

    @Override
    public void onDisable() {
        configManager.saveArenas();
        dataManager.save();
        for (Arena arena : arenaManager.getArenas())
            if (arena.isEnable() && arena.getArenaHandler().isRunning()) arena.getArenaHandler().getArenaService().finishGame();
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
}
