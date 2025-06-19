package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class PvpDisaster extends BaseDisaster{

    private int startedTimePlayers = 0;

    public PvpDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<gold><bold>PVP", "<white>PVP is on until half players remove!", arena, DisasterType.PVP);
    }

    @Override
    public void setup() {
        super.setup();
        startedTimePlayers = getArena().getArenaHandler().getPlayersPlaying().size();
    }

    @Override
    public void deActive() {
        super.deActive();

        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Hot potato Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

    public int getStartedTimePlayers() {
        return startedTimePlayers;
    }

    public void setStartedTimePlayers(int startedTimePlayers) {
        this.startedTimePlayers = startedTimePlayers;
    }
}
