package me.ehsanmna.disaster.util;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.commands.sub.CloneWorldCmd;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;

public class SlimeWorldUtils {


    public static void createCloneWorld(String worldName){
        CloneWorldCmd cloneWorldCmd = new CloneWorldCmd();
        cloneWorldCmd.onCommand(Bukkit.getConsoleSender(), new String[]{worldName, worldName+"-backup", "file"});
//        try{
//            return SWMPlugin.getInstance().getWorld(worldName).clone(worldName+"-backup");
//        }catch (NullPointerException e){e.printStackTrace();}
//        String worldName = templateWorldName+"-backup";
//        WorldsConfig config = ConfigManager.getWorldConfig();
//        WorldData worldData = config.getWorlds().get(templateWorldName);
//
//        World world = Bukkit.getWorld(worldName);
//        if (world != null) DisasterPlugin.getInstance().getLogger().info(Logging.COMMAND_PREFIX + ChatColor.RED + "World " + worldName + " is already loaded!");
//        else if (worldData == null) DisasterPlugin.getInstance().getLogger().info(Logging.COMMAND_PREFIX + ChatColor.RED + "Failed to find world " + templateWorldName + " inside the worlds config file.");
//        else if (CommandManager.getInstance().getWorldsInUse().contains(templateWorldName))
//            DisasterPlugin.getInstance().getLogger().info(Logging.COMMAND_PREFIX + ChatColor.RED + "World " + templateWorldName + " is already being used on another command! Wait some time and try again.");
//        else {
//            String dataSource = worldData.getDataSource();
//            SlimeLoader loader = SWMPlugin.getInstance().getLoader(dataSource);
//            CommandManager.getInstance().getWorldsInUse().add(templateWorldName);
//            Bukkit.getScheduler().runTaskAsynchronously(SWMPlugin.getInstance(), () -> {
//                try {
//                    SlimeWorld slimeWorld = SWMPlugin.getInstance().getWorld(templateWorldName) == null ?
//                            SWMPlugin.getInstance().loadWorld(loader, templateWorldName, true, worldData.toPropertyMap()).clone(worldName, loader) :
//                            SWMPlugin.getInstance().getWorld(templateWorldName).clone(worldName, loader);
//                    Bukkit.getScheduler().runTask(SWMPlugin.getInstance(), () -> {
//                        try {
//                            SWMPlugin.getInstance().loadWorld(slimeWorld, true);
//                            config.getWorlds().put(worldName, worldData);
//                            config.save();
//                        }catch (IllegalArgumentException | UnknownWorldException | IOException | WorldLockedException e) {e.printStackTrace();}
//                    });
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.DIFFICULTY, "hard");
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.ALLOW_MONSTERS, true);
//                    slimeWorld.getPropertyMap().setValue(SlimeProperties.PVP, true);
//                }catch (Exception ignored){}
//                finally {CommandManager.getInstance().getWorldsInUse().remove(templateWorldName);}
//            });
//        }
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
