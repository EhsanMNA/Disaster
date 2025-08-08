package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
            if (!playerManager.getPlayerInArena(player).isAlive()){e.setCancelled(true); return;}
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
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) arenaPlayer.getPlayer().teleport(arenaPlayer.getArena().getSpawn());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player player && e.getDamager() instanceof Player damager){
            if (playerManager.isPlayerInArena(player) && playerManager.isPlayerInArena(damager)){
                ArenaPlayer arenaPlayer = playerManager.getPlayerInArena(player);
                ArenaPlayer arenaDamager = playerManager.getPlayerInArena(damager);
                if (!arenaPlayer.isAlive() || !arenaDamager.isAlive()) e.setCancelled(true);
                else if (player.getHealth() <= e.getDamage()){
                    playerManager.killPlayer(arenaPlayer);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player player)
          if (playerManager.isPlayerInArena(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        // Only cancel natural regeneration, not healing from potions/gapples/etc
        if (event.getEntity() instanceof Player player)
            if (playerManager.isPlayerInArena(player))
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
                        event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (playerManager.isPlayerInArena(player)) playerManager.leavePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (playerManager.isPlayerInArena(player)){
                ArenaPlayer arenaPlayer = playerManager.getPlayerInArena(player);
                if (!arenaPlayer.isAlive()) event.setCancelled(true);
            }
        }
    }

}