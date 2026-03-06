package me.ehsanmna.disaster.model.powerup.requirement;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.entity.Player;

public class XPRequirement implements PowerUpRequirement {

    private final DisasterPlugin disasterPlugin;
    private final int amount;

    public XPRequirement(DisasterPlugin disasterPlugin, int amount) {
        this.disasterPlugin = disasterPlugin;
        this.amount = amount;
    }

    @Override
    public String getName() {
        return "XPRequirement";
    }

    @Override
    public String getDescription() {
        return "Depends on player XP";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return disasterPlugin;
    }

    @Override
    public boolean isRequire(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        return player.getTotalExperience() >= amount;
    }

    @Override
    public void consume(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        player.setTotalExperience(player.getTotalExperience() - amount);
    }

    public int getAmount() {
        return amount;
    }
}