package ca.strendin.MSTR_Regions;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class MSTR_Regions extends JavaPlugin {
    
    @Override
    public void onDisable() {        
        MSTR_Comms.logThis(this.getDescription().getName() + " disabled");        
    }

    @Override
    public void onEnable() {
    	
    	CuboidRegionHandler.initRegions(this);
    	
    	PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MSTR_PlayerListener(this), this);
    	
		getCommand("msregion").setExecutor(new MSTR_RegionCommand(this));
		getCommand("msregiontool").setExecutor(new MSTR_RegionToolCommand(this));
		
        MSTR_Comms.logThis(this.getDescription().getName() + " enabled"); 
    	
    }

}
