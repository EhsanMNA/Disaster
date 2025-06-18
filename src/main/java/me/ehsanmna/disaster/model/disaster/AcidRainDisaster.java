package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AcidRainDisaster extends BaseDisaster{

    private BukkitRunnable damageTask;

    public AcidRainDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<green><bold>ACID RAIN", "<white>Get out of the rain! Its not safe!", arena, DisasterType.ACID_RAIN);
    }

    @Override
    public void setup() {
        super.setup();
        World world = getArena().getArenaRegion().getPos1().getWorld();
        world.setStorm(true);
        world.setWeatherDuration(999999);

        damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    cancel();
                    return;
                }
                damageExposedPlayers();
            }
        };
        damageTask.runTaskTimer(getPlugin(), 0L, 20L);
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Acid Rain Disaster activated for arena: " + getArena().getName());
    }

    private void damageExposedPlayers() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            Location playerLoc = player.getLocation();
            if (isExposedToSky(playerLoc)) {
                player.damage(1.0);
                player.getWorld().spawnParticle(Particle.SLIME, playerLoc.clone().add(0, 1, 0),
                        10, 0.3, 0.5, 0.3, 0);
            }
        }
    }

    private boolean isExposedToSky(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        for (int y = location.getBlockY() + 1; y <= world.getMaxHeight(); y++) {
            if (world.getBlockAt(x, y, z).getType() != Material.AIR &&
                    world.getBlockAt(x, y, z).getType() != Material.VOID_AIR)
                return false;
        }
        return true;
    }

    @Override
    public void deActive() {
        super.deActive();
        if (damageTask != null) {
            damageTask.cancel();
            damageTask = null;
        }
        World world = getArena().getArenaRegion().getPos1().getWorld();
        world.setWeatherDuration(0);
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Acid Rain Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

}
