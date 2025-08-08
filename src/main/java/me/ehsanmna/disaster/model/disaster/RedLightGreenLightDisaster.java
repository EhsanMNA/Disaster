package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedLightGreenLightDisaster extends BaseDisaster {

    private final Map<UUID, Location> playerLastLocations = new HashMap<>();
    private boolean isRedLight = false;
    private int currentPhaseDuration = 0;
    private int currentPhaseTicks = 0;

    // Configurable durations (in ticks)
     public static int RED_LIGHT_DURATION = 20 * 5; // 5 seconds
     public static int GREEN_LIGHT_DURATION = 20 * 10; // 10 seconds

    // Sound effects
    public static Sound RED_LIGHT_SOUND = Sound.BLOCK_NOTE_BLOCK_BASS;
    public static Sound GREEN_LIGHT_SOUND = Sound.BLOCK_NOTE_BLOCK_PLING;

    public RedLightGreenLightDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-redlightgreenlight-title"),
                TextUtils.getMessage("disaster-redlightgreenlight-description"),
                arena, DisasterType.RED_LIGHT_GREEN_LIGHT);
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

        // Start with green light
        switchToGreenLight();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive()) {
                    deActive();
                    cancel();
                    return;
                }

                currentPhaseTicks++;

                // Check if phase should switch
                if (currentPhaseTicks >= currentPhaseDuration) {
                    if (isRedLight) switchToGreenLight();
                    else switchToRedLight();
                }

                // During red light, check for player movement
                if (isRedLight) checkPlayerMovement();
                else updatePlayerLocations();
            }
        }.runTaskTimer(getPlugin(), 0, 1); // Run every tick
    }

    private void switchToRedLight() {
        isRedLight = true;
        currentPhaseTicks = 0;
        currentPhaseDuration = RED_LIGHT_DURATION;

        // Notify players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();

            // Title notification
            TextUtils.sendTitle(player, TextUtils.getMessage("disaster-redlight-title"),TextUtils.getMessage("disaster-redlight-subtitle"));
//            player.sendTitle(
//                    ChatColor.RED + "RED LIGHT!",
//                    ChatColor.RED + "Don't move!",
//                    10, 40, 10);

            // Action bar message
            TextUtils.sendActionbar(player, TextUtils.getMessage("disaster-redlight-actionbar"));
//            sendActionBar(player, ChatColor.RED + "RED LIGHT! Don't move!");

            // Sound effect
            player.playSound(player.getLocation(), RED_LIGHT_SOUND, 1.0f, 1.0f);
        }

        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Switched to RED LIGHT in arena: " + getArena().getName());
        }
    }

    private void switchToGreenLight() {
        isRedLight = false;
        currentPhaseTicks = 0;
        currentPhaseDuration = GREEN_LIGHT_DURATION;

        // Update all player locations (so they can move during green light)
        updatePlayerLocations();

        // Notify players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();

            // Title notification
            TextUtils.sendTitle(player, TextUtils.getMessage("disaster-greenlight-title"),TextUtils.getMessage("disaster-greenlight-subtitle"));
//            player.sendTitle(
//                    ChatColor.GREEN + "GREEN LIGHT!",
//                    ChatColor.GREEN + "You can move now!",
//                    10, 40, 10);

            // Action bar message
            TextUtils.sendActionbar(player, TextUtils.getMessage("disaster-greenlight-actionbar"));
//            sendActionBar(player, ChatColor.GREEN + "GREEN LIGHT! Move now!");

            // Sound effect
            player.playSound(player.getLocation(), GREEN_LIGHT_SOUND, 1.0f, 1.0f);
        }

        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Switched to GREEN LIGHT in arena: " + getArena().getName());
        }
    }

    private void checkPlayerMovement() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            Location currentLoc = player.getLocation();
            Location lastLoc = playerLastLocations.get(player.getUniqueId());

            if (lastLoc != null && hasMoved(currentLoc, lastLoc)) {
                // Player moved during red light - punish them
                player.damage(2.0); // 1 heart of damage
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);

                if (getArena().getArenaHandler().getArenaService().isDebug()) {
                    getPlugin().getLogger().info(player.getName() + " moved and took damage in Red Light Green Light in arena: " + getArena().getName());
                }
            }
        }
    }

    private void updatePlayerLocations() {
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            playerLastLocations.put(player.getUniqueId(), player.getLocation().clone());
        }
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