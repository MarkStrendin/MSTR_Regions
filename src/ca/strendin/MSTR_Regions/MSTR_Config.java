package ca.strendin.MSTR_Regions;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class MSTR_Config {
    public static String regionToolName = "Region tool";
    public static int regionToolID = 286; // Golden Axe
    
    public static boolean isProtectableContainer(Material thisMaterial) {
		if (
				(thisMaterial == Material.CHEST) ||
				(thisMaterial == Material.FURNACE) ||
				(thisMaterial == Material.DISPENSER)
				) {
			return true;			
		} else {
			return false;
		}
	}
    
    public static boolean isProtectableContainer(Block thisBlock) {
    	return isProtectableContainer(thisBlock.getType());
	}

}
