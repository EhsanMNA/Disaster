package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DragonsDisaster extends BaseDisaster {

    private final Random random = new Random();
    private BukkitRunnable dragonTask;
    private final List<EnderDragon> dragons = new ArrayList<>();
    private final int maxY;
    private final int minY;

    public DragonsDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<purple><bold>DRAGONS", "<white>Dragons has been spawned on the map.", arena, DisasterType.DRAGON);
        this.maxY = Math.max(arena.getArenaHandler().getArena().getArenaRegion().getPos1().getBlockY(),
                arena.getArenaHandler().getArena().getArenaRegion().getPos2().getBlockY());
        this.minY = Math.min(arena.getArenaHandler().getArena().getArenaRegion().getPos1().getBlockY(),
                arena.getArenaHandler().getArena().getArenaRegion().getPos2().getBlockY());
    }

    @Override
    public void setup() {
        super.setup();
        spawnDragons();
        dragonTask = new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Dragons!");
                if (!isActive()) {
                    cleanupDragons();
                    cancel();
                    return;
                }
                moveDragons();
                affectArea();
            }
        };
        dragonTask.runTaskTimer(getPlugin(), 0L, 20L);
        Bukkit.getLogger().info("Dragons Disaster activated for arena: " + getArena().getName());
    }

    private void spawnDragons() {
        World world = getArena().getArenaRegion().getPos1().getWorld();
        int spawned = 0;
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            if (spawned >= 2) break;
            Player player = arenaPlayer.getPlayer();
            Location spawnLoc = getRandomLocationAroundPlayer(player);
            if (spawnLoc != null) {
                spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc.getBlockX(), spawnLoc.getBlockZ()) + 10);
                EnderDragon dragon = (EnderDragon) world.spawnEntity(spawnLoc, EntityType.ENDER_DRAGON);
                dragon.setCustomName("Disaster Dragon " + (spawned + 1));
                dragon.setCustomNameVisible(true);
                dragons.add(dragon);
                spawned++;
                world.spawnParticle(Particle.DRAGON_BREATH, spawnLoc, 50, 1, 1, 1, 0.1);
                world.playSound(spawnLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            }
        }
    }

    private void moveDragons() {
        List<ArenaPlayer> players = getArena().getArenaHandler().getPlayersPlaying();
        if (players.isEmpty()) return;

        for (EnderDragon dragon : dragons) {
            if (dragon.isDead()) continue;
            ArenaPlayer randomPlayer = players.get(random.nextInt(players.size()));
            Location targetLoc = getRandomLocationAroundPlayer(randomPlayer.getPlayer());
            if (targetLoc != null) {
                targetLoc.setY(random.nextInt(maxY - minY + 1) + minY);
                dragon.teleport(targetLoc);
            }
        }
    }

    private Location getRandomLocationAroundPlayer(Player player) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = 15;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius + 5;
        int x = (int) (playerLoc.getX() + Math.cos(angle) * distance);
        int z = (int) (playerLoc.getZ() + Math.sin(angle) * distance);

        Location loc = new Location(world, x, 0, z);
        return getArena().getArenaRegion().isInRegion(loc) ? loc : null;
    }

    private void affectArea() {
        for (EnderDragon dragon : dragons) {
            if (dragon.isDead()) continue;
            Location center = dragon.getLocation();
            World world = center.getWorld();
            int radius = 3;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location loc = center.clone().add(x, y, z);
                        if (center.distance(loc) <= radius && getArena().getArenaRegion().isInRegion(loc)) {
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

            double knockRadius = 5.0;
            for (Player player : world.getNearbyEntitiesByType(Player.class, center, knockRadius)) {
                if (getArena().getArenaRegion().isInRegion(player.getLocation())) {
                    player.setVelocity(new Vector(0, 2, 0));
                    player.damage(2.0);
                    world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(),
                            20, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
                }
            }
        }
    }

    private void cleanupDragons() {
        for (EnderDragon dragon : dragons) {
            if (dragon != null && !dragon.isDead()) {
                Location loc = dragon.getLocation();
                dragon.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 50, 1, 1, 1, 0.1);
                dragon.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
                dragon.remove();
            }
        }
        dragons.clear();
    }

    @Override
    public void deActive() {
        super.deActive();
        if (dragonTask != null) {
            dragonTask.cancel();
            dragonTask = null;
        }
        cleanupDragons();
        Bukkit.getLogger().info("Dragons Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}