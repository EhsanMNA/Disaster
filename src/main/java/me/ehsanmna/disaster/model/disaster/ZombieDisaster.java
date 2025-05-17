package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieDisaster extends BaseDisaster {

    private final DisasterPlugin plugin;
    private final Random random = new Random();
    private final List<Zombie> spawnedZombies = new ArrayList<>();
    private boolean hasSpawned = false;
    private static final int MAX_ZOMBIES = 50;

    public ZombieDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin,"<green><bold>ZOMBIES", "<white>These are not humans!", arena, DisasterType.ZOMBIE);
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        super.setup();
        spawnZombies();
        hasSpawned = true;
        Bukkit.getLogger().info("Zombie Disaster activated for arena: " + getArena().getName());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cleanupZombies();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void spawnZombies() {
        int totalZombies = 0;
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            int zombiesToSpawn = random.nextInt(5) + 1;
            int remainingCapacity = MAX_ZOMBIES - totalZombies;
            zombiesToSpawn = Math.min(zombiesToSpawn, remainingCapacity);

            for (int i = 0; i < zombiesToSpawn && totalZombies < MAX_ZOMBIES; i++) {
                Location spawnLoc = getRandomLocationAroundPlayer(player);
                if (spawnLoc != null) {
                    Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
                    if (random.nextDouble() < 0.3) {
                        zombie.setBaby(true);
                    }
                    spawnedZombies.add(zombie);
                    totalZombies++;
                    zombie.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLoc,
                            10, 0.3, 0.3, 0.3, 0);
                }
            }
        }
        Bukkit.getLogger().info("Spawned " + totalZombies + " zombies in arena: " + getArena().getName());
    }

    private Location getRandomLocationAroundPlayer(Player player) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = 10;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius + 5; // Min 5 blocks away
        int x = (int) (playerLoc.getX() + Math.cos(angle) * distance);
        int z = (int) (playerLoc.getZ() + Math.sin(angle) * distance);
        int y = world.getHighestBlockYAt(x, z) + 1;

        Location loc = new Location(world, x, y, z);
        if (getArena().getArenaRegion().isInRegion(loc) &&
                world.getBlockAt(loc).getType() == Material.AIR &&
                world.getBlockAt(loc.clone().add(0, 1, 0)).getType() == Material.AIR) {
            return loc;
        }
        return null;
    }

    private void cleanupZombies() {
        for (Zombie zombie : spawnedZombies) {
            if (zombie != null && !zombie.isDead()) {
                zombie.getWorld().spawnParticle(Particle.SMOKE_NORMAL, zombie.getLocation(),
                        10, 0.3, 0.3, 0.3, 0);
                zombie.remove();
            }
        }
        spawnedZombies.clear();
    }

    @Override
    public void deActive() {
        super.deActive();
        cleanupZombies();
        Bukkit.getLogger().info("Zombie Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}