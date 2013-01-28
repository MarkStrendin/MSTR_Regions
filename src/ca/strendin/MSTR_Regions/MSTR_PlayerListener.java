package ca.strendin.MSTR_Regions;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class MSTR_PlayerListener implements Listener {	
	private static MSTR_Regions plugin;
	
	public MSTR_PlayerListener(MSTR_Regions mstr_Regions) {
		plugin = mstr_Regions;
	}

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
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getBlockClicked())) {
    		event.setCancelled(true);
    		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you are not allowed to do that here");
    	}		
	}
	
	/*
	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getBlockClicked())) {
    		event.setCancelled(true);
    		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you are not allowed to do that here");
    	}			
	}
	*/
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		
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
		Player player = event.getPlayer();		

		ArrayList<CuboidRegion> regionsEntered = CuboidRegionHandler.getRegionsHere(event.getTo());
		
		if (regionsEntered != null) {
			if (!regionsEntered.isEmpty()) {
				/*
				 * Check if a given player is allowed in a region
				 */
				if (!CuboidRegionHandler.canMoveHere(player, event.getTo(),regionsEntered)) {
					if (!CuboidRegionHandler.canMoveHere(player, event.getFrom())) {
						player.teleport(player.getLocation().getWorld().getSpawnLocation());
					} else {
						player.teleport(event.getFrom());
					}
					
					MSTR_Comms.sendPlayerError(player, "Sorry, you are not allowed in this region");
				} else {					
					for (CuboidRegion thisRegion : regionsEntered) {						
						/*
						 * Check if this region is set to announce on player entry
						 */
						if (thisRegion.shouldAnnounceOnEnter()) {
							if (CuboidRegionHandler.getRegionHere(event.getFrom().getBlock()) != thisRegion) {
								MSTR_Comms.sendPlayerInfo(player, thisRegion.getAnnounceText());
							}
						}
						
						
						
						/*
						 * Check if we should alert a player's entry into a region to the region owner
						 */
						if (thisRegion.shouldAlertOnPlayerEntry()) {
							if (!thisRegion.isOnWhiteList(player.getName())) {
								if (CuboidRegionHandler.getRegionHere(event.getFrom().getBlock()) != thisRegion) {
									Player regionOwner = plugin.getServer().getPlayer(thisRegion.getOwner());						
									if (regionOwner != null) {
										MSTR_Comms.sendPlayerInfo(regionOwner, player.getName() + " has just entered the region \"" + thisRegion.getName() + "\"");
										MSTR_Comms.sendPlayerInfo(regionOwner, " Entry from: " + event.getTo().getBlockX() + ", " + event.getTo().getBlockY() + ", " + event.getTo().getBlockZ() + " in world " + event.getTo().getWorld().getName());
									}								
								}								
							}														
						}
					}
				}
				
			}
		}
	}
    
	
    @EventHandler        
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
    	if (CuboidRegionHandler.getRegionHere(event.getLocation()) != null) {
        	if (isEntityHostile(event.getEntityType())) {
        		
        		/*
        		 * Only cancel the event if its a natural mob spawn - allow spawns from eggs/dispensors/etc
        		 */
        		if (
        				(event.getSpawnReason() == SpawnReason.CHUNK_GEN) ||
        				(event.getSpawnReason() == SpawnReason.NATURAL) ||
        				(event.getSpawnReason() == SpawnReason.VILLAGE_INVASION)
        				) {
        			event.setCancelled(true);
        			
        		}
        		        		
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
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
    	
    	if (event.getRemover() instanceof Player) {
    		Player player = (Player)event.getRemover();
    		
    		if (!CuboidRegionHandler.canBreakBlocksHere(player, event.getRemover().getLocation().getBlock())) {
        		event.setCancelled(true);
        		MSTR_Comms.sendPlayerError(player, "Sorry, you can't place or break blocks here");
        	}
    	}
    }
    
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
    	if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getBlock())) {
    		event.setCancelled(true);
    		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you can't place or break blocks here");
    	} 
    	
    }    
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	if (event.getRightClicked() instanceof ItemFrame) {
    		if (!CuboidRegionHandler.canBreakBlocksHere(event.getPlayer(), event.getRightClicked().getLocation().getBlock())) {
        		event.setCancelled(true);
        		MSTR_Comms.sendPlayerError(event.getPlayer(), "Sorry, you can't place or break blocks here");
        	} 
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
