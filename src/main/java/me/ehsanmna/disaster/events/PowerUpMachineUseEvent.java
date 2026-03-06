package me.ehsanmna.disaster.events;

import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PowerUpMachineUseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;
    private final PowerUpMachine powerUpMachine;
    private boolean cancelled;

    public PowerUpMachineUseEvent(Arena arena, PowerUpMachine powerUpMachine) {
        this.arena = arena;
        this.powerUpMachine = powerUpMachine;
    }

    public Arena getArena() {
        return arena;
    }

    public PowerUpMachine getPowerUpMachine() {
        return powerUpMachine;
    }

    @Override
    public @NotNull HandlerList getHandlers() {return handlers;}

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
