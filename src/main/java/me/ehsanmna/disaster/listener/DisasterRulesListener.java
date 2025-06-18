package me.ehsanmna.disaster.listener;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class DisasterRulesListener implements Listener {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    public DisasterRulesListener(PlayerManager playerManager, ArenaManager arenaManager) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
    }

    //Disable entity damage like zombies
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player player)
            if(playerManager.isPlayerInArena(player))
                if (!(e.getEntity() instanceof Player)) e.setCancelled(true);
    }

    // Potato disaster listener
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePotato(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player damaged){
            if (playerManager.isPlayerInArena(damaged) && playerManager.isPlayerInArena(damager)){
                ArenaPlayer arenaPlayer = playerManager.getPlayerInArena(damager);
                Arena arena = arenaPlayer.getArena();
                if (playerManager.getPlayerInArena(damager).isAlive())
                    if (arena.getArenaHandler().hasDisaster(DisasterType.HOT_POTATO))
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

                e.setCancelled(true);
            }
        }
    }

}
