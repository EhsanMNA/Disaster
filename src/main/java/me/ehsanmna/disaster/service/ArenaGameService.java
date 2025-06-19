package me.ehsanmna.disaster.service;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.events.ArenaEndEvent;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.*;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
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
        ArenaEndEvent event = new ArenaEndEvent(arena);
        Bukkit.getPluginManager().callEvent(event);

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
        arena.getArenaHandler().getDisasters().forEach(Disaster::deActive);
        arena.getArenaHandler().getDisasters().clear();

        new BukkitRunnable() {
            @Override
            public void run() {arenaService.finishGame(true);}
        }.runTaskLater(plugin, 100);
    }

    public boolean addDisaster(String disasterName){
        return addDisaster(disasterName, false);
    }

    public boolean addDisaster(String disasterName, boolean sendMessage){
        try {
            DisasterType disasterType = DisasterType.valueOf(disasterName.toUpperCase());
            addDisaster(disasterType, sendMessage);
            return true;
        }catch (Exception ignored){}
        return false;
    }

    private void addDisaster(DisasterType disasterType, boolean sendMessage){
        Disaster disaster = getDisaster(disasterType);
        arena.getArenaHandler().addDisaster(disaster);
        if (sendMessage){
            arena.getArenaHandler().announce("game-disaster-header");
            arena.getArenaHandler().announce("game-disaster-title");
            arena.getArenaHandler().announce("game-disaster-description", disaster.getName(), disaster.getDescription());
            arena.getArenaHandler().announce("game-disaster-footer");
        }
    }

    public void addRandomDisasters(boolean sendMessage){
        Random random = new Random();
        int n = random.nextInt(2)+1;
        List<Disaster> disasters = new ArrayList<>();
        for (int i = 0; i < n; i++){
            Disaster disaster = getRandomDisaster();
            disaster = submitDisaster(disaster);
            if(disaster!= null) disasters.add(disaster);
        }
        if(disasters.isEmpty()) arena.getArenaHandler().announce("No disaster found ... (report admin! "+n+")");
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
            if(i == 100) return null;
        }
        return disaster;
    }

    private Disaster getRandomDisaster(){
        Disaster disaster;
        Random random = new Random();
        int index = random.nextInt(10);
        switch (index){
            case 1 -> disaster = new DragonsDisaster(plugin,arena);
            case 2 -> disaster = new FloodDisaster(plugin,arena);
            case 3 -> disaster = new LightingDisaster(plugin,arena);
            case 4 -> disaster = new MeteorShowerDisaster(plugin,arena);
            case 5 -> disaster = new ZombieDisaster(plugin,arena);
            case 6 -> disaster = new HotPotatoDisaster(plugin,arena);
            case 7 -> disaster = new PvpDisaster(plugin,arena);
            case 8 -> disaster = new WolfPlayerDisaster(plugin,arena);
            case 9 -> disaster = new WitherDisaster(plugin,arena);
            default -> disaster = new AcidRainDisaster(plugin,arena);
        }
        return disaster;
    }

    public Disaster getDisaster(DisasterType disasterType){
        Disaster disaster;
        switch (disasterType){
            case ACID_RAIN -> disaster = new AcidRainDisaster(plugin,arena);
            case DRAGON -> disaster = new DragonsDisaster(plugin,arena);
            case FLOOD -> disaster = new FloodDisaster(plugin,arena);
            case LIGHTING -> disaster = new LightingDisaster(plugin,arena);
            case METEOR -> disaster = new MeteorShowerDisaster(plugin,arena);
            case ZOMBIE -> disaster = new ZombieDisaster(plugin,arena);
            case PVP -> disaster = new PvpDisaster(plugin,arena);
            case WOLF_PLAYER -> disaster = new WolfPlayerDisaster(plugin,arena);
            case WITHER -> disaster = new WitherDisaster(plugin,arena);
            default -> disaster = new HotPotatoDisaster(plugin,arena);
        }
        return disaster;
    }

}
