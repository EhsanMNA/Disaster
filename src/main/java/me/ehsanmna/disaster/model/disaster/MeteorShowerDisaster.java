package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class MeteorShowerDisaster extends BaseDisaster {

    private final Random random = new Random();
    private BukkitRunnable meteorTask;
    private final int maxY;  // Maximum Y level of the region

    public MeteorShowerDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<red><bold>METEOR SHOWER", "<white>Oh look! world comes to end!", arena, DisasterType.METEOR);
        this.maxY = Math.max(arena.getArenaRegion().getPos1().getBlockY(),
                arena.getArenaRegion().getPos2().getBlockY());
    }

    @Override
    public void setup() {
        super.setup();
        meteorTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cancel();
                    return;
                }
                launchMeteors();
            }
        };
        meteorTask.runTaskTimer(getPlugin(), 0L, 60L);
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Meteor Shower Disaster activated for arena: " + getArena().getName());
    }

    private void launchMeteors() {
        int fireballCount = random.nextInt(3) + 1;
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            for (int i = 0; i < fireballCount; i++) {
                getPlugin().getLogger().info("Launching fireball with 13 khordad");
                Location spawnLoc = getRandomLocationOfArena();
                if (spawnLoc == null) continue;
                getPlugin().getLogger().info("location of fireball is: "+spawnLoc.getBlockX()+", "+spawnLoc.getBlockY()+", "+spawnLoc.getBlockZ());

                spawnLoc.setY(maxY + 20);
                World world = spawnLoc.getWorld();
                Fireball fireball = (Fireball) world.spawnEntity(spawnLoc, EntityType.FIREBALL);
                fireball.setDirection(new Vector(0, -1, 0));
                fireball.setYield(0);
                world.spawnParticle(Particle.FLAME, spawnLoc, 20, 0.5, 0.5, 0.5, 0.1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fireball.isDead() || fireball.isOnGround()) {
                            Location impactLoc = fireball.getLocation();
                            if (getArena().getArenaRegion().isInRegion(impactLoc)) {
                                explodeArea(impactLoc);
                                damageNearbyPlayers(impactLoc);
                            }
                            fireball.remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(getPlugin(), 0L, 1L);
            }
        }
    }

    private Location getRandomLocationOfArena() {
        Location pos1 = getArena().getArenaRegion().getPos1();
        Location pos2 = getArena().getArenaRegion().getPos2();

        int arz = Math.max(pos1.getBlockX(), pos2.getBlockX()) - Math.min(pos1.getBlockX(), pos2.getBlockX());
        int tool = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) - Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int x = random.nextInt(arz)+1 + Math.min(pos1.getBlockX(), pos2.getBlockX());
        int z = random.nextInt(tool)+1 + Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        Location loc = new Location(Bukkit.getWorld(getArena().getWorldName()+"-backup"), x, 0, z);
        return getArena().getArenaRegion().isInRegion(loc) ? loc : null;
    }

    private void explodeArea(Location center) {
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
                            if (random.nextDouble() < 0.3) {
                                Location fireLoc = loc.clone().add(0, 1, 0);
                                if (fireLoc.getBlock().getType().isAir()) {
                                    fireLoc.getBlock().setType(Material.FIRE);
                                }
                            }
                        }
                    }
                }
            }
        }
        world.spawnParticle(Particle.EXPLOSION_LARGE, center, 1, 0, 0, 0, 0);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }

    private void damageNearbyPlayers(Location center) {
        double radius = 3.0;
        for (Player player : center.getWorld().getNearbyEntitiesByType(Player.class, center, radius)) {
            if (getArena().getArenaRegion().isInRegion(player.getLocation())) {
                player.damage(6.0);
            }
        }
    }

    @Override
    public void deActive() {
        super.deActive();
        if (meteorTask != null) {
            meteorTask.cancel();
            meteorTask = null;
        }
        Bukkit.getLogger().info("Meteor Shower Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}