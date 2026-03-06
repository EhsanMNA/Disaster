package me.ehsanmna.disaster.model.powerup.impl;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearPowerUp implements PowerUp {

    private final DisasterPlugin plugin;

    public ClearPowerUp(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Clear";
    }

    @Override
    public String getDescription() {
        return "Clears all active effects from the player.";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.CLEAR;
    }

    @Override
    public ItemStack getItemStackIcon() {
        return new ItemStack(Material.MILK_BUCKET);
    }

    @Override
    public void setup() {}


    @Override
    public void handle(ArenaPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.getActivePotionEffects().forEach(effect -> bukkitPlayer.removePotionEffect(effect.getType()));
    }
}