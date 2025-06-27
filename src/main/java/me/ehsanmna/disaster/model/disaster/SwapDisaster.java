package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwapDisaster extends BaseDisaster {

    private int swappedTimes = 0;

    public SwapDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, TextUtils.getMessage("disaster-swap-title"), TextUtils.getMessage("disaster-swap-description"), arena, DisasterType.SWAP);
    }

    @Override
    public void setup() {
        super.setup();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (swappedTimes >= 3 || !isActive()) {
                    deActive();
                    cancel(); // Stop the task after 3 swaps or if disaster is deactivated
                    return;
                }

                // Get a list of living players
                List<ArenaPlayer> alivePlayers = new ArrayList<>(getArena().getArenaHandler().getPlayersPlaying());
                if (alivePlayers.size() < 2) {
                    // Not enough players to swap
                    if (getArena().getArenaHandler().getArenaService().isDebug())
                        getPlugin().getLogger().info("Not enough players to swap in arena: " + getArena().getName());
                    deActive();
                    cancel();
                    return;
                }

                // Shuffle players to randomize pairs
                Collections.shuffle(alivePlayers);

                // Swap locations of players in pairs
                for (int i = 0; i < alivePlayers.size() - 1; i += 2) {
                    ArenaPlayer player1 = alivePlayers.get(i);
                    ArenaPlayer player2 = alivePlayers.get(i + 1);

                    Player p1 = player1.getPlayer();
                    Player p2 = player2.getPlayer();

                    // Store locations
                    Location loc1 = p1.getLocation();
                    Location loc2 = p2.getLocation();

                    // Teleport players to each other's locations
                    p1.teleport(loc2);
                    p2.teleport(loc1);

                    // Notify players
                    TextUtils.sendMessage(p1, "disaster-swap-notify", p2.getName());
                    TextUtils.sendMessage(p2, "disaster-swap-notify", p1.getName());

                    if (getArena().getArenaHandler().getArenaService().isDebug())
                        getPlugin().getLogger().info("Swapped " + p1.getName() + " with " + p2.getName() + " in arena: " + getArena().getName());

                }

                swappedTimes++;
            }
        }.runTaskTimer(getPlugin(), 0, 300); // Run every 15 seconds (300 ticks)
    }

    @Override
    public void deActive() {
        super.deActive();

        if (getArena().getArenaHandler().getArenaService().isDebug()) {
            getPlugin().getLogger().info("Swap player Disaster deactivated for arena: " + getArena().getName());
        }
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

    public int getSwappedTimes() {
        return swappedTimes;
    }

    public void setSwappedTimes(int swappedTimes) {
        this.swappedTimes = swappedTimes;
    }
}