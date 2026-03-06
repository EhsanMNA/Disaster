package me.ehsanmna.disaster.model.hologram;

import me.ehsanmna.disaster.model.hologram.impl.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramFactory {
    private final JavaPlugin plugin;
    private final boolean useModernEntities;

    public HologramFactory(JavaPlugin plugin, boolean useModernEntities) {
        this.plugin = plugin;
        this.useModernEntities = useModernEntities;
    }

    public TextHologram createTextHologram(Location location, String id, String text) {
        if (useModernEntities) {
            return new ModernTextHologram(plugin, id, location, text);
        } else {
            return new LegacyTextHologram(plugin, id, location, text);
        }
    }

    public ItemHologram createItemHologram(Location location, String id, ItemStack item) {
        if (useModernEntities) {
            return new ModernItemHologram(plugin, id, location, item);
        } else {
            throw new UnsupportedOperationException("Item holograms requires modern DisplayEntities");
        }
    }
}