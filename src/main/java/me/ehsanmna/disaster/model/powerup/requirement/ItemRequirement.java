package me.ehsanmna.disaster.model.powerup.requirement;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class ItemRequirement implements PowerUpRequirement {

    private final DisasterPlugin disasterPlugin;
    private final Material itemType;
    private final int amount;
    private final int customModelData; // -1 means no custom model data check

    public ItemRequirement(DisasterPlugin disasterPlugin, Material itemType, int amount, int customModelData) {
        this.disasterPlugin = disasterPlugin;
        this.itemType = itemType;
        this.amount = amount;
        this.customModelData = customModelData;
    }

    @Override
    public String getName() {
        return "ItemRequirement";
    }

    @Override
    public String getDescription() {
        return "Depends on ItemStacks";
    }

    @Override
    public DisasterPlugin getPlugin() {
        return disasterPlugin;
    }

    @Override
    public boolean isRequire(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemType) {
                if (customModelData == -1 || (item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == customModelData)) {
                    count += item.getAmount();
                }
            }
        }
        return count >= amount;
    }

    @Override
    public void consume(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (remaining <= 0) break;
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == itemType) {
                if (customModelData == -1 || (item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == customModelData)) {
                    int itemAmount = item.getAmount();
                    if (itemAmount > remaining) {
                        item.setAmount(itemAmount - remaining);
                        remaining = 0;
                    } else {
                        player.getInventory().setItem(i, null);
                        remaining -= itemAmount;
                    }
                }
            }
        }
    }

    public Material getItemType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }

    public int getCustomModelData() {
        return customModelData;
    }
}