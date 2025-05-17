package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ArenaPlayer> playersInArena = new HashMap<>();

    private final LobbyManager lobbyManager;

    public PlayerManager(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    public void killPlayer(ArenaPlayer arenaPlayer){
        arenaPlayer.setAlive(false);
        arenaPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
        arenaPlayer.getPlayer().getInventory().clear();
        arenaPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE, 0, false,false));
    }

    public ArenaPlayer joinPlayer(Player player, Arena arena){
        ArenaPlayer arenaPlayer = arena.getArenaHandler().addPlayer(player);
        addPlayerToArena(arenaPlayer);
        player.teleport(arena.getSpawn());
        return arenaPlayer;
    }

    public ArenaPlayer spectatePlayer(Player player, Arena arena){
        ArenaPlayer arenaPlayer = arena.getArenaHandler().addSpectator(player);
        addPlayerToArena(arenaPlayer);
        player.teleport(arena.getSpawn());
        return arenaPlayer;
    }

    private void addPlayerToArena(ArenaPlayer arenaPlayer) {
        playersInArena.put(arenaPlayer.getPlayer().getUniqueId(), arenaPlayer);
    }

    public boolean isPlayerInArena(Player player){
        return playersInArena.containsKey(player.getUniqueId());
    }

    public ArenaPlayer getPlayerInArena(Player player) {
        return getPlayerInArena(player.getUniqueId());
    }

    public ArenaPlayer getPlayerInArena(UUID uuid) {
        return playersInArena.get(uuid);
    }

    public void removePlayerFromArena(UUID id){
        ArenaPlayer arenaPlayer = playersInArena.remove(id);
        arenaPlayer.getArena().getArenaHandler().removePlayer(arenaPlayer);
    }

    public void leavePlayer(Player player) {
        removePlayerFromArena(player.getUniqueId());
        player.teleport(lobbyManager.getLobbySpawn());
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.setGameMode(GameMode.SURVIVAL);
    }
}
