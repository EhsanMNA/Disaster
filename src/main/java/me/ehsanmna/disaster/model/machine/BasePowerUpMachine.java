package me.ehsanmna.disaster.model.machine;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.hologram.impl.ItemHologram;
import me.ehsanmna.disaster.model.hologram.impl.TextHologram;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.requirement.PowerUpRequirement;
import me.ehsanmna.disaster.model.region.Region;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePowerUpMachine implements PowerUpMachine{

    String name;
    String description;
    PowerUp powerUp;
    List<PowerUpRequirement> requirements = new ArrayList<>();
    DisasterPlugin plugin = DisasterPlugin.getInstance();
    Arena arena;
    Region machineRegion;
    TextHologram textHologram;
    ItemHologram itemHologram;

    @Override
    public List<PowerUpRequirement> getRequirements() {
        return requirements;
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PowerUp getPowerUp() {
        return powerUp;
    }

    @Override
    public Region getRegion() {
        return machineRegion;
    }

    @Override
    public void setRegion(Region region) {
        machineRegion = region;
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    @Override
    public void setArena(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void setPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    public void setTextHologram(TextHologram textHologram) {
        this.textHologram = textHologram;
    }

    public void setItemHologram(ItemHologram itemHologram) {
        this.itemHologram = itemHologram;
    }

    public TextHologram getTextHologram() {
        return textHologram;
    }

    public ItemHologram getItemHologram() {
        return itemHologram;
    }
}
