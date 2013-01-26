package ca.strendin.MSTR_Regions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CuboidRegionHandler {   
    
    // Temporary workspace for inputting coordinates into a new region
    private static Hashtable<Player,Block> playerWorkspace = new Hashtable<Player,Block>();
    private static Hashtable<Player,CuboidPreRegion> preRegions = new Hashtable<Player,CuboidPreRegion>();    
    private static ArrayList<CuboidRegion> regions = new ArrayList<CuboidRegion>();
	
    // Where the serialized regions are being stored
	private static File regionDir;
    
    public static CuboidRegion getRegionByName(String name) {        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.getName().toLowerCase().equals(name.toLowerCase())) {
                return thisRegion;
            }
        }
        return null;
    }
    
    // Deserializes regions
    public static boolean initRegions(MSTR_Regions plugin) {
    	boolean returnMe = false;
    	File pluginDirectory = plugin.getDataFolder(); 
    	regionDir = new File(pluginDirectory, "regions");
    	
        MSTR_Comms.logThis("Initializing regions from \"" + regionDir.getPath() + "\"");
        
        // Clear current region lists
        playerWorkspace.clear();
        preRegions.clear();
        regions.clear();
                
        if (!pluginDirectory.exists()) {        
            pluginDirectory.mkdir();           
        }
        
        if (!regionDir.exists()) {
        	MSTR_Comms.logThis("Region directory does not exist - creating");
            regionDir.mkdir();
            // Since we know there will be no regions to load, don't bother trying
            return false;
        }
        
        // For each file contained in it, attempt to load
        FilenameFilter filter = new FilenameFilter() {
          public boolean accept(File dir, String name) {
              return name.endsWith(".msregion");          
          }
        };
        
        String[] regionFileNames = regionDir.list(filter);
        
        if (regionFileNames.length > 0) {
        
	        for (String thisRegionFile : regionFileNames) {
	            try {
	                FileInputStream fileIn = new FileInputStream(regionDir + "/" + thisRegionFile);
	                ObjectInputStream in = new ObjectInputStream(fileIn);
	                regions.add((CuboidRegion) in.readObject());
	                MSTR_Comms.logThis(" Loaded region: " + thisRegionFile);
	                in.close();
	                fileIn.close();
	                returnMe = true;
	            } catch (Exception e) {
	            	MSTR_Comms.logThis(" Failed to load region \"" + thisRegionFile + "\"");
	            }
	        }
        } else {
        	MSTR_Comms.logThis(" No regions to load");        	
        }
        
        return returnMe;
    }    
    
    // Serializes all regions
    public static void saveAllRegions(){
    	if ((regionDir.exists())) {
    		MSTR_Comms.logThis("Saving regions to \"" + regionDir.getPath() + "\"");
            for (CuboidRegion thisRegion : regions) {
                try {
                    FileOutputStream fileout = new FileOutputStream(regionDir.getPath() + "\\" + thisRegion.getName() + ".msregion");
                    ObjectOutputStream out = new ObjectOutputStream(fileout);
                    out.writeObject(thisRegion);
                    out.close();
                    fileout.close();                
                } catch (IOException i) {
                	MSTR_Comms.logThis("Failed to save region data for " + thisRegion.getName());
                }
            }    		
    	} else {
    		MSTR_Comms.logThis("No directory to save regions to!");
    	}    	
    }
    
    //TODO: Actually sanitize the string
    public static String sanitizeInput(String input) {
        
        String working = null;
        
        // Only allow a region name to be 20 characters long (just because)
        // only allow a region name to be lower case
        if (input.length() > 20) {
            working = input.substring(0, 20).toLowerCase();            
        } else {
            working = input.toLowerCase();
        }
        
        // only output alphabet characters and numbers - remove anything else
        
        String REGEX = "[^a-z0-9]";
        
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(working); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
          m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        
        working = sb.toString();
        return working;
    }
    
    private static String displayBoolFlag(boolean thisBool, String flagName) {
    	ChatColor trueColor = ChatColor.GREEN;
    	ChatColor falseColor = ChatColor.RED;
    	
    	if (thisBool) {
    		return trueColor + flagName;
    	} else {
    		return falseColor + flagName;    		
    	}    	
    }
    
    public static void sendRegionInfo(Player player, CuboidRegion specifiedRegion) {  
    	ChatColor mainColour = ChatColor.AQUA;
    	ChatColor valueColour = ChatColor.WHITE;
    	
    	MSTR_Comms.sendPlayer(player,mainColour + "Info for region \"" + valueColour +specifiedRegion.getName()+ mainColour +"\":");
    	MSTR_Comms.sendPlayerInfo(player,mainColour +" Coordinates: " + valueColour + specifiedRegion.getCoordinateString());              
    	MSTR_Comms.sendPlayerInfo(player,mainColour +" Owner: " + valueColour + specifiedRegion.getOwner());
    	MSTR_Comms.sendPlayerInfo(player,mainColour +" Flags: " + displayBoolFlag(!specifiedRegion.canBreakBlocks(),"blockprotect") + ", "
        + displayBoolFlag(!specifiedRegion.canExplode(),"explosionprotect") + ", "
        + displayBoolFlag(specifiedRegion.canPlayersEnter(),"playerentry") + ", "
        + displayBoolFlag(specifiedRegion.canEnemyMobsSpawnHere(),"enemyspawn") + ", "
        + displayBoolFlag(!specifiedRegion.canPlayersOpenChests(),"containerprotect") + ", "        
        + displayBoolFlag(specifiedRegion.canAnnounceOnEnter(),"announce")
        );        

        if (specifiedRegion.canAnnounceOnEnter()) {
        	MSTR_Comms.sendPlayerInfo(player,mainColour +" Announce text: " + valueColour + specifiedRegion.getAnnounceText());
        }
    }
    
    public static void removeRegion (Player thePlayer, String regionName) {
        
        boolean removedRegion = false;        
        CuboidRegion foundRegion = null;
        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.getName().contentEquals(regionName)) {
                foundRegion = thisRegion;
                removedRegion = true;
            }
        }
 
        if (foundRegion != null) {
            if (removedRegion = true) {
                regions.remove(foundRegion);
                File thisRegionFile = new File(regionDir,foundRegion.getName() + ".msregion");
                if (thisRegionFile.exists()) {
                    thisRegionFile.delete();
                }
            }
        }
        
        if (removedRegion) {            
        	MSTR_Comms.sendPlayer(thePlayer, "Region removed");
        } else {
        	MSTR_Comms.sendPlayerError(thePlayer, "Region not found");
        }
    }    

	public static void setRegionOwner (Player thePlayer, String regionName, String newOwner) {
	    
	    boolean regionOwnerSet = false;        
	    CuboidRegion foundRegion = null;
	    
	    for (CuboidRegion thisRegion : regions) {
	        if (thisRegion.getName().endsWith(regionName)) {
	            foundRegion = thisRegion;
	            regionOwnerSet = true;
	        }
	    }
	
	    if (foundRegion != null) {
	        if (regionOwnerSet = true) {
	        	foundRegion.setOwner(newOwner);	            
	        }
	    }
	    
	    if (regionOwnerSet) {            
	    	MSTR_Comms.sendPlayer(thePlayer, "Region owner set to " + newOwner);
	    } else {
	    	MSTR_Comms.sendPlayerError(thePlayer, "Region not found");
	    }
	}    
    
    public static void createRegion(Player thePlayer, String regionName) {
        // Coordinates should already be stored in the preRegions hashtable        
        
        // Check for a pre-region
        if (preRegions.containsKey(thePlayer)) {           
            CuboidRegion newRegion = new CuboidRegion(sanitizeInput(regionName.toLowerCase()), thePlayer,preRegions.get(thePlayer));           
            regions.add(newRegion);
            saveAllRegions();
            MSTR_Comms.sendPlayer(thePlayer, "New region created: " + ChatColor.AQUA + sanitizeInput(regionName.toLowerCase()));
        } else {
        	MSTR_Comms.sendPlayerError(thePlayer, "Not ready to create a region yet!");
        }        
    }
    
    public static void inputCoordinate(Player thePlayer, Block theBlock) {               
        
        // If there is already a saved pre-region, delete it
        if (preRegions.containsKey(thePlayer)) {
            preRegions.remove(thePlayer);
        }
        
        
        // If the player has a block stored already, create a region
        if (playerWorkspace.containsKey(thePlayer)) {
           CuboidPreRegion preRegion = new CuboidPreRegion(theBlock,playerWorkspace.get(thePlayer));
           
           playerWorkspace.remove(thePlayer);
           
           if (preRegions.containsKey(thePlayer)) {
               preRegions.remove(thePlayer);
           }            
           
           preRegions.put(thePlayer, preRegion);
           
           MSTR_Comms.sendPlayer(thePlayer,"Ready to create a region!");  
           MSTR_Comms.sendPlayerInfo(thePlayer,"Use \"/msregion create <name>\" to create a region");  
        } else {
        // If the player does not have a block stored already, just store it
            playerWorkspace.put(thePlayer, theBlock);
            MSTR_Comms.sendPlayer(thePlayer,"Block location #1 stored");
            MSTR_Comms.sendPlayer(thePlayer,"Now, select the block on the opposite corner of your new region");
        }
    }
        
    public static void getRegionInfoHere(Player thePlayer, Block thisBlock) {
        // Go through list of regions and check
        
        Location blockLocation = thisBlock.getLocation();
        
        boolean foundMatch = false;
        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(blockLocation)) {
                sendRegionInfo(thePlayer,thisRegion);                               
                foundMatch = true;
            }
        }
        
        if (foundMatch == false) {
        	MSTR_Comms.sendPlayerInfo(thePlayer,"No regions here!");
        }
    }
    
    public static CuboidRegion getRegionHere(Block thisBlock) {
        // Go through list of regions and check
        
        Location blockLocation = thisBlock.getLocation();
                
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(blockLocation)) {                                               
                return thisRegion;
            }
        }
        return null;
    }
    
    public static CuboidRegion getRegionHere(Location blockLocation) {
        // Go through list of regions and check
                
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(blockLocation)) {                                               
                return thisRegion;
            }
        }
        return null;
    }
    
     
    public static void listAllRegions(Player thePlayer) {        
    	if (!regions.isEmpty()) {    	
        	MSTR_Comms.sendPlayer(thePlayer, "Current regions (" + regions.size() + "):");
	    	for (CuboidRegion thisRegion : regions) {
	        	MSTR_Comms.sendPlayerInfo(thePlayer, " " + thisRegion.toString());
	        }
    	} else {
    		MSTR_Comms.sendPlayer(thePlayer, "No regions defined!");
    	}
    }
    
    /*
    
    public static boolean canSetInkHere(Player player, Block block) {
        boolean returnMe = true;        
        // Check for ignore permission
        if (BDPermissions.ignoresRegions(player)) {
            return true;
        }
        
        // Check for regions        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(block.getLocation())) {
            	if (thisRegion.getOwner().toLowerCase().contentEquals((player.getName().toLowerCase()))) {
                	return true;
                } else if (thisRegion.canStorePaint() == false) {
                    returnMe = false;
                }
            }
        }
        
        return returnMe;         
    }
    */
    
    public static boolean canBreakBlocksHere(Player player, Block block) {
        boolean returnMe = true;        
        // Check for ignore permission
        if (MSTR_Permissions.ignoresRegions(player)) {
            return true;
        }
        
        // Check for regions        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(block.getLocation())) {
            	
            	if (thisRegion.getOwner().toLowerCase().contentEquals((player.getName().toLowerCase()))) {
                	return true;
                } else if (thisRegion.canBreakBlocks() == false) {
                    returnMe = false;
                }
            }
        }
        
        return returnMe;         
    }
    
    public static boolean canOpenContainersHere(Player player, Block block) {
        boolean returnMe = true;        
        // Check for ignore permission
        if (MSTR_Permissions.ignoresRegions(player)) {
            return true;
        }
        
        // Check for regions        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(block.getLocation())) {
            	
            	if (thisRegion.getOwner().toLowerCase().contentEquals((player.getName().toLowerCase()))) {
                	return true;
                } else if (thisRegion.canPlayersOpenChests() == false) {
                    returnMe = false;
                }
            }
        }
        
        return returnMe;         
    }
    
    /*
    public static boolean canPaintHere(Player player, Block block) {
        boolean returnMe = true;       
        // Check for ignore permission
        if (BDPermissions.ignoresRegions(player)) {
            return true;
        }
        
        // Check for regions        
        for (CuboidRegion thisRegion : regions) {
            if (thisRegion.isInThisRegion(block.getLocation())) {
            	if (thisRegion.getOwner().toLowerCase().contentEquals((player.getName().toLowerCase()))) {
                	return true;
                } else if (thisRegion.canApplyPaint() == false) {
                    returnMe = false;
                }
            }
        }
                
        return returnMe;        
    }
    */    
}

    