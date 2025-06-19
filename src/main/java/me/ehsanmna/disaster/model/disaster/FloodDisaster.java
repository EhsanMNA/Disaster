package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
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
        super(plugin, TextUtils.getMessage("disaster-flood-title"), TextUtils.getMessage("disaster-flood-description"), arena, DisasterType.FLOOD);
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
                raiseWater();
            }
        };
        floodTask.runTaskTimer(getPlugin(), 0L, 120L);

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

        this.currentWaterLevel = Math.min(getArena().getArenaRegion().getPos1().getBlockY(),
                getArena().getArenaRegion().getPos2().getBlockY());
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Flood Disaster activated for arena: " + getArena().getName());
    }

    private void raiseWater() {
        Location pos1 = getArena().getArenaRegion().getPos1();
        Location pos2 = getArena().getArenaRegion().getPos2();

        pos1.setWorld(Bukkit.getWorld(getArena().getWorldName()+"-backup"));
        pos2.setWorld(Bukkit.getWorld(getArena().getWorldName()+"-backup"));

        for (int x = Math.min(pos1.getBlockX(), pos2.getBlockX()); x<Math.max(pos1.getBlockX(), pos2.getBlockX()); x++){
            for (int z = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); z<Math.max(pos1.getBlockZ(), pos2.getBlockZ()); z++){
                Location loc = new Location(pos1.getWorld(), x,currentWaterLevel,z);
                if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.VOID_AIR) loc.getBlock().setType(Material.WATER);
            }
        }
        currentWaterLevel++;
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
                            (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.VOID_AIR)) {
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
        // cleanupWater();
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Flood Disaster deactivated for arena: " + getArena().getName());
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