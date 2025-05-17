package me.ehsanmna.disaster.model.region;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Region {

    private Location pos1;
    private Location pos2;

    public Region(Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null){
            Bukkit.getLogger().warning("Could nto create Door region!");
            return;
        }
        if (pos1.getWorld() != pos2.getWorld())
            throw new IllegalArgumentException("Positions must be in the same world!");

        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    // Get all blocks within the region
    public List<Location> getBlocks() {
        List<Location> blocks = new ArrayList<>();
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    blocks.add(new Location(world, x, y, z));

        return blocks;
    }

    // Check if a location is within the region
    public boolean isInRegion(Location location) {
        if (!location.getWorld().equals(pos1.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    // Check if a block is within the region
    public boolean isInRegion(Block block) {
        return isInRegion(block.getLocation());
    }

    public List<Chunk> getChunks() {
        Set<Chunk> chunks = new HashSet<>();
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX()) >> 4; // Convert to chunk coordinates
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;

        for (int x = minX; x <= maxX; x++)
            for (int z = minZ; z <= maxZ; z++)
                chunks.add(world.getChunkAt(x, z));

        return new ArrayList<>(chunks);
    }

    // Getters and setters
    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
}
