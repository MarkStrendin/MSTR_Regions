package ca.strendin.MSTR_Regions;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MSTR_Comms {	
	public static String pluginName = "MSTR_Regions";
    public static ChatColor itemColor = ChatColor.GREEN;
    private static ChatColor infoColor = ChatColor.GRAY;
    private static ChatColor errorColor = ChatColor.RED;
    private static ChatColor normalColor = ChatColor.DARK_GREEN; 
    private static ChatColor serverMsgColor = ChatColor.YELLOW;   
    public static final Logger log = Logger.getLogger("Minecraft");
    

    // Public so that BDCommands methods can see it
   
    
    public static void sendPlayer(Player tothisplayer, String message) {
        tothisplayer.sendMessage(normalColor + message);        
    }
    
    public static void sendPlayerInfo(Player tothisplayer, String message) {
        tothisplayer.sendMessage(infoColor + message);        
    }
    
    public static void logThis(String message) {
        log.info("[" + pluginName + "] " + message);
    }    
    
    public static void sendConsole(String message) {
        logThis(message);        
    }
    
    public static void sendConsoleOnly(String message) {
        System.out.println(message);
    }
    
    
    public static void sendPlayerError(Player tothisplayer, String message) {
        tothisplayer.sendMessage(errorColor  + message);
    }
    
    public static void permDenyMsg(Player tothisplayer) {
        tothisplayer.sendMessage(errorColor + "You do not have permission to do that");        
    }
    
    public static void sendtoCommandSender(CommandSender sender,String message) {
        if (sender instanceof Player) {
        	sendPlayer((Player)sender,message);
        } else {
            logThis(message);            
        }
    }
    
    public static void sendToOps(String message) {
        for(Player thisPlayer : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
            if (thisPlayer.isOp()) {
                thisPlayer.sendMessage(normalColor + "[MSTR_Regions] " +serverMsgColor + message);
            }        
        }        
    }
    
}
