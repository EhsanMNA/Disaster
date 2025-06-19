package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class HotPotatoDisaster extends BaseDisaster{

    private ArenaPlayer potatoPlayer;
    private BukkitTask potatoTask;
    private int playersWhenStarted = 0;

    public HotPotatoDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<gold><bold>HOT POTATO", "<white>Last one has the potato will be explode!", arena, DisasterType.HOT_POTATO);
        setPlayersWhenStarted(arena.getArenaHandler().getPlayersPlaying().size());
    }

    @Override
    public void setup() {
        super.setup();
        int playerNumber = new Random().nextInt(getArena().getArenaHandler().getPlayersPlaying().size());
        potatoPlayer = getArena().getArenaHandler().getPlayersPlaying().get(playerNumber);
        potatoPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));

        potatoTask = new BukkitRunnable() {
            @Override
            public void run() {
                getPlugin().getPlayerManager().killPlayer(potatoPlayer);
                deActive();
            }
        }.runTaskLater(getPlugin(), 20 * 45);
        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Hot potato Disaster activated for arena: " + getArena().getName());
    }

    @Override
    public void deActive() {
        super.deActive();
        if (potatoTask != null) {
            potatoTask.cancel();
            potatoTask = null;
        }

        potatoPlayer = null;

        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Hot potato Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

    public int getPlayersWhenStarted() {
        return playersWhenStarted;
    }

    public void setPlayersWhenStarted(int playersWhenStarted) {
        this.playersWhenStarted = playersWhenStarted;
    }

}
