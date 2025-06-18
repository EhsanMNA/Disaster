package me.ehsanmna.disaster.events;

import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaPlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ArenaPlayer player;
    private final Arena arena;
    private boolean cancelled;

    public ArenaPlayerDeathEvent(ArenaPlayer player) {
        this.player = player;
        this.arena = player.getArena();
    }

    @Override
    public boolean isCancelled() {return cancelled;}

    public ArenaPlayer getPlayer() {return player;}

    public Arena getArena() {return arena;}

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
