package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class LightingDisaster extends BaseDisaster{

    private final Random random = new Random();
    private BukkitRunnable task;

    public LightingDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<yellow><bold>LIGHTING", "<white>Lighting will summon on the map.", arena, DisasterType.LIGHTING);
    }


    @Override
    public void setup() {
        super.setup();
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cancel();
                    return;
                }
                strikeRandomLightning();
            }
        };
        task.runTaskTimer(getPlugin(), 0L, 100L); // Every 5 seconds
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Lightning Disaster activated for arena: " + getArena().getName());
    }

    @Override
    public void deActive() {
        super.deActive();
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Lightning Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

    private void strikeRandomLightning() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            Location strikeLocation = getRandomLocationAroundPlayer(player);
            if (strikeLocation == null) continue;

            World world = strikeLocation.getWorld();
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 60 || !isActive()) {
                        world.strikeLightningEffect(strikeLocation);
                        world.createExplosion(strikeLocation, 0F, false, false);
                        explodeBlocks(strikeLocation);
                        damageNearbyPlayers(strikeLocation);
                        cancel();
                        return;
                    }
                    world.spawnParticle(Particle.VILLAGER_HAPPY, strikeLocation.clone().add(0, 1, 0),
                            10, 0.3, 0.3, 0.3, 0);
                    ticks += 10;
                }
            }.runTaskTimer(getPlugin(), 0L, 10L);
        }
    }

    private Location getRandomLocationAroundPlayer(Player player) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = 10; // 10 blocks around player
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius;
        int x = (int) (playerLoc.getX() + Math.cos(angle) * distance);
        int z = (int) (playerLoc.getZ() + Math.sin(angle) * distance);
        int y = world.getHighestBlockYAt(x, z) + 1;

        Location loc = new Location(world, x, y, z);
        return getArena().getArenaRegion().isInRegion(loc) ? loc : null;
    }

    private void explodeBlocks(Location center) {
        World world = center.getWorld();
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if (center.distance(loc) <= radius && loc.getBlock().getType() != Material.BEDROCK) {
                        loc.getBlock().breakNaturally();
                    }
                }
            }
        }
    }

    private void damageNearbyPlayers(Location center) {
        double radius = 3.0;
        for (Player player : center.getWorld().getNearbyEntitiesByType(Player.class, center, radius)) {
            if (getArena().getArenaRegion().isInRegion(player.getLocation())) {
                player.damage(4.0);
            }
        }
    }
}
