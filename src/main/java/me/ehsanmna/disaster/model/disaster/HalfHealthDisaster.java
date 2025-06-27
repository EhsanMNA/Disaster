package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.entity.Player;

public class HalfHealthDisaster extends BaseDisaster {

    public HalfHealthDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-halfhealth-title"), TextUtils.getMessage("disaster-halfhealth-description"), arena, DisasterType.HALF_HEALTH);
    }

    @Override
    public void setup() {
        super.setup();

        // Apply half-health to all living players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            double currentHealth = player.getHealth();
            double newHealth = Math.max(0.5, currentHealth / 2.0); // Ensure health doesn't go below 0.5 (1/2 heart)
            player.setHealth(newHealth);
            TextUtils.sendMessage(player, "disaster-halfhealth-notify");

            if (getArena().getArenaHandler().getArenaService().isDebug()) {
                getPlugin().getLogger().info("Halved health of " + player.getName() + " to " + newHealth + " in arena: " + getArena().getName());
            }
        }
    }

    @Override
    public void deActive() {
        super.deActive();

        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Half Health Disaster deactivated for arena: " + getArena().getName());
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}