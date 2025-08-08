package me.ehsanmna.disaster.util;

import me.ehsanmna.disaster.DisasterPlugin;
import org.bukkit.Bukkit;

public class DependencyManager {

    public boolean checkForDependency(){

        if (!Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager")) {
            DisasterPlugin.getInstance().getLogger().warning("Could not detect Slime world manager plugin! Please install it before using Disaster!");
            return false;
        }
        return true;
    }

}
