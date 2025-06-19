package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;

public abstract class BaseDisaster implements Disaster{

    private final DisasterPlugin plugin;
    private String name;
    private final String description;
    private final Arena arena;
    private final DisasterType type;
    private boolean active;

    public BaseDisaster(DisasterPlugin plugin, String name, String description, Arena arena, DisasterType type) {
        this.plugin = plugin;
        this.name = name;
        this.description = description;
        this.arena = arena;
        this.type = type;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public DisasterType getType(){
        return type;
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    @Override
    public DisasterPlugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setup() {
        if (arena.getArenaHandler().getArenaService().isDebug())
            plugin.getLogger().info("Setting up the "+name +" disaster for "+arena.getName()+" arena!");
    }

    @Override
    public void deActive() {
        active = false;
    }

    @Override
    public void act() {
        active = true;
    }
}
