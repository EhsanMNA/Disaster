package me.ehsanmna.disaster.model.hologram.impl;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ModernItemHologram extends BaseHologram implements ItemHologram {
    private ItemDisplay display;
    private ItemStack item;

    public ModernItemHologram(JavaPlugin plugin, String id, Location location, ItemStack item) {
        super(plugin, id, location);
        this.item = item;
    }

    @Override
    public void spawn() {
        if (isSpawned()) return;

        display = location.getWorld().spawn(location, ItemDisplay.class, entity -> {
            entity.setItemStack(item);
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        });

        spawned = true;
    }

    @Override
    public void despawn() {
        if (!isSpawned()) return;

        display.remove();
        display = null;
        spawned = false;
    }

    @Override
    public void update() {
        if (!isSpawned()) return;

        display.teleport(location);
        display.setItemStack(item);
    }

    @Override
    protected void updateVisibilityFor(Player player) {
        if (visibleByDefault && !hiddenPlayers.contains(player.getUniqueId())) {
            player.showEntity(plugin, display);
        } else {
            player.hideEntity(plugin, display);
        }
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item;
        if (isSpawned()) {
            display.setItemStack(item);
        }
    }

    @Override
    public ItemStack getItem() {
        return item.clone();
    }
}