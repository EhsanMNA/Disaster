package me.ehsanmna.disaster.service;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.SlimeWorldUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ArenaService {

    private boolean debug = false;
    private final DisasterPlugin plugin;
    private final Arena arena;
    private final ArenaWaitingService waitingService;
    private final ArenaGameService gameService;

    public ArenaService(DisasterPlugin plugin ,Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        waitingService = new ArenaWaitingService(this);
        gameService = new ArenaGameService(this);
    }

    public Arena getArena() {
        return arena;
    }

    public ArenaWaitingService getWaitingService() {
        return waitingService;
    }

    public ArenaGameService getGameService() {
        return gameService;
    }

    public void startGame(){
        arena.getArenaHandler().setRunning(true);
        gameService.startGame();
    }

    public void finishGame(boolean restart) {
        for (ArenaPlayer arenaPlayer : new ArrayList<>(arena.getArenaHandler().getPlayers())){
            Player player = arenaPlayer.getPlayer();
            plugin.getPlayerManager().leavePlayer(player);
        }
        arena.disable();
        if (restart)
            new BukkitRunnable() {
                @Override
                public void run() {
                    arena.enable();
                    SlimeWorldUtils.createCloneWorld(arena.getWorldName());
                    arena.getArenaHandler().setSlimeWorld(SlimeWorldUtils.getWorld(arena.getWorldName()));
                }
            }.runTaskLater(getPlugin(), 100);
    }

    public DisasterPlugin getPlugin() {
        return plugin;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
