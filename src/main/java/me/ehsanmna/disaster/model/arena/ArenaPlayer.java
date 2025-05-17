package me.ehsanmna.disaster.model.arena;

import org.bukkit.entity.Player;

import java.util.Objects;

public class ArenaPlayer {
    private final Player player;
    private Arena arena;
    private boolean isAlive;

    public ArenaPlayer(Player player) {
        this.player = player;
        this.isAlive = true;
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArenaPlayer that = (ArenaPlayer) o;
        return Objects.equals(player, that.player) && Objects.equals(arena, that.arena);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, arena);
    }
}
