package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class NoJumpDisaster extends BaseDisaster {

    public NoJumpDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-nojump-title"), TextUtils.getMessage("disaster-nojump-description"), arena, DisasterType.NO_JUMP);
    }

    @Override
    public void setup() {
        super.setup();

        // Apply Jump Boost -128 (prevents jumping) to all alive players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, -128, false, false, false));

            if (getArena().getArenaHandler().getArenaService().isDebug()) {
                getPlugin().getLogger().info("Applied no-jump effect to " + player.getName() + " in arena: " + getArena().getName());
            }
        }

        // Deactivate after 15 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                deActive();
            }
        }.runTaskLater(getPlugin(), 45 * 20); // 45 seconds
    }

    @Override
    public void deActive() {
        super.deActive();

        // Remove Jump Boost effect from all players
        for (ArenaPlayer arenaPlayer : getArena().getArenaHandler().getPlayersPlaying()) {
            Player player = arenaPlayer.getPlayer();
            player.removePotionEffect(PotionEffectType.JUMP);
            if (getArena().getArenaHandler().getArenaService().isDebug()) {
                getPlugin().getLogger().info("Removed no-jump effect from " + player.getName() + " in arena: " + getArena().getName());
            }
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}