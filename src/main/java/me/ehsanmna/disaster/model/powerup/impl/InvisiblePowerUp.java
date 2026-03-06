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

public class InvisiblePowerUp implements PowerUp {

    private final DisasterPlugin plugin;

    public InvisiblePowerUp(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Invisible";
    }

    @Override
    public String getDescription() {
        return "Grants invisibility to the player.";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.INVISIBLE;
    }

    @Override
    public void setup() {}

    @Override
    public ItemStack getItemStackIcon() {
        return new ItemStack(Material.POTION);
    }

    @Override
    public void handle(ArenaPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
    }
}