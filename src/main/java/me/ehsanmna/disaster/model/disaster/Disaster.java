package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;

public interface Disaster {


    public String getName();

    public String getDescription();

    public Arena getArena();

    public DisasterPlugin getPlugin();

    public boolean isActive();

    public DisasterType getType();

    public void setup();

    public void deActive();

    public void act();

}
