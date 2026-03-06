package me.ehsanmna.disaster.model.powerup.impl;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedPowerUp implements PowerUp {

    private final DisasterPlugin plugin;

    public SpeedPowerUp(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Speed";
    }

    @Override
    public String getDescription() {
        return "Grants a speed boost to the player.";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.SPEED;
    }

    @Override
    public void setup() {}

    @Override
    public ItemStack getItemStackIcon() {
        return new ItemStack(Material.QUARTZ);
    }

    @Override
    public void handle(ArenaPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1)); // 10 seconds, level 2
    }
}