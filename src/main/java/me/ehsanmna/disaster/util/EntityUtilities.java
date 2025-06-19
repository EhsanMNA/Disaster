package me.ehsanmna.disaster.util;

import me.ehsanmna.disaster.model.arena.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityUtilities {

    public static void removeBlocksAround(Location center, Arena arena, int radius){
        World world = center.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if (center.distance(loc) <= radius && arena.getArenaRegion().isInRegion(loc)) {
                        Block block = loc.getBlock();
                        if (block.getType() != Material.BEDROCK && !block.getType().isAir()) {
                            block.setType(Material.AIR);
                            world.spawnParticle(Particle.BLOCK_CRACK, loc, 10, 0.3, 0.3, 0.3, 0,
                                    block.getType().createBlockData());
                        }
                    }
                }
            }
        }
    }

    public static void knockEntities(double knockRadius, Location center, Arena arena){
        World world = center.getWorld();
        for (Player player : world.getNearbyEntitiesByType(Player.class, center, knockRadius)) {
            if (arena.getArenaRegion().isInRegion(player.getLocation())) {
                player.setVelocity(new Vector(0, 2, 0));
                player.damage(2.0);
                world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(),
                        20, 0.5, 0.5, 0.5, 0.1);
                world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
            }
        }
    }

}
