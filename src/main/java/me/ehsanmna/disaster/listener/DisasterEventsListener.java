package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.events.ArenaPlayerDeathEvent;
import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaHandler;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import me.ehsanmna.disaster.model.disaster.WolfPlayerDisaster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisasterEventsListener implements Listener {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    public DisasterEventsListener(PlayerManager playerManager, ArenaManager arenaManager) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
    }


    @EventHandler
    public void onDeath(ArenaPlayerDeathEvent e){
        Arena arena = e.getArena();
        ArenaHandler arenaHandler = arena.getArenaHandler();
        if (arenaHandler.hasDisaster(DisasterType.WOLF_PLAYER)){
            WolfPlayerDisaster disaster = (WolfPlayerDisaster) arenaHandler.getArenaService().getGameService().getDisaster(DisasterType.WOLF_PLAYER);
            if (arenaHandler.getPlayersPlaying().size() == 1 && disaster.isActive()){
                ArenaPlayer wolfPlayer = disaster.getWolfPlayer();
                if (arenaHandler.getPlayersPlaying().get(0).getPlayer().getUniqueId().equals(wolfPlayer.getPlayer().getUniqueId()))
                    arenaHandler.getArenaService().getGameService().finishGame();
            }
        }
    }

}
