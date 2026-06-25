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
import java.util.Arrays;
import java.util.Collections;
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
        for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers()) {
            Player player = arenaPlayer.getPlayer();
            player.teleport(arena.getSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
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
                if (loopedTimes >= arena.getArenaConfig().time() || arena.getArenaHandler().getPlayersPlaying().isEmpty()) {
                    finishGame();
                    return;
                }

                for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers()) {
                    if (arenaPlayer.isAlive()) {
                        TextUtils.sendActionbar(arenaPlayer.getPlayer(), "game-actionbar-timer", arena.getArenaHandler().getGameTime() + "");
                    } else {
                        TextUtils.sendActionbar(arenaPlayer.getPlayer(), "game-actionbar-spectator", arena.getArenaHandler().getGameTime() + "");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void finishGame() {
        ArenaEndEvent event = new ArenaEndEvent(arena);
        Bukkit.getPluginManager().callEvent(event);

        task.cancel();
        arena.getArenaHandler().announce("game-finish-header");
        StringBuilder stringBuilder = new StringBuilder();
        for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers())
            if (arenaPlayer.isAlive()) stringBuilder.append(arenaPlayer.getPlayer().getName()).append(", ");
        if (stringBuilder.isEmpty()) stringBuilder.append("No One");
        else stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        arena.getArenaHandler().announce("game-finish-title", stringBuilder.toString());
        arena.getArenaHandler().announce("game-finish-footer");

        arena.getArenaHandler().setGameTime(arena.getArenaConfig().time());
        try {arena.getArenaHandler().getDisasters().forEach(Disaster::deActive);
        }catch (Exception ignored){}
        arena.getArenaHandler().getDisasters().clear();

        new BukkitRunnable() {
            @Override
            public void run() {
                arenaService.finishGame(true);
            }
        }.runTaskLater(plugin, 100);
    }

    public boolean addDisaster(String disasterName) {
        return addDisaster(disasterName, false);
    }

    public boolean addDisaster(String disasterName, boolean sendMessage) {
        try {
            DisasterType disasterType = DisasterType.valueOf(disasterName.toUpperCase());
            addDisaster(disasterType, sendMessage);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private void addDisaster(DisasterType disasterType, boolean sendMessage) {
        Disaster disaster = getDisaster(disasterType);
        arena.getArenaHandler().addDisaster(disaster);
        if (sendMessage) {
            arena.getArenaHandler().announce("game-disaster-header");
            arena.getArenaHandler().announce("game-disaster-title");
            arena.getArenaHandler().announce("game-disaster-description", disaster.getName(), disaster.getDescription());
            arena.getArenaHandler().announce("game-disaster-footer");
        }
    }

    public void addRandomDisasters(boolean sendMessage) {
        Random random = new Random();
        int numDisasters = random.nextInt(2) + 1;

        // Get all possible disaster types except CUSTOME
        List<DisasterType> availableDisasters = new ArrayList<>(Arrays.asList(DisasterType.values()));
        availableDisasters.remove(DisasterType.CUSTOME);

        // Remove types of active disasters to avoid duplicates
        for (Disaster activeDisaster : arena.getArenaHandler().getDisasters()) {
            availableDisasters.remove(activeDisaster.getType());
        }

        // Shuffle to randomize selection
        Collections.shuffle(availableDisasters, random);

        // Select up to numDisasters from available ones
        List<Disaster> selectedDisasters = new ArrayList<>();
        int disastersToAdd = Math.min(numDisasters, availableDisasters.size());
        for (int i = 0; i < disastersToAdd; i++) {
            Disaster disaster = getDisaster(availableDisasters.get(i));
            selectedDisasters.add(disaster);
        }

        // Handle case where no disasters are available
        if (selectedDisasters.isEmpty()) {
            if (arena.getArenaHandler().getArenaService().isDebug()) {
                plugin.getLogger().info("No available disasters to add in arena: " + arena.getName());
            }
            arena.getArenaHandler().announce("No disaster found ... (report admin! " + numDisasters + ")");
            return;
        }

        // Announce and add disasters
        if (sendMessage) {
            arena.getArenaHandler().announce("game-disaster-header");
            arena.getArenaHandler().announce("game-disaster-title");
        }
        for (Disaster disaster : selectedDisasters) {
            arena.getArenaHandler().addDisaster(disaster);
            if (sendMessage) arena.getArenaHandler().announce("game-disaster-description", disaster.getName(), disaster.getDescription());

            if (arena.getArenaHandler().getArenaService().isDebug()) plugin.getLogger().info("Added disaster " + disaster.getName() + " to arena: " + arena.getName());

        }
        if (sendMessage) arena.getArenaHandler().announce("game-disaster-footer");
        
    }

    private Disaster getRandomDisaster() {
        Disaster disaster;
        Random random = new Random();
        int index = random.nextInt(18);
        switch (index) {
            case 1 -> disaster = new DragonsDisaster(plugin, arena);
            case 2 -> disaster = new FloodDisaster(plugin, arena);
            case 3 -> disaster = new LightingDisaster(plugin, arena);
            case 4 -> disaster = new MeteorShowerDisaster(plugin, arena);
            case 5 -> disaster = new ZombieDisaster(plugin, arena);
            case 6 -> disaster = new HotPotatoDisaster(plugin, arena);
            case 7 -> disaster = new PvpDisaster(plugin, arena);
            case 8 -> disaster = new WolfPlayerDisaster(plugin, arena);
            case 9 -> disaster = new WitherDisaster(plugin, arena);
            case 10 -> disaster = new SwapDisaster(plugin, arena);
            case 11 -> disaster = new BlindDisaster(plugin, arena);
            case 12 -> disaster = new HalfHealthDisaster(plugin, arena);
            case 13 -> disaster = new NoJumpDisaster(plugin, arena);
            case 14 -> disaster = new TornadoDisaster(plugin, arena);
            case 15 -> disaster = new RedLightGreenLightDisaster(plugin, arena);
            case 16 -> disaster = new GroundAwayDisaster(plugin, arena);
            default -> disaster = new AcidRainDisaster(plugin, arena);
        }
        return disaster;
    }

    public Disaster getDisaster(DisasterType disasterType) {
        Disaster disaster;
        switch (disasterType) {
            case ACID_RAIN -> disaster = new AcidRainDisaster(plugin, arena);
            case DRAGON -> disaster = new DragonsDisaster(plugin, arena);
            case FLOOD -> disaster = new FloodDisaster(plugin, arena);
            case LIGHTING -> disaster = new LightingDisaster(plugin, arena);
            case METEOR -> disaster = new MeteorShowerDisaster(plugin, arena);
            case ZOMBIE -> disaster = new ZombieDisaster(plugin, arena);
            case PVP -> disaster = new PvpDisaster(plugin, arena);
            case WOLF_PLAYER -> disaster = new WolfPlayerDisaster(plugin, arena);
            case WITHER -> disaster = new WitherDisaster(plugin, arena);
            case SWAP -> disaster = new SwapDisaster(plugin, arena);
            case BLIND -> disaster = new BlindDisaster(plugin, arena);
            case HALF_HEALTH -> disaster = new HalfHealthDisaster(plugin, arena);
            case NO_JUMP -> disaster = new NoJumpDisaster(plugin, arena);
            case TORNADO -> disaster = new TornadoDisaster(plugin, arena);
            case RED_LIGHT_GREEN_LIGHT -> disaster = new RedLightGreenLightDisaster(plugin, arena);
            case GROUND_AWAY -> disaster = new GroundAwayDisaster(plugin, arena);
            default -> disaster = new HotPotatoDisaster(plugin, arena);
        }
        return disaster;
    }
}