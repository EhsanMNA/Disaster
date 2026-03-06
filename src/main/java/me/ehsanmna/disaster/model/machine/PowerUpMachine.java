package me.ehsanmna.disaster.model.machine;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.hologram.impl.ItemHologram;
import me.ehsanmna.disaster.model.hologram.impl.TextHologram;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import me.ehsanmna.disaster.model.powerup.requirement.PowerUpRequirement;
import me.ehsanmna.disaster.model.region.Region;
import org.bukkit.Location;

import java.util.List;

public interface PowerUpMachine {

    String getName();

    String getDescription();

    DisasterPlugin getPlugin();

    List<PowerUpRequirement> getRequirements();

    PowerUp getPowerUp();

    Arena getArena();

    Region getRegion();

    Location getButtonLocation();

    void setRegion(Region region);

    void setButtonLocation(Location location);

    void setArena(Arena arena);

    void setPowerUp(PowerUp powerUp);

    public void setTextHologram(TextHologram textHologram);

    public void setItemHologram(ItemHologram itemHologram);
}
