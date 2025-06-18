package me.ehsanmna.disaster.util;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.commands.CommandManager;
import com.grinderwolf.swm.plugin.config.ConfigManager;
import com.grinderwolf.swm.plugin.config.WorldData;
import com.grinderwolf.swm.plugin.config.WorldsConfig;
import com.grinderwolf.swm.plugin.log.Logging;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import me.ehsanmna.disaster.DisasterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class SlimeWorldUtils {


    public static void createCloneWorld(String worldName){
//        try{
//            return SWMPlugin.getInstance().getWorld(worldName).clone(worldName+"-backup");
//        }catch (NullPointerException e){e.printStackTrace();}
        String newWorldName = worldName+"-backup";
        WorldsConfig config = ConfigManager.getWorldConfig();
        WorldData worldData = config.getWorlds().get(worldName);
        if (worldData == null) DisasterPlugin.getInstance().getLogger().info(Logging.COMMAND_PREFIX + ChatColor.RED + "Failed to find world " + worldName + " inside the worlds config file.");
        else if (CommandManager.getInstance().getWorldsInUse().contains(worldName))
            DisasterPlugin.getInstance().getLogger().info(Logging.COMMAND_PREFIX + ChatColor.RED + "World " + worldName + " is already being used on another command! Wait some time and try again.");
        else {
            String dataSource = worldData.getDataSource();
            SlimeLoader loader = SWMPlugin.getInstance().getLoader(dataSource);
            CommandManager.getInstance().getWorldsInUse().add(worldName);
            Bukkit.getScheduler().runTaskAsynchronously(SWMPlugin.getInstance(), () -> {
                try {
                    SlimeWorld slimeWorld = SWMPlugin.getInstance().getWorld(worldName) == null ?
                            SWMPlugin.getInstance().loadWorld(loader, worldName, true, worldData.toPropertyMap()).clone(newWorldName, loader) :
                            SWMPlugin.getInstance().getWorld(worldName).clone(newWorldName, loader);
                    Bukkit.getScheduler().runTask(SWMPlugin.getInstance(), () -> {
                        try {
                            SWMPlugin.getInstance().loadWorld(slimeWorld, true);
                            config.getWorlds().put(newWorldName, worldData);
                            config.save();
                        }catch (IllegalArgumentException | UnknownWorldException | IOException | WorldLockedException e) {e.printStackTrace();}
                    });
                    slimeWorld.getPropertyMap().setValue(SlimeProperties.DIFFICULTY, "hard");
                    slimeWorld.getPropertyMap().setValue(SlimeProperties.ALLOW_MONSTERS, true);
                    slimeWorld.getPropertyMap().setValue(SlimeProperties.PVP, true);
                }catch (Exception ignored){}
                finally {CommandManager.getInstance().getWorldsInUse().remove(worldName);}
            });
        }
//        SlimeWorld slimeWorld = SWMPlugin.getInstance().getWorld(worldName+"-backup");
//        slimeWorld.getPropertyMap().setValue(SlimeProperties.DIFFICULTY, "normal");
//        slimeWorld.getPropertyMap().setValue(SlimeProperties.ALLOW_MONSTERS, true);
//        slimeWorld.getPropertyMap().setValue(SlimeProperties.PVP, true);
//        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"swm clone-world "+worldName+" "+worldName+"-backup");
//        return SWMPlugin.getInstance().getWorld(worldName+"-backup");
    }

    public static SlimeWorld getWorld(String worldName){
        return SWMPlugin.getInstance().getWorld(worldName+"-backup");
    }

    public static void deleteWorld(String worldName){
//        try {
//            SWMPlugin.getInstance().getWorld(worldName).getLoader().deleteWorld(worldName);
//        } catch (UnknownWorldException | IOException e) {
//            throw new RuntimeException(e);
//        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"swm unload "+worldName +"-backup");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"swm delete "+worldName +"-backup");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"swm delete "+worldName +"-backup");
    }

}
