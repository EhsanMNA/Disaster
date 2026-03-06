package me.ehsanmna.disaster.model.hologram.impl;

import me.ehsanmna.disaster.model.hologram.Hologram;
import org.bukkit.inventory.ItemStack;

public interface ItemHologram extends Hologram {
    void setItem(ItemStack item);
    ItemStack getItem();
}
