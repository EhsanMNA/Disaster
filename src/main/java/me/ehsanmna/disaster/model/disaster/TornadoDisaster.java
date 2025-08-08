package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
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
    private final List<FallingBlock> floatingBlocks = new ArrayList<>();
    private final Random random = new Random();

    // Configurable parameters
    private final int DURATION = 300 * 20; // 300 seconds (5 minutes)
    private final double TORNADO_RADIUS = 5.0; // Effect radius
    private final double BLOCK_PICKUP_CHANCE = 0.5; // 50% chance
    private final int PARTICLE_INTERVAL = 2; // Ticks between particle spawns
    private final int BLOCK_PICKUP_INTERVAL = 10; // Ticks between block pickup attempts
    private final int DAMAGE_INTERVAL = 20; // Ticks between damage (1 second)
    private final double DAMAGE_AMOUNT = 2.0; // 1 heart
    private final double PULL_STRENGTH = 0.2; // How strongly players are pulled in
    private final double UPWARD_STRENGTH = 0.3; // How high players are launched
    private final int TORNADO_MOVE_INTERVAL = 100; // Ticks between location changes
    private final double MAX_MOVE_DISTANCE = 5.0; // Max distance tornado can move at once

    public TornadoDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-tornado-title"),
                TextUtils.getMessage("disaster-tornado-description"),
                arena, DisasterType.TORNADO);
    }

    @Override
    public void setup() {
        super.setup();

        // Set initial tornado center
        tornadoCenter = getArena().hasArenaRegion() ?
                getArena().getArenaRegion().getRandomLocation() :
                getArena().getSpawn().clone();

        // Ensure we're above ground
        tornadoCenter.setY(findGroundLevel(tornadoCenter) + 1);

        // Notify players
        broadcastTornadoWarning();

        new BukkitRunnable() {
            int ticks = 0;
            int nextMoveTick = TORNADO_MOVE_INTERVAL;

            @Override
            public void run() {
                if (!isActive() || ticks >= DURATION) {
                    deActive();
                    cancel();
                    return;
                }

                // Visual effects
                if (ticks % PARTICLE_INTERVAL == 0) {
                    spawnTornadoParticles(ticks);
                }

                // Move tornado periodically
                if (ticks >= nextMoveTick) {
                    moveTornado();
                    nextMoveTick = ticks + TORNADO_MOVE_INTERVAL;
                }

                // Block pickup logic
                if (ticks % BLOCK_PICKUP_INTERVAL == 0 && random.nextDouble() <= BLOCK_PICKUP_CHANCE) {
                    pickupRandomBlock();
                }

                // Player effects
                affectNearbyPlayers(ticks);

                ticks++;
            }
        }.runTaskTimer(getPlugin(), 0, 1);
    }

    private void broadcastTornadoWarning() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            player.sendTitle(
                    ChatColor.RED + "TORNADO WARNING!",
                    ChatColor.YELLOW + "A tornado has appeared!",
                    10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
        }
    }

    private int findGroundLevel(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();

        for (int y = world.getMaxHeight(); y > world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid() && block.getType() != Material.BEDROCK) {
                return y;
            }
        }
        return location.getBlockY();
    }

    private void spawnTornadoParticles(int ticks) {
        World world = tornadoCenter.getWorld();

        // Spiral particles
        for (double y = 0; y < 10; y += 0.5) {
            double radius = Math.sin(y) * 2;
            for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 8) {
                double x = radius * Math.cos(theta + (ticks * 0.1));
                double z = radius * Math.sin(theta + (ticks * 0.1));
                Location particleLoc = tornadoCenter.clone().add(x, y, z);

                // Different particles at different heights
                Particle particle = y < 5 ? Particle.CLOUD : Particle.SMOKE_LARGE;
                world.spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }

        // Base effect
        world.spawnParticle(Particle.EXPLOSION_NORMAL, tornadoCenter, 5, 1, 0, 1, 0);
    }

    private void moveTornado() {
        if (!getArena().hasArenaRegion()) return;

        // Calculate new position within arena
        Location newLocation = getArena().getArenaRegion().getRandomLocation();
        newLocation.setY(findGroundLevel(newLocation) + 1);

        // Limit movement distance
        if (newLocation.distance(tornadoCenter) > MAX_MOVE_DISTANCE) {
            Vector direction = newLocation.toVector().subtract(tornadoCenter.toVector()).normalize();
            newLocation = tornadoCenter.clone().add(direction.multiply(MAX_MOVE_DISTANCE));
        }

        tornadoCenter = newLocation;

        // Play move sound
        tornadoCenter.getWorld().playSound(tornadoCenter, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 0.5f);
    }

    private void pickupRandomBlock() {
        if (!getArena().hasArenaRegion()) return;

        // Get random location near tornado
        Location blockLoc = tornadoCenter.clone().add(
                random.nextDouble() * TORNADO_RADIUS * 2 - TORNADO_RADIUS,
                0,
                random.nextDouble() * TORNADO_RADIUS * 2 - TORNADO_RADIUS
        );

        Block block = blockLoc.getBlock();
        if (block.getType().isAir() || block.isLiquid() || block.getType() == Material.BEDROCK) {
            return;
        }

        // Break block with effects
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5),
                10, 0.3, 0.3, 0.3, 0, block.getBlockData());
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 0.8f, 0.8f + random.nextFloat() * 0.4f);

        // Create falling block
        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(
                block.getLocation().add(0.5, 0, 0.5),
                block.getBlockData()
        );

        // Apply upward and spiral motion
        Vector velocity = new Vector(
                (random.nextDouble() - 0.5) * 0.2,
                0.5 + random.nextDouble() * 0.3,
                (random.nextDouble() - 0.5) * 0.2
        );
        fallingBlock.setVelocity(velocity);
        fallingBlock.setDropItem(false);
        floatingBlocks.add(fallingBlock);

        // Remove original block
        block.setType(Material.AIR);
    }

    private void affectNearbyPlayers(int ticks) {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            double distance = player.getLocation().distance(tornadoCenter);

            if (distance <= TORNADO_RADIUS) {
                // Damage player at intervals
                if (ticks % DAMAGE_INTERVAL == 0) {
                    player.damage(DAMAGE_AMOUNT);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
                }

                // Pull player toward center and upward
                Vector direction = tornadoCenter.toVector().subtract(player.getLocation().toVector());
                direction.normalize().multiply(PULL_STRENGTH * (1 - (distance / TORNADO_RADIUS)));
                direction.setY(UPWARD_STRENGTH);
                player.setVelocity(player.getVelocity().add(direction));

                // Visual effect on player
                player.spawnParticle(Particle.CRIT, player.getLocation(), 5, 0.5, 0.5, 0.5, 0);
            }
        }
    }

    @Override
    public void deActive() {
        super.deActive();

        // Remove all floating blocks
        for (FallingBlock block : floatingBlocks) {
            if (block.isValid()) {
                block.remove();
            }
        }
        floatingBlocks.clear();

        // Play ending sound
        if (tornadoCenter != null) {
            tornadoCenter.getWorld().playSound(tornadoCenter, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
        }

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