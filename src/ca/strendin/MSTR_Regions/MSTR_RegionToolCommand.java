package ca.strendin.MSTR_Regions;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MSTR_RegionToolCommand implements CommandExecutor {

	public MSTR_RegionToolCommand(MSTR_Regions mstr_Regions) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (MSTR_Permissions.canManageRegions(player)) {
				givePlayerRegionTool(player);
			} else {
				MSTR_Comms.permDenyMsg(player);
			}
		} else {
			MSTR_Comms.sendtoCommandSender(sender, "Player command only!");
		}

		return true;
	}
	
	/*
     * Gives the player a region tool
     */
    public static void givePlayerRegionTool(Player thisPlayer) {
        ItemStack RegionToolItem = new ItemStack(MSTR_Config.regionToolID,(short)1,(byte)0);
        
        // Modify the item before sending it to the player
        ItemMeta meta = RegionToolItem.getItemMeta(); 
        
        // Rename the item
        meta.setDisplayName(MSTR_Config.regionToolName);        
        
        // Set the item's lore
        ArrayList<String> newLore = new ArrayList<String>();
        newLore.add("Left click blocks to display region information");
        newLore.add("Right click blocks to create regions");
        meta.setLore(newLore);
        
        // Commit changes to the item
        RegionToolItem.setItemMeta(meta);
        
        MSTR_Comms.sendPlayer(thisPlayer, "Giving Info tool: " + MSTR_Comms.itemColor + RegionToolItem.getType());
        MSTR_Comms.logThis("Giving " + thisPlayer.getDisplayName() + " a paintbrush tool (" + RegionToolItem.getType() + ")");
        
        thisPlayer.getInventory().addItem(RegionToolItem);
    }

}
