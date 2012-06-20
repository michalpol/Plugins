package org.michalpol.HungerStat;

import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Hunger Stat Plugin
 * @author michalpol
 * @version 0.0.2
 */

public class HungerStat extends JavaPlugin
{

	public Logger log=Logger.getLogger("Minecraft");;
	public void onEnable()
	{
		log.info("HungerStat v. 0.0.3 Enabled!");
	}
	
	
	public void onDisable()
	{
		log.info("HungerStat v. 0.0.3 Disabled!");
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
				int WSn=(int) Math.floor(totalfood/0.01);
				int Sp=(int) Math.floor(totalfood/0.1);
				int Sw=(int) Math.floor(totalfood/0.015);
				int J=(int) Math.floor(totalfood/0.2);
				int Js=(int) Math.floor(totalfood/0.8);
				int BB=(int) Math.floor(totalfood/0.025);
				int AD=(int) Math.floor(totalfood/0.03);
				
				player.sendMessage("Total FoodLevel: "+"§e"+Float.toString(totalfood)+"["+Integer.toString(foodpercent)+"%]§f");
				player.sendMessage("Minecraft Level: "+Integer.toString(player.getLevel()));
				player.sendMessage("Minecraft Experience: "+ Integer.toString(((int)((player.getExp())*100))) + "%");
				player.sendMessage("Minecraft Total Experience: "+Integer.toString(player.getTotalExperience()));
				player.sendMessage("W/S: "+Integer.toString(WSn)+"m Sp: "+Integer.toString(Sp)+"m Sw: "+Integer.toString(Sw)+"m J: "+Integer.toString(J)+"x Js: "+Integer.toString(Js)+"x BB: "+Integer.toString(BB)+"x A/D: "+Integer.toString(AD)+"x");
				return true;
		}
		return false; 
	}
}
