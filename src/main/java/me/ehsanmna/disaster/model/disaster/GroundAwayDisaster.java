package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroundAwayDisaster extends BaseDisaster {

    private Location center;
    private final List<Block> affectedBlocks = new ArrayList<>();
    private final Random random = new Random();

    // Configuration
    public static int MAX_RADIUS = 30; // Maximum radius of the hole
    public static int EXPAND_INTERVAL = 20; // Ticks between expansions (1 second)
    public static int DURATION = 30 * 20; // Total duration in ticks (30 seconds)
    public static int PARTICLE_DELAY = 10; // Ticks before block breaks after particles

    public GroundAwayDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-groundaway-title"),
                TextUtils.getMessage("disaster-groundaway-description"),
                arena, DisasterType.GROUND_AWAY);
    }

    @Override
    public void setup() {
        super.setup();

        // Set center to a random location in the arena region or spawn
        if (getArena().hasArenaRegion()) {
            center = getArena().getArenaRegion().getRandomLocation();
        } else {
            center = getArena().getSpawn().clone();
        }

        // Ensure we're working with the ground level
        center.setY(findGroundLevel(center));

        // Notify players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            player.sendMessage(ChatColor.RED + "The ground is collapsing!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        }

        new BukkitRunnable() {
            int radius = 0;
            int ticks = 0;

            @Override
            public void run() {
                if (!isActive() || ticks >= DURATION) {
                    deActive();
                    cancel();
                    return;
                }

                // Expand the hole every EXPAND_INTERVAL ticks
                if (ticks % EXPAND_INTERVAL == 0 && radius <= MAX_RADIUS) {
                    expandHole(radius);
                    radius++;
                }

                ticks++;
            }
        }.runTaskTimer(getPlugin(), 0, 1);
    }

    private int findGroundLevel(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();

        // Start from the highest point and go down until we find a solid block
        for (int y = world.getMaxHeight(); y > world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid() && block.getType() != Material.BEDROCK) {
                return y;
            }
        }
        return location.getBlockY();
    }

    private void expandHole(int radius) {
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        // Calculate the circle of blocks at this radius
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                // Check if this block is approximately at the current radius
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                    // Break all blocks from ground level down to bedrock (or min height)
                    for (int y = centerY; y >= world.getMinHeight(); y--) {
                        Block block = world.getBlockAt(x, y, z);

                        // Skip air and liquids
                        if (block.getType() == Material.AIR || block.isLiquid()) {
                            continue;
                        }

                        // Skip bedrock
                        if (block.getType() == Material.BEDROCK) {
                            break;
                        }

                        // Add to affected blocks list
                        affectedBlocks.add(block);

                        // Show breaking particles
                        world.spawnParticle(
                                Particle.BLOCK_CRACK,
                                block.getLocation().add(0.5, 0.5, 0.5),
                                20, // Count
                                0.25, 0.25, 0.25, // Offsets
                                0.1, // Extra
                                block.getBlockData()
                        );

                        // Schedule block removal after delay
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                block.setType(Material.AIR);
                                // Play break sound
                                world.playSound(
                                        block.getLocation(),
                                        Sound.BLOCK_STONE_BREAK,
                                        1.0f,
                                        0.8f + random.nextFloat() * 0.4f
                                );
                            }
                        }.runTaskLater(getPlugin(), PARTICLE_DELAY);
                    }
                }
            }
        }

        // Play warning sound at the edge
        Location edgeLocation = center.clone().add(radius, 0, 0);
        world.playSound(edgeLocation, Sound.BLOCK_LAVA_EXTINGUISH, 0.5f, 1.0f);
    }

    @Override
    public void deActive() {
        super.deActive();
        affectedBlocks.clear();

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