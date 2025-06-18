package me.ehsanmna.disaster.model.arena;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.util.SlimeWorldUtils;
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
                    arena.getArenaHandler().setSlimeWorld(SlimeWorldUtils.getWorld(arena.getWorldName()));
                    if (arena.getArenaHandler().getSlimeWorld() == null)
                        DisasterPlugin.getInstance().getLogger().warning("Could not get "+arena.getWorldName()+" slime world! [arena:"+arena.getName()+"]");
                }
            }.runTaskLater(DisasterPlugin.getInstance(), 100L);
        }
    }

}
