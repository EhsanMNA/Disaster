package me.ehsanmna.disaster.util;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;

public class SlimeWorldUtils {


    public static SlimeWorld createCloneWorld(String worldName){
//        try{
//            return SWMPlugin.getInstance().getWorld(worldName).clone(worldName+"-backup");
//        }catch (NullPointerException e){e.printStackTrace();}
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"swm clone-world "+worldName+" "+worldName+"-backup");
        return SWMPlugin.getInstance().getWorld(worldName+"-backup");
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
