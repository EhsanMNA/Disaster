package me.ehsanmna.disaster.listener;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import me.ehsanmna.disaster.events.ArenaPlayerDeathEvent;
import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.Disaster;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisasterGameListener implements Listener {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    public DisasterGameListener(PlayerManager playerManager, ArenaManager arenaManager) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if (playerManager.isPlayerInArena(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if (playerManager.isPlayerInArena(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onInterAct(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && playerManager.isPlayerInArena(player)) {
            Material type = e.getClickedBlock().getType();
            if (type.toString().contains("DOOR") || type.toString().contains("BUTTON")) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if (playerManager.isPlayerInArena(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent e){
        Player player = e.getPlayer();
        if (playerManager.isPlayerInArena(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player player){
            if (playerManager.isPlayerInArena(player)){
                ArenaPlayer arenaPlayer = playerManager.getPlayerInArena(player);
                if (!arenaPlayer.isAlive()) e.setCancelled(true);
                else if (player.getHealth() <= e.getDamage()){
                    playerManager.killPlayer(arenaPlayer);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) {
        if (playerManager.isPlayerInArena((Player)e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        // Only cancel natural regeneration, not healing from potions/gapples/etc
        if (event.getEntity() instanceof Player player)
            if (playerManager.isPlayerInArena(player))
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
                        event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) event.setCancelled(true);
    }

}
