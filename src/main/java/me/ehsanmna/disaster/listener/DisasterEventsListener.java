package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.events.ArenaPlayerDeathEvent;
import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
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
        if (!e.isCancelled())
            if (e.getArena().getArenaHandler().getPlayersPlaying().size() == 1)
                e.getArena().getArenaHandler().getArenaService().getGameService().finishGame();

    }

}
