package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieDisaster extends BaseDisaster {

    private final Random random = new Random();
    private final List<Zombie> spawnedZombies = new ArrayList<>();
    private static final int MAX_ZOMBIES = 50;

    public ZombieDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-zombie-title"), TextUtils.getMessage("disaster-zombie-description"), arena, DisasterType.ZOMBIE);
    }

    @Override
    public void setup() {
        super.setup();
        spawnZombies();
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Zombie Disaster activated for arena: " + getArena().getName());
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
                    if (random.nextDouble() < 0.3) zombie.setBaby(true);
                    zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));

                    spawnedZombies.add(zombie);
                    totalZombies++;
                    zombie.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLoc,
                            10, 0.3, 0.3, 0.3, 0);
                }
            }
        }
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Spawned " + totalZombies + " zombies in arena: " + getArena().getName());
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

        Block loc = new Location(world, x, y, z).getBlock();
        Block top = world.getBlockAt(loc.getLocation().clone().add(0, 1, 0));
        if (getArena().getArenaRegion().isInRegion(loc) &&
                (loc.getType() == Material.AIR || loc.getType() == Material.VOID_AIR) &&
                (top.getType() == Material.AIR || top.getType() == Material.VOID_AIR)) {
            return loc.getLocation();
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
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Zombie Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}