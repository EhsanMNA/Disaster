package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.EntityUtilities;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WitherDisaster extends BaseDisaster {

    private final Random random = new Random();
    private BukkitRunnable witherTask;
    private final List<Wither> withers = new ArrayList<>();

    public WitherDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-wither-title"), TextUtils.getMessage("disaster-wither-description"), arena, DisasterType.WITHER);
    }

    @Override
    public void setup() {
        super.setup();
        spawnWithers();
        witherTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cleanupWithers();
                    cancel();
                    return;
                }
                affectArea();
            }
        };
        witherTask.runTaskTimer(getPlugin(), 0L, 20L);
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Dragons Disaster activated for arena: " + getArena().getName());
    }

    private void spawnWithers() {
        World world = Bukkit.getWorld(getArena().getWorldName()+"-backup");
        int spawned = 0;
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            if (spawned >= 3) break;
            Player player = arenaPlayer.getPlayer();
            Location spawnLoc = getRandomLocationAroundPlayer(player);
            if (spawnLoc != null) {
                spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc.getBlockX(), spawnLoc.getBlockZ()) + 10);
                assert world != null;
                Wither wither = (Wither) world.spawnEntity(spawnLoc, EntityType.WITHER);
                withers.add(wither);
                spawned++;
                world.spawnParticle(Particle.SPELL_WITCH, spawnLoc, 50, 1, 1, 1, 0.1);
                world.playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            }
        }
    }

    private Location getRandomLocationAroundPlayer(Player player) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = 20;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius + 5;
        int x = (int) (playerLoc.getX() + Math.cos(angle) * distance);
        int z = (int) (playerLoc.getZ() + Math.sin(angle) * distance);

        Location loc = new Location(world, x, playerLoc.getBlockY(), z);
        return getArena().getArenaRegion().isInRegion(loc) ? loc : null;
    }

    private void affectArea() {
        for (Wither wither : withers) {
            if (wither.isDead()) continue;
            Location center = wither.getLocation();
            World world = center.getWorld();

            EntityUtilities.removeBlocksAround(center,getArena(),3);

            double knockRadius = 4.0;
            for (Player player : world.getNearbyEntitiesByType(Player.class, center, knockRadius)) {
                if (getArena().getArenaRegion().isInRegion(player.getLocation())) {
                    player.setVelocity(new Vector(0, 2, 0));
                    player.damage(3.0);
                    world.spawnParticle(Particle.END_ROD, player.getLocation(),
                            20, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);
                }
            }
        }
    }

    private void cleanupWithers() {
        for (Wither wither : withers) {
            if (wither != null && !wither.isDead()) {
                Location loc = wither.getLocation();
                wither.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 50, 1, 1, 1, 0.1);
                wither.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
                wither.remove();
            }
        }
        withers.clear();
    }

    @Override
    public void deActive() {
        super.deActive();
        if (witherTask != null) {
            witherTask.cancel();
            witherTask = null;
        }
        cleanupWithers();
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Dragons Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}