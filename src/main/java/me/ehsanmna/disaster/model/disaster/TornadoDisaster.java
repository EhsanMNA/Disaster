package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TornadoDisaster extends BaseDisaster {

    private Location tornadoCenter;
    private final Random random = new Random();
    private final List<FallingBlock> floatingBlocks = new ArrayList<>();

    public TornadoDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-tornado-title"), TextUtils.getMessage("disaster-tornado-description"), arena, DisasterType.TORNADO);
    }

    @Override
    public void setup() {
        super.setup();

        // Set tornado center to a random location in the arena region
        if (getArena().hasArenaRegion()) {
            tornadoCenter = getArena().getArenaRegion().getRandomLocation();
        } else {
            tornadoCenter = getArena().getSpawn();
        }

        new BukkitRunnable() {
            int ticks = 0;
            final int duration = 300 * 20; // 300 seconds

            @Override
            public void run() {
                if (ticks >= duration || !isActive()) {
                    deActive();
                    cancel();
                    return;
                }

                // Spawn particles for tornado effect (spiral shape)
                for (double y = 0; y < 10; y += 0.5) {
                    double radius = Math.sin(y) * 2;
                    for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 16) {
                        double x = radius * Math.cos(theta);
                        double z = radius * Math.sin(theta);
                        tornadoCenter.getWorld().spawnParticle(Particle.CLOUD, tornadoCenter.clone().add(x, y, z), 1, 0, 0, 0, 0);
                    }
                }

                // Randomly pick up blocks
                if (ticks % 20 == 0 && getArena().hasArenaRegion()) { // Every second
                    Location blockLoc = getArena().getArenaRegion().getRandomLocation();
                    Block block = blockLoc.getBlock();
                    if (block.getType() != Material.AIR && !block.isLiquid()) {
                        Material material = block.getType();
                        block.setType(Material.AIR);
                        FallingBlock fallingBlock = blockLoc.getWorld().spawnFallingBlock(blockLoc, material.createBlockData());
                        fallingBlock.setVelocity(new Vector(0, 0.5, 0)); // Float upward
                        floatingBlocks.add(fallingBlock);
                    }
                }

                // Affect players in tornado radius (5 blocks)
                for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
                    Player player = arenaPlayer.getPlayer();
                    Location playerLoc = player.getLocation();
                    double distance = playerLoc.distance(tornadoCenter);
                    if (distance <= 5) {
                        // Damage player (1 heart every second)
                        if (ticks % 20 == 0) {
                            player.damage(2.0); // 1 heart
                        }
                        // Push toward center and upward
                        Vector direction = tornadoCenter.toVector().subtract(playerLoc.toVector()).normalize().multiply(0.2);
                        direction.setY(0.3); // Upward push
                        player.setVelocity(direction);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(getPlugin(), 0, 1); // Run every tick for smooth visuals
    }

    @Override
    public void deActive() {
        super.deActive();

        // Remove floating blocks
        for (FallingBlock block : floatingBlocks) {
            block.remove();
        }
        floatingBlocks.clear();

        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Tornado Disaster deactivated for arena: " + getArena().getName());
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}