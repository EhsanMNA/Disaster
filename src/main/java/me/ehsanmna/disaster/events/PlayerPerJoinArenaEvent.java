package me.ehsanmna.disaster.events;

import me.ehsanmna.disaster.model.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPerJoinArenaEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Arena arena;
    private boolean cancelled;

    public PlayerPerJoinArenaEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }

    @Override
    public boolean isCancelled() {return cancelled;}

    public Player getPlayer() {return player;}

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
