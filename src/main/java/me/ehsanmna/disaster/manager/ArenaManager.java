package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final DisasterPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();


    public ArenaManager(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    public void addArena(Arena arena){
        arenas.put(arena.getName(), arena);
    }

    public Arena getArena(String name){
        return arenas.get(name);
    }

    public boolean isArenaExist(String name){
        return arenas.containsKey(name);
    }

    public Collection<Arena> getArenas(){
        return arenas.values();
    }

    public boolean isArenaFull(Arena arena){
        return arena.getArenaHandler().isRunning() ?
                arena.getMaxPlayers() <= arena.getArenaHandler().getPlayersPlaying().size() :
                arena.getArenaHandler().getArenaService().getWaitingService().isWaitingFull();
    }

    public Arena removeArena(String name){
        return arenas.remove(name);
    }


}
