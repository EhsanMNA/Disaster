package me.ehsanmna.disaster.events;

import me.ehsanmna.disaster.model.arena.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaEndEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;
    private boolean cancelled;

    public ArenaEndEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public boolean isCancelled() {return cancelled;}

    public Arena getArena() {return arena;}

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() {return handlers;}
}
