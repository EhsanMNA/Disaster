package me.ehsanmna.disaster.model.powerup.requirement;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;

public interface PowerUpRequirement {

    String getName();

    String getDescription();

    DisasterPlugin getPlugin();

    boolean isRequire(ArenaPlayer arenaPlayer);

    void consume(ArenaPlayer arenaPlayer);

}
