package me.ehsanmna.disaster.model.powerup.impl;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HealthPowerUp implements PowerUp {

    private final DisasterPlugin plugin;

    public HealthPowerUp(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Health";
    }

    @Override
    public String getDescription() {
        return "Restores full health to the player.";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }


    @Override
    public PowerUpType getType() {
        return PowerUpType.HEALTH;
    }

    @Override
    public void setup() {}

    @Override
    public ItemStack getItemStackIcon() {
        return new ItemStack(Material.APPLE);
    }

    @Override
    public void handle(ArenaPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
    }
}