package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.events.ArenaPlayerDeathEvent;
import me.ehsanmna.disaster.events.ArenaStartEvent;
import me.ehsanmna.disaster.events.PlayerJoinArenaEvent;
import me.ehsanmna.disaster.events.PlayerPerJoinArenaEvent;
import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaHandler;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import me.ehsanmna.disaster.model.disaster.WolfPlayerDisaster;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.machine.PowerUpMachineImpl;
import me.ehsanmna.disaster.util.LocationUtils;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DisasterEventsListener implements Listener {

    private final DisasterPlugin plugin;
    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    public DisasterEventsListener(DisasterPlugin plugin, PlayerManager playerManager, ArenaManager arenaManager) {
        this.plugin = plugin;
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

    @EventHandler
    public void onJoin(PlayerJoinArenaEvent e){
        Arena arena = e.getArena();
        ArenaPlayer arenaPlayer = e.getPlayer();
        arena.getArenaHandler().getPlayers().forEach( arenaPlayer1 -> {
            TextUtils.sendMessage(arenaPlayer1.getPlayer(), "arena-join-player",
                    arenaPlayer.getPlayer().getName(),
                    arena.getArenaHandler().getPlayersPlaying().size()+"",
                    arena.getMaxPlayers()+""
                    );
        });
    }

    @EventHandler
    public void onStart(ArenaStartEvent e){
        Arena arena = e.getArena();

        // HANDLE POWER UP MACHINES
        for (PowerUpMachine machine : arena.getMachines().values()){
            try {
                PowerUpMachineImpl powerUpmachineImpl = (PowerUpMachineImpl) machine;
                Location hologramLocation = LocationUtils.center(powerUpmachineImpl.getButtonLocation().clone());

                powerUpmachineImpl.initializeHolograms(hologramLocation, TextUtils.getMessage("machine-click"));
                powerUpmachineImpl.initializeHolograms(hologramLocation.add(0,1,0), powerUpmachineImpl.getPowerUp().getItemStackIcon());

            }catch (Exception error){
                plugin.getLogger().severe("Error on ArenaStartEvent machine hologram load, for "+arena.getName()+"! Machine: "+machine.getName()+ ", Error: "+error.getMessage());
                plugin.getErrorLogger().logError("Failed to load machine! Arena:"+arena.getName()+", Machine: "+machine.getName(), error);
            }
        }

    }

}
