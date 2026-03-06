package me.ehsanmna.disaster.util;

import org.bukkit.Location;

public class LocationUtils {

    public static Location center(Location location){
        location.set(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return location.add(0.5,0,0.5);
    }

}
