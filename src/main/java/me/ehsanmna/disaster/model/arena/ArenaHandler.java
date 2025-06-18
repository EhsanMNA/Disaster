package me.ehsanmna.disaster.model.arena;

import com.infernalsuite.aswm.api.world.SlimeWorld;
import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.disaster.Disaster;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import me.ehsanmna.disaster.service.ArenaService;
import me.ehsanmna.disaster.util.SlimeWorldUtils;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ArenaHandler {

    private final Arena arena;
    private final ArenaService arenaService;
    private final ArenaWorldHandler arenaWorldHandler;
    private boolean isRunning;
    private int gameTime;
    private SlimeWorld slimeWorld;
    List<Disaster> disasters = new ArrayList<>();
    List<ArenaPlayer> players = new ArrayList<>();

    public ArenaHandler(DisasterPlugin plugin, Arena arena) {
        this.arena = arena;
        this.arenaService = new ArenaService(plugin ,arena);
        this.isRunning = false;
        this.gameTime = arena.getTime();
        this.arenaWorldHandler = new ArenaWorldHandler(arena);
    }

    public Arena getArena() {
        return arena;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getGameTime() {
        return gameTime;
    }

    public ArenaService getArenaService() {
        return arenaService;
    }

    public List<Disaster> getDisasters() {
        return disasters;
    }

    public List<ArenaPlayer> getPlayers() {
        return players;
    }

    public ArenaWorldHandler getArenaWorldHandler() {
        return arenaWorldHandler;
    }

    public void addDisaster(Disaster disaster) {
        disasters.add(disaster);
        disaster.act();
    }

    public ArenaPlayer addPlayer(Player player){
        player.getInventory().clear();

        ArenaPlayer arenaPlayer = new ArenaPlayer(player);
        arenaPlayer.setArena(arena);
        players.add(arenaPlayer);
        arenaService.getWaitingService().handleWaiting();
        return arenaPlayer;
    }

    public ArenaPlayer addSpectator(Player player){
        if (getPlayersPlaying().size() == arena.getMaxPlayers()) return null;

        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        ArenaPlayer arenaPlayer = new ArenaPlayer(player);
        arenaPlayer.setArena(arena);
        arenaPlayer.setAlive(false);
        players.add(arenaPlayer);
        return arenaPlayer;
    }

    public void removeDisaster(Disaster disaster) {
        disasters.removeIf(disaster1 -> disaster1.getClass().getName().equalsIgnoreCase(disaster.getClass().getName()));
    }

    public boolean hasDisaster(DisasterType disaster){
        for (Disaster disaster1 : getDisasters())
            if (disaster1.getType() == disaster) return true;
        return false;
    }

    public void removePlayer(ArenaPlayer arenaPlayer){
        players.remove(arenaPlayer);
    }

    public void removePlayer(Player player) {
        players.removeIf(p -> p.getPlayer().equals(player));
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public void tick() {
        gameTime--;
    }

    public SlimeWorld getSlimeWorld() {
        return slimeWorld;
    }

    public void setSlimeWorld(SlimeWorld slimeWorld) {
        this.slimeWorld = slimeWorld;
    }

    public int getWaitingTime() {
        return arenaService.getWaitingService().getTimer();
    }

    public void setWaitingTime(int waitingTime) {
        this.arenaService.getWaitingService().setTimer(waitingTime);
    }

    public List<ArenaPlayer> getPlayersPlaying(){
        return players.stream().filter(ArenaPlayer::isAlive).toList();
    }

    public List<ArenaPlayer> getSpectators(){
        return players.stream().filter(arenaPlayer -> !arenaPlayer.isAlive()).toList();
    }

    public void announce(String message, String... placeholders){
        for (ArenaPlayer arenaPlayer : players)
            TextUtils.sendMessage(arenaPlayer.getPlayer(), message, placeholders);
    }
}
