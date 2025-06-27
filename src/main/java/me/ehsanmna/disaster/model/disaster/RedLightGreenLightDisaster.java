package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedLightGreenLightDisaster extends BaseDisaster {

    private final Map<UUID, Location> playerLastLocations = new HashMap<>();

    public RedLightGreenLightDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-redlightgreenlight-title"), TextUtils.getMessage("disaster-redlightgreenlight-description"), arena, DisasterType.RED_LIGHT_GREEN_LIGHT);
    }

    @Override
    public void setup() {
        super.setup();

        // Initialize player locations
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            playerLastLocations.put(player.getUniqueId(), player.getLocation().clone());
            if (getArena().getArenaHandler().getArenaService().isDebug()) {
                getPlugin().getLogger().info("Monitoring " + player.getName() + " for movement in Red Light Green Light in arena: " + getArena().getName());
            }
        }

        new BukkitRunnable() {
            int ticks = 0;
            final int duration = 20 * 20; // 20 seconds

            @Override
            public void run() {
                if (ticks >= duration || !isActive()) {
                    deActive();
                    cancel();
                    return;
                }

                // Check for player movement
                for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
                    Player player = arenaPlayer.getPlayer();
                    Location currentLoc = player.getLocation();
                    Location lastLoc = playerLastLocations.get(player.getUniqueId());

                    // Compare position (ignore rotation)
                    if (lastLoc != null && hasMoved(currentLoc, lastLoc)) {
                        player.damage(2.0); // 1 heart of damage
                        if (getArena().getArenaHandler().getArenaService().isDebug()) {
                            getPlugin().getLogger().info(player.getName() + " moved and took damage in Red Light Green Light in arena: " + getArena().getName());
                        }
                    }
                    playerLastLocations.put(player.getUniqueId(), currentLoc.clone());
                }

                ticks++;
            }
        }.runTaskTimer(getPlugin(), 0, 20);
    }

    private boolean hasMoved(Location current, Location last) {
        // Compare only X, Y, Z coordinates to allow head rotation
        return current.getX() != last.getX() || current.getY() != last.getY() || current.getZ() != last.getZ();
    }

    @Override
    public void deActive() {
        super.deActive();
        playerLastLocations.clear();
        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Red Light Green Light Disaster deactivated for arena: " + getArena().getName());
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}