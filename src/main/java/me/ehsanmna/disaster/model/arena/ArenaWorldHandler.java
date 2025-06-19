package me.ehsanmna.disaster.model.arena;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.util.SlimeWorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaWorldHandler {

    private final Arena arena;

    public ArenaWorldHandler(Arena arena) {
        this.arena = arena;
    }

    public void checkWorld(){
        if (arena.getArenaHandler() == null) return;
        if (arena.getArenaHandler().getSlimeWorld() == null) {
            SlimeWorldUtils.createCloneWorld(arena.getWorldName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    SlimeWorld slimeWorld = SlimeWorldUtils.getWorld(arena.getWorldName());
                    if (arena.getArenaHandler().getSlimeWorld() == null){
                        DisasterPlugin.getInstance().getLogger().warning("Could not get "+arena.getWorldName()+" slime world! [arena:"+arena.getName()+"] using bukkit world game rules!");
                        Bukkit.getWorld(arena.getWorldName()+"-backup").setDifficulty(Difficulty.HARD);
                        return;
                    }
//                    arena.getArenaHandler().setSlimeWorld(slimeWorld);
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.DIFFICULTY, "hard");
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.ALLOW_MONSTERS, true);
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.PVP, true);
                }
            }.runTaskLater(DisasterPlugin.getInstance(), 100L);
        }
    }

}
