package me.ehsanmna.disaster.model.machine;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.hologram.impl.ItemHologram;
import me.ehsanmna.disaster.model.hologram.impl.TextHologram;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PowerUpMachineImpl extends BasePowerUpMachine {

    private Location buttonLocation;

    public PowerUpMachineImpl(String name, String description, PowerUpType powerUpType, DisasterPlugin plugin) {
        this.name = name;
        this.description = description;
        this.plugin = plugin;
        this.powerUp = getPlugin().getPowerUpManager().getPowerUp(powerUpType);
        this.requirements = new ArrayList<>();
        this.machineRegion = null;
        this.textHologram = null;
        this.itemHologram = null;
    }

    public PowerUpMachineImpl(String name, String description, PowerUp powerUp, DisasterPlugin plugin) {
        this.name = name;
        this.description = description;
        this.powerUp = powerUp;
        this.plugin = plugin;
        this.requirements = new ArrayList<>();
        this.machineRegion = null;
        this.textHologram = null;
        this.itemHologram = null;
    }

    public void initializeHolograms(Location hologramLocation, ItemStack item) {
        if (item != null) {
            itemHologram = (ItemHologram) plugin.getHologramManager().createItemHologram(name + "_item", hologramLocation.clone().add(0, 3, 0), item, false);
        }
    }

    public void initializeHolograms(Location hologramLocation, String text) {
        if (text != null) {
            textHologram = (TextHologram) plugin.getHologramManager().createTextHologram(name + "_text", hologramLocation.clone().add(0, 2, 0), text, false);
        }
    }

    @Override
    public Location getButtonLocation() {
        return buttonLocation;
    }

    @Override
    public void setButtonLocation(Location buttonLocation) {
        this.buttonLocation = buttonLocation;
    }
}