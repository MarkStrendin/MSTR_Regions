package ca.strendin.MSTR_Regions;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class CuboidPreRegion {
    Location loc1;
    Location loc2;
    
    public CuboidPreRegion(Location one, Location two) {
        loc1 = one;        
        loc2 = two;
    }
    
    public CuboidPreRegion(Block one, Block two) {
        loc1 = one.getLocation();        
        loc2 = two.getLocation();
    }
    
    public String toString() {
        return "Pre-Region: ("+loc1.getBlockX()+","+loc1.getBlockY()+","+loc1.getBlockZ()+") ("+loc2.getBlockX()+","+loc2.getBlockY()+","+loc2.getBlockZ()+")";        
    }
}

