package ca.strendin.MSTR_Regions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class MSTR_PlayerListener implements Listener {	
	public MSTR_PlayerListener(MSTR_Regions mstr_Regions) {}

	private boolean isEntityHostile(EntityType thisEntity) {    	
    	if (
    			(thisEntity == EntityType.BLAZE) ||
    			(thisEntity == EntityType.CAVE_SPIDER) ||
    			(thisEntity == EntityType.CREEPER) ||
    			(thisEntity == EntityType.ENDERMAN) ||
    			(thisEntity == EntityType.GHAST) ||
    			(thisEntity == EntityType.GIANT) ||
    			(thisEntity == EntityType.PIG_ZOMBIE) ||
    			(thisEntity == EntityType.SILVERFISH) ||
    			(thisEntity == EntityType.SKELETON) ||
    			(thisEntity == EntityType.SLIME) ||
    			(thisEntity == EntityType.SPIDER) ||
    			(thisEntity == EntityType.WITCH) ||    			
    			(thisEntity == EntityType.ZOMBIE)    			
    			){
    		return true;
    	} else {
    		return false;
    	}
    }
	
	private boolean isPlayerHoldingRegionTool(Player player) {
		if (player.getItemInHand().getTypeId() > 0) {
			ItemMeta itemInHandMeta = player.getItemInHand().getItemMeta();        	
        	if (itemInHandMeta.hasDisplayName()) {
        		if (itemInHandMeta.getDisplayName().contentEquals(MSTR_Config.regionToolName)) {        		
    				if (player.getItemInHand().getTypeId() == MSTR_Config.regionToolID) {
    					return true;    					
    				}
        		}
        	}			
		}		
		return false;
	}
	
	
	
	@EventHandler        
    public void onPlayerInteract(PlayerInteractEvent event) { 
    	Player player = event.getPlayer();
    	
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		// For handling the region tool
        	if (MSTR_Permissions.canManageRegions(player)) {
    			if (isPlayerHoldingRegionTool(player)) {	               
        			CuboidRegionHandler.getRegionInfoHere(player, event.getClickedBlock());
                    event.setCancelled(true);	                            
                }        		
        	} 
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		// For handling the region tool
        	if (MSTR_Permissions.canManageRegions(player)) {
    			if (isPlayerHoldingRegionTool(player)) {	                            
        			CuboidRegionHandler.inputCoordinate(player, event.getClickedBlock()); 
                    event.setCancelled(true);	                            
                }        		
        	}
        	
        	// For handling container protection
        	if (MSTR_Config.isProtectableContainer(event.getClickedBlock())) {
                if (CuboidRegionHandler.getRegionHere(event.getClickedBlock()) != null) {
		    		if (!CuboidRegionHandler.canOpenContainersHere(player, event.getClickedBlock())) {
		    			event.setCancelled(true);
		    			MSTR_Comms.sendPlayerError(player, "Sorry, containers are protected in this region");
		    		}
                }
        		        		
        	}
        	
        } 
	}
	
	@EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {    	
    	CuboidRegion thisRegion = CuboidRegionHandler.getRegionHere(event.getTo());
    	if (thisRegion != null) {
    		Player player = event.getPlayer();
    		
 		
    		// For announcing upon entry
    		
    		// Check to see if the player is just entering the region, so we don't spam them with messages while they
    		// are in the region
    		if (CuboidRegionHandler.getRegionHere(event.getFrom()) == null) {
    		
				if (thisRegion.canAnnounceOnEnter()) {
					MSTR_Comms.sendPlayerInfo(player, thisRegion.getAnnounceText());
				}
    		}
    		
    		// For checking to see if they are allowed in the region
    		
    		// Check to see if they are the owner of the region
    		
    		if (!thisRegion.canPlayersEnter()) {
				if (!thisRegion.getOwner().toLowerCase().contentEquals((player.getName().toLowerCase()))) {
					if (CuboidRegionHandler.getRegionHere(event.getFrom()) == thisRegion) {
						player.teleport(player.getLocation().getWorld().getSpawnLocation());
					} else {
						player.teleport(event.getFrom());
					}
					MSTR_Comms.sendPlayerError(player, "Sorry, you are not allowed in the region \""+thisRegion.getName()+"\"");
				}
    		}    		
    	}
    }
    
    @EventHandler        
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (CuboidRegionHandler.getRegionHere(event.getLocation()) != null) {
        	if (isEntityHostile(event.getEntityType())) {
        		event.setCancelled(true);        		
        	}        		
        }
    }
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {    	
    	if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getBlock())) {
    		event.setCancelled(true);
    		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you can't place or break blocks here");
    	}    
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
    	if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getBlock())) {
    		event.setCancelled(true);
    		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you can't place or break blocks here");
    	}       
    }
    
    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
    	for (Block thisBlock : event.blockList()) {
    		CuboidRegion regionHere = CuboidRegionHandler.getRegionHere(thisBlock);
    		if (regionHere != null) {
    			if (!regionHere.canExplode()) {
    				event.setCancelled(true);
    			}
    		}
    	}
    }  

}
