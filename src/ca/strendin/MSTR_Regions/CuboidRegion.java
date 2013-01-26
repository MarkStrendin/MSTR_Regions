package ca.strendin.MSTR_Regions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;



public class CuboidRegion implements Serializable {
    private static final long serialVersionUID = -5894508785309276848L;
    private boolean bPaintBrush_CanStorePaint;
    private boolean bPaintBrush_CanApplyPaint;
    private boolean bCanBreakBlocks;
    private boolean bCanExplode;
    private boolean bCanEnemiesSpawnHere;
    private boolean bCanPlayersEnter;
    private boolean bAnnounceOnEnter;
    private boolean bProtectChests;    
    private int iHighX;
    private int iHighY;
    private int iHighZ;
    private int iLowX;
    private int iLowY;
    private int iLowZ;
    private String sOwner;
    private String sName;
    private String sWorld;
    private String sAnnounceThisOnEnter;
    private List<String> whiteList;
    
    
    // If the location is in a region, returns that region. Otherwise returns null.
    public static CuboidRegion getRegion(ArrayList<CuboidRegion> regionList,Location thisLocation) {
        return null;
    }
    
    // Returns true if the specified location is inside this region
    public boolean isInThisRegion(Location thisLocation) {
        
        boolean returnMe = false;
        
        if (thisLocation.getWorld().getName().equals(sWorld)) {        
            if ((thisLocation.getBlockX() >= iLowX)  && (thisLocation.getBlockX() <= iHighX)) {
                if ((thisLocation.getBlockY() >= iLowY)  && (thisLocation.getBlockY() <= iHighY)) {
                    if ((thisLocation.getBlockZ() >= iLowZ)  && (thisLocation.getBlockZ() <= iHighZ)) {
                        returnMe = true;
                    }
                }            
            }
        }
                
        return returnMe;
    }
    
    public String toString() {
        return "Region "+sName+" ("+iHighX+","+iHighY+","+iHighZ+") ("+iLowX+","+iLowY+","+iLowZ+") Owner: "+sOwner;        
    }
    
    public String getOwner() {
        return sOwner;
    }
    
    public String getWorld() {
        return sWorld;
    }
    
    public String getCoordinateString() {
        return "("+iHighX+","+iHighY+","+iHighZ+") to ("+iLowX+","+iLowY+","+iLowZ+") in \"" + sWorld + "\"";        
    }
    
    public String getAnnounceText() {
    	if (sAnnounceThisOnEnter == null) {
    		return "You have entered the region \"" + sName + "\"";
    	} else {
    		return sAnnounceThisOnEnter;
    	}
    }
    
    public boolean canPlayersOpenChests() {
    	return bProtectChests;
    }
    
    public boolean canBreakBlocks() {
    	return bCanBreakBlocks;
    }
    
    public boolean canEnemyMobsSpawnHere() {
    	return bCanEnemiesSpawnHere;
    }
    
    public boolean canPlayersEnter() {
    	return bCanPlayersEnter;
    }
    
    public boolean canExplode() {
    	return bCanExplode;
    }
    
    public boolean canStorePaint() {
        return bPaintBrush_CanStorePaint;
    }
    
    public boolean canApplyPaint() {
        return bPaintBrush_CanApplyPaint;
    }
    
    public boolean canAnnounceOnEnter() {
    	return bAnnounceOnEnter;
    }
    
    public String getName() {
        return sName;
    }
    
    public void setCanPlayersOpenChests(boolean value) {
    	bProtectChests = value;    	
    }
    
    public void setAnnounceText(String value) {
    	sAnnounceThisOnEnter = value;
    }
    
    public void setCanEnemyMobsSpawnHere(boolean value) {
        bCanEnemiesSpawnHere = value;        
    }
    
    public void setCanPlayersEnter(boolean value) {
        bCanPlayersEnter = value;        
    }
    
    public void setCanBreakBlocks(boolean value) {
    	bCanBreakBlocks = value;
    }
    
    public void setCanExplode(boolean value) {
    	bCanExplode = value;
    }
    
    public void setCanStorePaint(boolean value) {
        bPaintBrush_CanStorePaint = value;
    }
    
    public void setCanApplyPaint(boolean value) {
        bPaintBrush_CanApplyPaint = value;
    }
    
    public void setOwner(String newOwner) {
    	sOwner = newOwner;
    }
    
    public void setAnnounceOnEnter(boolean value) {
    	bAnnounceOnEnter = value;
    }
    
    public CuboidRegion(String regionName, Player owner, CuboidPreRegion preRegion) {
        // For coordinates, make sure that the high and low values get sorted out properly
        sName = regionName;
        sOwner = owner.getDisplayName();
        sWorld = preRegion.loc1.getWorld().getName();
        
        
        
        // Defaults     
        bPaintBrush_CanApplyPaint = false;
        bPaintBrush_CanStorePaint = true;
        bCanBreakBlocks = false;
        bCanExplode = false;
        bCanEnemiesSpawnHere = true;
        bCanPlayersEnter = true;
        bAnnounceOnEnter = false; 
        bProtectChests = false;
        sAnnounceThisOnEnter = null;
        
        // X
        if (preRegion.loc1.getBlockX() > preRegion.loc2.getBlockX()) {
            iHighX = preRegion.loc1.getBlockX();
            iLowX = preRegion.loc2.getBlockX();
        } else {
            iHighX = preRegion.loc2.getBlockX();
            iLowX = preRegion.loc1.getBlockX();            
        }
        
        // Y
        if (preRegion.loc1.getBlockY() > preRegion.loc2.getBlockY()) {
            iHighY = preRegion.loc1.getBlockY();
            iLowY = preRegion.loc2.getBlockY();
        } else {
            iHighY = preRegion.loc2.getBlockY();
            iLowY = preRegion.loc1.getBlockY();            
        }
        
        // Z
        if (preRegion.loc1.getBlockZ() > preRegion.loc2.getBlockZ()) {
            iHighZ = preRegion.loc1.getBlockZ();
            iLowZ = preRegion.loc2.getBlockZ();
        } else {
            iHighZ = preRegion.loc2.getBlockZ();
            iLowZ = preRegion.loc1.getBlockZ();            
        }       
    }
    
    
    
    
}
