package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;

public interface Disaster {


    String getName();

    String getDescription();

    Arena getArena();

    DisasterPlugin getPlugin();

    boolean isActive();

    boolean canActive();

    DisasterType getType();

    void setup();

    void deActive();

    void act();

}
