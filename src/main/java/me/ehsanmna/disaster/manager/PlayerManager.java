package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.events.ArenaPlayerDeathEvent;
import me.ehsanmna.disaster.events.PlayerJoinArenaEvent;
import me.ehsanmna.disaster.events.PlayerPerJoinArenaEvent;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ArenaPlayer> playersInArena = new HashMap<>();

    private final DisasterPlugin plugin;
    private final LobbyManager lobbyManager;

    public PlayerManager(DisasterPlugin plugin) {
        this.plugin = plugin;
        this.lobbyManager = plugin.getLobbyManager();
    }

    public void killPlayer(ArenaPlayer arenaPlayer){

        Player player = arenaPlayer.getPlayer();
        arenaPlayer.setAlive(false);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE, 0, false,false));

        TextUtils.sendTitle(player, TextUtils.getMessage("player_death_title"), TextUtils.getMessage("player_death_subtitle"));

        ArenaPlayerDeathEvent event = new ArenaPlayerDeathEvent(arenaPlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (arenaPlayer.getArena().getArenaHandler().getPlayersPlaying().isEmpty())
            arenaPlayer.getArena().getArenaHandler().getArenaService().getGameService().finishGame();
    }

    public ArenaPlayer joinPlayer(Player player, Arena arena){
        PlayerPerJoinArenaEvent event = new PlayerPerJoinArenaEvent(player, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        ArenaPlayer arenaPlayer = arena.getArenaHandler().addPlayer(player);

        if (arena.getArenaHandler() == null) arena.enable();
        if (arena.getArenaHandler().getSlimeWorld() == null) arena.getArenaHandler().getArenaWorldHandler().checkWorld();

        addPlayerToArena(arenaPlayer);
        player.teleport(arena.getSpawn());

        PlayerJoinArenaEvent e = new PlayerJoinArenaEvent(arenaPlayer, arena);
        Bukkit.getPluginManager().callEvent(e);
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
        player.getInventory().clear();
        player.setAllowFlight(false);
        player.setFlying(false);
        player.clearActivePotionEffects();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealthScale(20.0);
        player.setHealthScaled(false);
        player.setHealth(20.0);
        player.setFoodLevel(30);
        try {player.teleport(lobbyManager.getLobbySpawn());
        }catch (IllegalArgumentException e){plugin.getLogger().warning("Could not teleport player to spawn location: "+ e.getMessage()+" - "+lobbyManager.getLobbySpawn());}
    }
}
