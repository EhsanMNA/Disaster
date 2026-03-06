package me.ehsanmna.disaster.model.powerup;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.inventory.ItemStack;

public interface PowerUp {

    String getName();

    String getDescription();

    DisasterPlugin getPlugin();

    PowerUpType getType();

    ItemStack getItemStackIcon();

    void setup();

    void handle(ArenaPlayer player);

}
