package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FloodDisaster extends BaseDisaster {

    private BukkitRunnable floodTask;
    private BukkitRunnable damageTask;
    private int currentWaterLevel;

    public FloodDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<blue><bold>FLOOD", "<white>Go to higher levels!", arena, DisasterType.FLOOD);
        int minY = Math.min(arena.getArenaRegion().getPos1().getBlockY(),
                arena.getArenaRegion().getPos2().getBlockY());
        this.currentWaterLevel = minY - 1;
    }

    @Override
    public void setup() {
        super.setup();
        floodTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cancel();
                    return;
                }
                raiseWaterAroundPlayers();
            }
        };
        floodTask.runTaskTimer(getPlugin(), 0L, 200L);

        damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cancel();
                    return;
                }
                damageSubmergedPlayers();
            }
        };
        damageTask.runTaskTimer(getPlugin(), 0L, 20L);
        Bukkit.getLogger().info("Flood Disaster activated for arena: " + getArena().getName());
    }

    private void raiseWaterAroundPlayers() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            Location center = player.getLocation();
            World world = center.getWorld();
            int radius = 5;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, 0, z);
                    loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
                    if (getArena().getArenaRegion().isInRegion(loc) &&
                            loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(Material.WATER);
                        world.spawnParticle(Particle.WATER_BUBBLE, loc.clone().add(0.5, 0.5, 0.5),
                                5, 0.2, 0.2, 0.2, 0);
                    }
                }
            }
        }
    }

    private void damageSubmergedPlayers() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            Location playerLoc = player.getLocation();
            if (getArena().getArenaRegion().isInRegion(playerLoc) &&
                    playerLoc.getBlock().getType() == Material.WATER) {
                player.damage(1.0);
                player.getWorld().spawnParticle(Particle.WATER_SPLASH, playerLoc.clone().add(0, 1, 0),
                        10, 0.3, 0.5, 0.3, 0);
            }
        }
    }

    @Override
    public void deActive() {
        super.deActive();
        if (floodTask != null) {
            floodTask.cancel();
            floodTask = null;
        }
        if (damageTask != null) {
            damageTask.cancel();
            damageTask = null;
        }
        cleanupWater();
        Bukkit.getLogger().info("Flood Disaster deactivated for arena: " + getArena().getName());
    }

    private void cleanupWater() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayers()) {
            Player player = arenaPlayer.getPlayer();
            Location center = player.getLocation();
            World world = center.getWorld();
            int radius = 5;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, 0, z);
                    loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
                    if (getArena().getArenaRegion().isInRegion(loc) &&
                            loc.getBlock().getType() == Material.WATER) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}