package org.michalpol.ItemDurability;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemDurability extends JavaPlugin
{

	public Logger log=Logger.getLogger("Minecraft");//logger
	public void onEnable()
	{
		log.info("[ItemDurability]Enabled!");
	}
	
	
	public void onDisable()
	{
		log.info("[ItemDurability]Disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("dur"))
		{
			Player player=(Player) sender;
			if(args.length==0)//no mode selected
			{
			ItemStack items=player.getItemInHand();
			int durable=items.getDurability();
			Material mat=items.getType();
			int maxdurable=mat.getMaxDurability();
			durable=maxdurable-durable;
			String max=Integer.toString(maxdurable);
			String current=Integer.toString(durable);
			String colorcode="§a";
			int prc=0;
			if(maxdurable>0)
			{
				prc=((durable*100)/maxdurable);
			if(((durable*100)/maxdurable)>66){colorcode="§a";}
			if(((durable*100)/maxdurable)<=66 && ((durable*100)/maxdurable)>33){colorcode="§e";}
			if(((durable*100)/maxdurable)<=33){colorcode="§4";}
			player.sendMessage("Durability: "+colorcode+current+"/"+max+" - "+prc+"%");
			}
			else
			{
				colorcode="§a";
				player.sendMessage("Durability: "+colorcode+current);
			}
			return true;
			}
			else//mode selected
			{
				if(args[0].equalsIgnoreCase("a"))
				{
					ItemStack slotHead = player.getInventory().getHelmet();
					ItemStack slotBody = player.getInventory().getChestplate();
					ItemStack slotLegs = player.getInventory().getLeggings();
					ItemStack slotFeet = player.getInventory().getBoots();
					Material matHead=slotHead.getType();
					Material matBody=slotBody.getType();
					Material matLegs=slotLegs.getType();
					Material matFeet=slotFeet.getType();					
					String descHead="No Armor";
					String descBody="No Armor";
					String descLegs="No Armor";
					String descFeet="No Armor";
					if(slotHead.getTypeId() == 298) {descHead="Leather Cap";}
					if(slotBody.getTypeId() == 299) {descBody="Leather Tunic";}
					if(slotLegs.getTypeId() == 300) {descLegs="Leather Pants";}
					if(slotFeet.getTypeId() == 301) {descFeet="Leather Boots";}
					if(slotHead.getTypeId() == 302) {descHead="Chainmail Helmet";}
					if(slotBody.getTypeId() == 303) {descBody="Chainmail Chestplate";}
					if(slotLegs.getTypeId() == 304) {descLegs="Chainmail Leggings";}
					if(slotFeet.getTypeId() == 305) {descFeet="Chainmail Boots";}
					if(slotHead.getTypeId() == 306) {descHead="Iron Helmet";}
					if(slotBody.getTypeId() == 307) {descBody="Iron Chestplate";}
					if(slotLegs.getTypeId() == 308) {descLegs="Iron Leggings";}
					if(slotFeet.getTypeId() == 309) {descFeet="Iron Boots";}
					if(slotHead.getTypeId() == 310) {descHead="Diamond Helmet";}
					if(slotBody.getTypeId() == 311) {descBody="Diamond Chestplate";}
					if(slotLegs.getTypeId() == 312) {descLegs="Diamond Leggings";}
					if(slotFeet.getTypeId() == 313) {descFeet="Diamond Boots";}
					if(slotHead.getTypeId() == 314) {descHead="Gold Helmet";}
					if(slotBody.getTypeId() == 315) {descBody="Gold Chestplate";}
					if(slotLegs.getTypeId() == 316) {descLegs="Gold Leggings";}
					if(slotFeet.getTypeId() == 317) {descFeet="Gold Boots";}					
					int durHead=slotHead.getDurability();
					int durBody=slotBody.getDurability();
					int durLegs=slotLegs.getDurability();
					int durFeet=slotFeet.getDurability();
					int mdurHead=matHead.getMaxDurability();
					int mdurBody=matBody.getMaxDurability();
					int mdurLegs=matLegs.getMaxDurability();
					int mdurFeet=matFeet.getMaxDurability();
					durHead=mdurHead-durHead;
					durBody=mdurBody-durBody;
					durLegs=mdurLegs-durLegs;
					durFeet=mdurFeet-durFeet;
					int durtot=Math.max(durHead,0)+Math.max(durBody,0)+Math.max(durLegs,0)+Math.max(durFeet,0);
					int mdurtot=Math.max(mdurHead,0)+Math.max(mdurBody,0)+Math.max(mdurLegs,0)+Math.max(mdurFeet,0);
					int prcHead=0;
					int prcBody=0;
					int prcLegs=0;
					int prcFeet=0;
					int prctot=0;
					String colorcodeh="§4";
					String colorcodeb="§4";
					String colorcodel="§4";
					String colorcodef="§4";
					String colorcodet="§4";
					if(mdurHead>0){prcHead=(durHead*100)/mdurHead;}
					if(mdurBody>0){prcBody=(durBody*100)/mdurBody;}
					if(mdurLegs>0){prcLegs=(durLegs*100)/mdurLegs;}
					if(mdurFeet>0){prcFeet=(durFeet*100)/mdurFeet;}
					if(mdurtot>0){prctot=(durtot*100)/mdurtot;}
					if(prcHead>66){colorcodeh="§a";}
					if(prcHead<=66 && prcHead>33){colorcodeh="§e";}
					if(prcHead<=33){colorcodeh="§4";}
					if(prcBody>66){colorcodeb="§a";}
					if(prcBody<=66 && prcBody>33){colorcodeb="§e";}
					if(prcBody<=33){colorcodeb="§4";}
					if(prcLegs>66){colorcodel="§a";}
					if(prcLegs<=66 && prcLegs>33){colorcodel="§e";}
					if(prcLegs<=33){colorcodel="§4";}
					if(prcFeet>66){colorcodef="§a";}
					if(prcFeet<=66 && prcFeet>33){colorcodef="§e";}
					if(prcFeet<=33){colorcodef="§4";}
					if(prctot>66){colorcodet="§a";}
					if(prctot<=66 && prctot>33){colorcodet="§e";}
					if(prctot<=33){colorcodet="§4";}
					String durHeadt=Integer.toString(durHead);
					String durBodyt=Integer.toString(durBody);
					String durLegst=Integer.toString(durLegs);
					String durFeett=Integer.toString(durFeet);
					String mdurHeadt=Integer.toString(mdurHead);
					String mdurBodyt=Integer.toString(mdurBody);
					String mdurLegst=Integer.toString(mdurLegs);
					String mdurFeett=Integer.toString(mdurFeet);
					String prcHeadt=Integer.toString(prcHead);
					String prcBodyt=Integer.toString(prcBody);
					String prcLegst=Integer.toString(prcLegs);
					String prcFeett=Integer.toString(prcFeet);
					String durtott=Integer.toString(durtot);
					String mdurtott=Integer.toString(mdurtot);
					String prctott=Integer.toString(prctot);
					player.sendMessage("Head ("+descHead+"): "+colorcodeh+durHeadt+"/"+mdurHeadt+" - "+prcHeadt+"%");
					player.sendMessage("Body ("+descBody+"): "+colorcodeb+durBodyt+"/"+mdurBodyt+" - "+prcBodyt+"%");
					player.sendMessage("Legs ("+descLegs+"): "+colorcodel+durLegst+"/"+mdurLegst+" - "+prcLegst+"%");
					player.sendMessage("Feet ("+descFeet+"): "+colorcodef+durFeett+"/"+mdurFeett+" - "+prcFeett+"%");
					player.sendMessage("Total: "+colorcodet+durtott+"/"+mdurtott+" - "+prctott+"%");
				return true;
				}
				else if(args[0].equalsIgnoreCase("help"))
				{
					player.sendMessage("/dur [mode] - Checks your item durability.");
					player.sendMessage("mode may be empty(Item in Hand) or a(Armor)");
					return true;
				}
			}
		}
		return false; 
	}
	
}
