package me.ehsanmna.disaster.listener;

import me.ehsanmna.disaster.DisasterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class DisasterSettingListener implements Listener {

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            DisasterPlugin.getInstance().getDataManager().loadData();
        }
    }
}
