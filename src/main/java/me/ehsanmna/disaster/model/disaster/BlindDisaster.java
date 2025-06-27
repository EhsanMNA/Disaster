package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BlindDisaster extends BaseDisaster{

    public BlindDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-blind-title"), TextUtils.getMessage("disaster-blind-description"), arena, DisasterType.BLIND);
    }

    @Override
    public void setup() {
        super.setup();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArenaPlayer player : getArena().getArenaHandler().getPlayersPlaying())
                    player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }.runTaskLater(getPlugin(), 20 * 60);
        for (ArenaPlayer player : getArena().getArenaHandler().getPlayersPlaying())
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 2, false, false, false));
    }

    @Override
    public void deActive() {
        super.deActive();

        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Hot potato Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }
}
