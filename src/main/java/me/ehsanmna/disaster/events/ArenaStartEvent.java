package me.ehsanmna.disaster.events;

import me.ehsanmna.disaster.model.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;
    private boolean cancelled;

    public ArenaStartEvent(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {return arena;}

    @Override
    public @NotNull HandlerList getHandlers() {return handlers;}

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
