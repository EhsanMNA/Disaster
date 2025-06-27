package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GroundAwayDisaster extends BaseDisaster {

    private Location center;

    public GroundAwayDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-groundaway-title"), TextUtils.getMessage("disaster-groundaway-description"), arena, DisasterType.GROUND_AWAY);
    }

    @Override
    public void setup() {
        super.setup();

        // Set center to a random location in the arena region or spawn
        if (getArena().hasArenaRegion()) {
            center = getArena().getArenaRegion().getRandomLocation();
        } else {
            center = getArena().getSpawn();
        }

        // Notify players
        for (Player player : getArena().getArenaHandler().getPlayersPlaying().stream().map(ArenaPlayer::getPlayer).toList()) {
            TextUtils.sendMessage(player, "disaster-groundaway-notify");
        }

        new BukkitRunnable() {
            int radius = 0;
            final int maxRadius = 30;
            final int duration = 30 * 20; // 30 seconds
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || !isActive()) {
                    deActive();
                    cancel();
                    return;
                }

                // Every second (20 ticks), expand the hole
                if (ticks % 20 == 0 && radius < maxRadius) {
                    World world = center.getWorld();
                    int centerX = center.getBlockX();
                    int centerZ = center.getBlockZ();
                    int y = center.getBlockY();

                    // Get blocks at current radius
                    for (int x = centerX - radius; x <= centerX + radius; x++) {
                        for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                            if (Math.round(Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2))) == radius) {
                                Block block = world.getBlockAt(x, y, z);
                                if (block.getType() != Material.AIR && !block.isLiquid()) {
                                    // Spawn block break particles
                                    world.spawnParticle(org.bukkit.Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0, block.getBlockData());
                                    // Schedule block removal after 10 ticks
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            block.setType(Material.AIR);
                                        }
                                    }.runTaskLater(getPlugin(), 10);
                                }
                            }
                        }
                    }
                    radius++;
                }

                ticks++;
            }
        }.runTaskTimer(getPlugin(), 0, 1); // Run every tick for precise timing

        // Deactivate after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                deActive();
                getArena().getArenaHandler().removeDisaster(GroundAwayDisaster.this);
            }
        }.runTaskLater(getPlugin(), 30 * 20);
    }

    @Override
    public void deActive() {
        super.deActive();
        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Ground Away Disaster deactivated for arena: " + getArena().getName());
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}