package me.ehsanmna.disaster.listener;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.events.PowerUpMachineUseEvent;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.powerup.requirement.PowerUpRequirement;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PowerUpListener implements Listener {

    private final DisasterPlugin plugin;

    public PowerUpListener(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || !isButton(block.getType())) return;


        ReadWriteNBT nbt = new NBTBlock(block).getData();
        if (!nbt.hasTag("powerup_machine")) return;
        String machineName = nbt.getString("powerup_machine");
        if (machineName == null) return;

        Player player = event.getPlayer();
        // Assuming a method to get ArenaPlayer from Player (implement in ArenaManager or similar)
        ArenaPlayer arenaPlayer = plugin.getPlayerManager().getPlayerInArena(player); // Pseudo-code: adjust based on actual implementation
        if (arenaPlayer == null || !arenaPlayer.isAlive()) return;

        PowerUpMachine machine = plugin.getPowerUpMachineManager().getMachine(arenaPlayer.getArena(), machineName);
        if (machine == null) {
            nbt.removeKey("powerup_machine");
            return;
        }

        PowerUpMachineUseEvent powerUpMachineUseEvent = new PowerUpMachineUseEvent(arenaPlayer.getArena(), machine);
        Bukkit.getPluginManager().callEvent(powerUpMachineUseEvent);

        if (powerUpMachineUseEvent.isCancelled()) return;

        boolean canUse = true;
        for (PowerUpRequirement requirement : machine.getRequirements()) {
            if (!requirement.isRequire(arenaPlayer)) {
                canUse = false;
                TextUtils.sendMessage(player, "machine-no-requirement");
                break;
            }
        }

        if (canUse) {
            for (PowerUpRequirement requirement : machine.getRequirements())
                requirement.consume(arenaPlayer);

            machine.getPowerUp().handle(arenaPlayer);
            TextUtils.sendMessage(player, "machine-activate");
        }
    }

    private boolean isButton(Material material) {
        // Check for all button types (stone, wood, etc.)
        return material.name().endsWith("_BUTTON");
    }
}