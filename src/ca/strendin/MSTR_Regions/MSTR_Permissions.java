package ca.strendin.MSTR_Regions;

import org.bukkit.entity.Player;


public class MSTR_Permissions {
        
    private static boolean hasPermission(Player thisplayer, String permName, boolean defaultPermission) {
        return thisplayer.hasPermission(permName);
    }
    
    /*
     * Ability to manage regions
     */
    public static boolean canManageRegions(Player player) {
        return hasPermission(player,"blockduplicator.region.manage",player.isOp());
    }
    
    /*
     * Ignore regions
     */
    public static boolean ignoresRegions(Player player) {
        return hasPermission(player,"blockduplicator.region.ignore",player.isOp());
    }
    
    
    
    
    
    
}
