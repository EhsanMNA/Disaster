package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.disaster.Disaster;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DisasterGameListener implements Listener {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    public DisasterGameListener(PlayerManager playerManager, ArenaManager arenaManager) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if (playerManager.isPlayerInArena(player) &&
                !playerManager.getPlayerInArena(player).getArena().getArenaHandler().getArenaService().isDebug()) e.setCancelled(true);
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
            if (type.toString().contains("DOOR")) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player player){
            if (playerManager.isPlayerInArena(player)){
                ArenaPlayer arenaPlayer = playerManager.getPlayerInArena(player);
                if (!arenaPlayer.isAlive()){
                    e.setCancelled(true);
                    return;
                }
                if (player.getHealth() <= e.getDamage()){
                    playerManager.killPlayer(arenaPlayer);
                    e.setCancelled(true);
                    player.setHealth(player.getMaxHealth());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player damaged){
            if (playerManager.isPlayerInArena(damaged) && playerManager.isPlayerInArena(damager)){
                Arena arena = playerManager.getPlayerInArena(damager).getArena();
                if (arena.getArenaHandler().hasDisaster(DisasterType.HOT_POTATO)){
                    for (Disaster disaster : arena.getArenaHandler().getDisasters())
                        if (disaster.getType() == DisasterType.HOT_POTATO && disaster.isActive())
                            for (int i = 0; i < 35; i++){
                                ItemStack item = damager.getInventory().getItem(i);
                                if (item != null && item.getType() == Material.POISONOUS_POTATO){
                                    damager.getInventory().removeItem(item);
                                    damaged.getInventory().addItem(item);
                                    e.setDamage(1);
                                    return;
                                }
                            }
                }
                e.setCancelled(true);
            }
        }
    }

}
