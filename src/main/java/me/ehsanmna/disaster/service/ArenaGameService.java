package me.ehsanmna.disaster.service;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.*;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaGameService {

    private final DisasterPlugin plugin;
    private final Arena arena;
    private final ArenaService arenaService;
    private BukkitTask task;

    public ArenaGameService(ArenaService arenaService) {
        this.arena = arenaService.getArena();
        this.arenaService = arenaService;
        this.plugin = arenaService.getPlugin();
    }

    public void startGame() {
        for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers()){
            Player player = arenaPlayer.getPlayer();
            player.teleport(arena.getSpawn());
            player.playSound(player.getLocation() , Sound.ENTITY_PLAYER_LEVELUP, 10 ,2);
            arena.getArenaHandler().announce("arena-start");
        }

        task = new BukkitRunnable() {
            int loopedTimes = 0;
            @Override
            public void run() {
                loopedTimes++;
                arena.getArenaHandler().tick();
                if (loopedTimes == 10) addRandomDisasters(true);
                if (loopedTimes == 60) addRandomDisasters(true);
                if (loopedTimes == 120) addRandomDisasters(true);
                if (loopedTimes >= arena.getArenaConfig().time()) {
                    finishGame();
                    return;
                }

                for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers()){
                    if (arenaPlayer.isAlive()){
                        TextUtils.sendActionbar(arenaPlayer.getPlayer(), "game-actionbar-timer", arena.getArenaHandler().getGameTime()+"");
                    }else {
                        TextUtils.sendActionbar(arenaPlayer.getPlayer(), "game-actionbar-spectator", arena.getArenaHandler().getGameTime()+"");
                    }
                }
            }
        }.runTaskTimer(plugin,0,20);
    }

    public void finishGame(){
        task.cancel();
        arena.getArenaHandler().announce("game-finish-header");
        StringBuilder stringBuilder = new StringBuilder();
        for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers())
            if (arenaPlayer.isAlive()) stringBuilder.append(arenaPlayer.getPlayer().getName()).append(", ");
        if (stringBuilder.isEmpty()) stringBuilder.append("No One");
        else stringBuilder.delete(stringBuilder.length() -2,stringBuilder.length());
        arena.getArenaHandler().announce("game-finish-title", stringBuilder.toString());
        arena.getArenaHandler().announce("game-finish-footer");

        arena.getArenaHandler().setGameTime(arena.getArenaConfig().time());
        arena.getArenaHandler().getDisasters().clear();

        new BukkitRunnable() {
            @Override
            public void run() {arenaService.finishGame();}
        }.runTaskLater(plugin, 100);
    }

    private void addRandomDisasters(boolean sendMessage){
        Random random = new Random();
        int n = random.nextInt(2)+1;
        List<Disaster> disasters = new ArrayList<>();
        for (int i = 0; i < n; i++){
            Disaster disaster = getRandomDisaster();
            disaster = submitDisaster(disaster);
            if(disaster!= null) disasters.add(disaster);
        }
        if(disasters.isEmpty()) arena.getArenaHandler().announce("No disaster found ... report admin!");
        if (sendMessage){
            arena.getArenaHandler().announce("game-disaster-header");
            arena.getArenaHandler().announce("game-disaster-title");
        }
        for (Disaster disaster : disasters) {
            arena.getArenaHandler().addDisaster(disaster);
            if (sendMessage) arena.getArenaHandler().announce("game-disaster-description", disaster.getName(), disaster.getDescription());
        }
        if (sendMessage) arena.getArenaHandler().announce("game-disaster-footer");
    }

    private Disaster submitDisaster(Disaster d){
        Disaster disaster = d;
        int i = 0;
        while (arena.getArenaHandler().hasDisaster(d.getType())){
            disaster = getRandomDisaster();
            i++;
            if(i == 50) return null;
        }
        return disaster;
    }

    private Disaster getRandomDisaster(){
        Disaster disaster;
        Random random = new Random();
        int index = random.nextInt(5)+1;
        switch (index){
            case 1 -> disaster = new DragonsDisaster(plugin,arena);
            case 2 -> disaster = new FloodDisaster(plugin,arena);
            case 3 -> disaster = new LightingDisaster(plugin,arena);
            case 4 -> disaster = new MeteorShowerDisaster(plugin,arena);
            case 5 -> disaster = new ZombieDisaster(plugin,arena);
            default -> disaster = new AcidRainDisaster(plugin,arena);
        }
        return disaster;
    }

}
