package ca.strendin.MSTR_Regions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MSTR_RegionCommand implements CommandExecutor {
    private final MSTR_Regions plugin;
    
    public MSTR_RegionCommand(MSTR_Regions plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player requestplayer = (Player)sender;
            
            if (MSTR_Permissions.canManageRegions(requestplayer)) {                                    
                if (args.length > 0) {
                    String param = args[0].toLowerCase();                
                    if (param.toLowerCase().equals("create")) {
                        HandleCreateCommand(requestplayer,args);
                    } else if (param.toLowerCase().equals("flag")) {
                        HandleFlagCommand(requestplayer,args);                    
                    } else if (param.toLowerCase().equals("list")) {
                        HandleListCommand(requestplayer,args);                    
                    } else if (param.toLowerCase().equals("remove")) {
                        HandleRemoveCommand(requestplayer,args);               
                    } else if (param.toLowerCase().equals("whitelist")) {
                    	HandleWhiteListCommand(requestplayer,args);
                    } else if (param.toLowerCase().equals("load")) {
                        HandleLoadCommand(requestplayer,args);
                    } else if (param.toLowerCase().equals("save")) {
                        HandleSaveCommand(requestplayer,args);
                    } else if (param.toLowerCase().equals("setowner")) {
                        HandleSetOwnerCommand(requestplayer,args);
                    } else if (param.toLowerCase().equals("info")) {
                        HandleInfoCommand(requestplayer,args);
                    }
                } else {
                	MSTR_Comms.sendPlayer(requestplayer, "/" + command.getLabel());
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    create <name>");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    info <region name>");                    
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    remove <region name>");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    list"); 
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    setowner <region name> <new owner name>");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    whitelist <region name> add|remove <player name>");                    
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    flag <region name> <flag>");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "    flags: blockprotect, explosionprotect, playerentry, ");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "           enemyspawn, announce, announcetext, ");
                    MSTR_Comms.sendPlayerInfo(requestplayer, "           containerprotect");
                }
            }
        } else {
        	MSTR_Comms.sendConsoleOnly("This command is designed for players only, sorry");
        }
        return true;
    }
    
    private void HandleWhiteListCommand(Player player, String[] args) {
    	if (args.length > 3) {
    		String specifiedPlayerName = args[3];
    		String subCommand = args[2];
            CuboidRegion specifiedRegion = CuboidRegionHandler.getRegionByName(args[1]);
    		
            if (specifiedRegion != null) {
            	if (subCommand.toLowerCase().equals("add")) {
            		specifiedRegion.addToWhiteList(specifiedPlayerName);
            		CuboidRegionHandler.saveAllRegions();
            		MSTR_Comms.sendPlayerInfo(player, "\""+specifiedPlayerName+"\" added to whitelist for region \""+specifiedRegion.getName()+"\"");
        		} else if (subCommand.toLowerCase().equals("remove")) {
        			specifiedRegion.removeFromWhiteList(specifiedPlayerName);
        			CuboidRegionHandler.saveAllRegions();
        			MSTR_Comms.sendPlayerInfo(player, "\""+specifiedPlayerName+"\" removed from whitelist for region \""+specifiedRegion.getName()+"\"");
        		} else {
            		MSTR_Comms.sendPlayerError(player, "Usage: /msregion whitelist <region> add <playername>");
            		MSTR_Comms.sendPlayerError(player, "       /msregion whitelist <region> remove <playername>");    			
        		}            	
            } else {
            	MSTR_Comms.sendPlayerError(player, "Region \""+args[1]+"\"not found!");
            }
    		
    	} else {    		
    		MSTR_Comms.sendPlayerError(player, "Usage: /msregion whitelist <region> add <playername>");
    		MSTR_Comms.sendPlayerError(player, "       /msregion whitelist <region> remove <playername>");
    	}
    }
    
    private void HandleSaveCommand(Player player, String[] args) {
        CuboidRegionHandler.saveAllRegions();
    }
    
    private void HandleListCommand(Player player, String[] args) {
        CuboidRegionHandler.listAllRegions(player);        
    }
    
    private void HandleSetOwnerCommand(Player player, String[] args) {
    	
    	for (String thisArg : args) {
    		MSTR_Comms.sendPlayerError(player, thisArg);
    	}
    	    	
    	
    	
    	// Make sure the player entered a name
        if (args.length == 3) {
            CuboidRegionHandler.setRegionOwner(player, args[1], args[2]);                            
        } else {
        	MSTR_Comms.sendPlayerError(player, "Region name and new owner required");
            MSTR_Comms.sendPlayerError(player, "Usage: /msregion setowner <region name> <new owner name>");
        }
        
    	
    	CuboidRegionHandler.saveAllRegions();    
    }
    
    private void HandleLoadCommand(Player player, String[] args) {
    	if (CuboidRegionHandler.initRegions(plugin)) {
    		MSTR_Comms.sendPlayer(player, "Regions (re)loaded");
    	} else {
    		MSTR_Comms.sendPlayer(player, "Error loading regions - see the server console or server logs for more information");
    	}
    }
    
    private void HandleRemoveCommand(Player player, String[] args) {
     // Make sure the player entered a name
        if (args.length > 1) {
            CuboidRegionHandler.removeRegion(player,args[1]);                            
        } else {
        	MSTR_Comms.sendPlayerError(player, "Region name required");
        	MSTR_Comms.sendPlayerError(player, "Usage: /msregion remove <name>");
        }
    }
    
    private void HandleFlagCommand(Player player, String[] args) {
        if (args.length > 1) {
            if (args.length > 2) {
                String regionName = args[1];
                CuboidRegion specifiedRegion = CuboidRegionHandler.getRegionByName(regionName);
                
                if (specifiedRegion != null) {                    
                    String flagName = args[2];                    
                    if (flagName.equals("blockprotect")) {
                        if (specifiedRegion.canBreakBlocks()) {
                            specifiedRegion.setCanBreakBlocks(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer allow breaking blocks");
                        } else {
                            specifiedRegion.setCanBreakBlocks(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now allow breaking blocks");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();
                    } else if (flagName.equals("explosionprotect")) {
                        if (specifiedRegion.canExplode()) {
                            specifiedRegion.setCanExplode(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer allow explosion damage");
                        } else {
                            specifiedRegion.setCanExplode(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now allow explosion damage");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();
                        
                    } else if (flagName.equals("playerentry")) {
                        if (specifiedRegion.canPlayersEnter()) {
                            specifiedRegion.setCanPlayersEnter(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer allow other players to enter");
                        } else {
                            specifiedRegion.setCanPlayersEnter(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now allow other players to enter");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();
                    } else if (flagName.equals("enemyspawn")) {
                        if (specifiedRegion.canEnemyMobsSpawnHere()) {
                            specifiedRegion.setCanEnemyMobsSpawnHere(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer allow enemies to spawn");
                        } else {
                            specifiedRegion.setCanEnemyMobsSpawnHere(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now allow enemies to spawn");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();
                    } else if (flagName.equals("announce")) {
                        if (specifiedRegion.canAnnounceOnEnter()) {
                            specifiedRegion.setAnnounceOnEnter(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer announce upon entry");
                        } else {
                            specifiedRegion.setAnnounceOnEnter(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now announce upon entry");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();                        
                    } else if (flagName.equals("announcetext")) {
                    	if (args.length > 3) {                    
                    		StringBuilder newAnnounceText = new StringBuilder();
                    		for (int x = 3; x < args.length; x++) {
                    			newAnnounceText.append(args[x]);
                    			newAnnounceText.append(" ");
                    		}
                    		
                    		
                    		specifiedRegion.setAnnounceText(newAnnounceText.toString());
                    		MSTR_Comms.sendPlayerInfo(player, "Announce text is now set to \"" + newAnnounceText.toString() + "\"");	
                    		CuboidRegionHandler.saveAllRegions();
                    	} else { 
                    		specifiedRegion.setAnnounceText(null);	
                    		CuboidRegionHandler.saveAllRegions();
                    		MSTR_Comms.sendPlayerInfo(player, "Announce text is now reset to default");
                    		
                    	}
                    } else if (flagName.equals("containerprotect")) {
                        if (specifiedRegion.canPlayersOpenChests()) {
                            specifiedRegion.setCanPlayersOpenChests(false);
                            MSTR_Comms.sendPlayerInfo(player, "Players can no longer open chests in region \""+regionName+"\"");
                        } else {
                            specifiedRegion.setCanPlayersOpenChests(true);
                            MSTR_Comms.sendPlayerInfo(player, "Players can now open chests in region \""+regionName+"\"");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions(); 	
                         
                    } else if (flagName.equals("paint")) {
                        if (specifiedRegion.canApplyPaint()) {
                            specifiedRegion.setCanApplyPaint(false);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will no longer applying ink");
                        } else {
                            specifiedRegion.setCanApplyPaint(true);
                            MSTR_Comms.sendPlayerInfo(player, "Region \""+regionName+"\" will now allow applying ink");                            
                        }                        
                        CuboidRegionHandler.saveAllRegions();
                    } else {
                    	MSTR_Comms.sendPlayerError(player, "Flag name not valid");
                        MSTR_Comms.sendPlayerError(player, " flags: blockprotect, explosionprotect, playerentry, ");
                        MSTR_Comms.sendPlayerError(player, "        enemyspawn, announce, announcetext, ");
                        MSTR_Comms.sendPlayerError(player, "        containerprotect");
                    }
                } else {
                	MSTR_Comms.sendPlayerError(player, "Region \""+regionName+"\" not found");
                }
            } else {
            	MSTR_Comms.sendPlayerError(player, "Flag name required");                                
            }                                                        
        } else {
        	MSTR_Comms.sendPlayerError(player, "Region name required");
            MSTR_Comms.sendPlayerError(player, "Usage: /msregion flag <region name> <flag name>");
        }
    }   
    
    private void HandleInfoCommand(Player player, String[] args) {
        if (args.length > 1) {
            String regionName = args[1];
            CuboidRegion specifiedRegion = CuboidRegionHandler.getRegionByName(regionName);
            
            if (specifiedRegion != null) {
                CuboidRegionHandler.sendRegionInfo(player,specifiedRegion);
            } else {
            	MSTR_Comms.sendPlayerError(player, "Region \""+regionName+"\" not found");
            }
        } else {
        	MSTR_Comms.sendPlayerError(player, "Region name required");
            MSTR_Comms.sendPlayerError(player, "Usage: /msregion info <name>");
        }
    }
    
    private void HandleCreateCommand(Player player, String[] args) {
        if (args.length > 1) {
            CuboidRegionHandler.createRegion(player,args[1]);                            
        } else {
        	MSTR_Comms.sendPlayerError(player, "Region name required");
            MSTR_Comms.sendPlayerError(player, "Usage: /msregion create <name>");
        }
    }

}
