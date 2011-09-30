package org.michalpol.HungerStat;

import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Hunger Stat Plugin
 * @author michalpol
 * @version 0.0.1
 */

public class HungerStat extends JavaPlugin
{

	public Logger log=Logger.getLogger("Minecraft");;
	public void onEnable()
	{
		log.info("HungerStat v. 0.0.1 Enabled!");
	}
	
	
	public void onDisable()
	{
		log.info("HungerStat v. 0.0.1 Disabled!");
	}
	
	/**
	 *  Called to recognize command
	 */
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("hs"))
		{
				Player player=(Player) sender;
				int food=player.getFoodLevel();
				float sature=player.getSaturation();
				float Exhaust=player.getExhaustion();
				float totalfood=food+sature-(Exhaust / (float) 4.0);
				float maxfood=20;
				int foodpercent=(int) Math.floor((totalfood/maxfood)*100);
				totalfood=(int)(totalfood*((float)400.0));
				totalfood=totalfood/(float)100.0;
				player.sendMessage("Total FoodLevel: "+"§e"+Float.toString(totalfood)+"/20.00 ["+Integer.toString(foodpercent)+"%]§f");
				return true;
		}
		return false; 
	}
}
