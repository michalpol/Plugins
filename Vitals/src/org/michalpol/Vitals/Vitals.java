package org.michalpol.Vitals;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Michalpol
 * @Version 0.0.1
 */
public class Vitals extends JavaPlugin {
	public Logger log = Logger.getLogger("Minecraft");//logger
	private Map<String,String> colors= new HashMap<String,String>();
	public void onEnable(){ 
		log.info("[Vitals]Enabling Vitals...");
		colors.put("black", "§0");
		colors.put("dblue", "§1");
		colors.put("dgreen", "§2");
		colors.put("daqua", "§3");
		colors.put("dred", "§4");
		colors.put("purple", "§5");
		colors.put("gold", "§6");
		colors.put("gray", "§7");
		colors.put("dgray", "§8");
		colors.put("blue", "§9");
		colors.put("green", "§a");
		colors.put("aqua", "§b");
		colors.put("red", "§c");
		colors.put("pink", "§d");
		colors.put("yellow", "§e");
		colors.put("white", "§f");
	}
	 
	public void onDisable(){ 
		log.info("[Vitals]Disabling Vitals...");
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("vitals"))
		{
			
			if(args.length==0)//no mode selected
			{
				Player player=(Player) sender;
				int i = 0;
				
				i = 0;
				String HPmess=colors.get("green");
				if(player.getHealth()<=10){HPmess=colors.get("yellow");}
				if(player.getHealth()<=5){HPmess=colors.get("red");}
				while (i<Math.floor((player.getHealth())))
				{HPmess+="|";i++;}
				HPmess+=colors.get("black");
				while (i<20)
				{HPmess+="|";i++;}
				
				i = 0;
				String HGmess=colors.get("gold");
				while (i<Math.floor((player.getFoodLevel())))
				{HGmess+="|";i++;}
				HGmess+=colors.get("black");
				while (i<20)
				{HGmess+="|";i++;}
				
				i = 0;
				String XPmess=colors.get("yellow");
				while (i<Math.floor((player.getExperience()/5)))
				{XPmess+="|";i++;}
				XPmess+=colors.get("black");
				while (i<20)
				{XPmess+="|";i++;}
				
				String ArmorMess="";
				if(player.getInventory().getHelmet().getType()==Material.DIAMOND_HELMET){ArmorMess+=colors.get("aqua")+"H";} else
					if(player.getInventory().getHelmet().getType()==Material.IRON_HELMET){ArmorMess+=colors.get("gray")+"H";} else
						if(player.getInventory().getHelmet().getType()==Material.GOLD_HELMET){ArmorMess+=colors.get("yellow")+"H";} else
							if(player.getInventory().getHelmet().getType()==Material.LEATHER_HELMET){ArmorMess+=colors.get("gold")+"H";} else
								if(player.getInventory().getHelmet().getType()==Material.CHAINMAIL_HELMET){ArmorMess+=colors.get("dgray")+"H";} else {ArmorMess+=colors.get("black")+"H";}
				
				if(player.getInventory().getChestplate().getType()==Material.DIAMOND_CHESTPLATE){ArmorMess+=colors.get("aqua")+"C";} else
					if(player.getInventory().getChestplate().getType()==Material.IRON_CHESTPLATE){ArmorMess+=colors.get("gray")+"C";} else
						if(player.getInventory().getChestplate().getType()==Material.GOLD_CHESTPLATE){ArmorMess+=colors.get("yellow")+"C";} else
							if(player.getInventory().getChestplate().getType()==Material.LEATHER_CHESTPLATE){ArmorMess+=colors.get("gold")+"C";} else
								if(player.getInventory().getChestplate().getType()==Material.CHAINMAIL_CHESTPLATE){ArmorMess+=colors.get("dgray")+"C";} else {ArmorMess+=colors.get("black")+"C";}
				
				if(player.getInventory().getLeggings().getType()==Material.DIAMOND_LEGGINGS){ArmorMess+=colors.get("aqua")+"L";} else
					if(player.getInventory().getLeggings().getType()==Material.IRON_LEGGINGS){ArmorMess+=colors.get("gray")+"L";} else
						if(player.getInventory().getLeggings().getType()==Material.GOLD_LEGGINGS){ArmorMess+=colors.get("yellow")+"L";} else
							if(player.getInventory().getLeggings().getType()==Material.LEATHER_LEGGINGS){ArmorMess+=colors.get("gold")+"L";} else
								if(player.getInventory().getLeggings().getType()==Material.CHAINMAIL_LEGGINGS){ArmorMess+=colors.get("dgray")+"L";} else {ArmorMess+=colors.get("black")+"L";}
				
				if(player.getInventory().getBoots().getType()==Material.DIAMOND_BOOTS){ArmorMess+=colors.get("aqua")+"B";} else
					if(player.getInventory().getBoots().getType()==Material.IRON_BOOTS){ArmorMess+=colors.get("gray")+"B";} else
						if(player.getInventory().getBoots().getType()==Material.GOLD_BOOTS){ArmorMess+=colors.get("yellow")+"B";} else
							if(player.getInventory().getBoots().getType()==Material.LEATHER_BOOTS){ArmorMess+=colors.get("gold")+"B";} else
								if(player.getInventory().getBoots().getType()==Material.CHAINMAIL_BOOTS){ArmorMess+=colors.get("dgray")+"B";} else {ArmorMess+=colors.get("black")+"B";}
				String WeaponsMess="";
				if(player.getInventory().contains(Material.DIAMOND_SWORD)){WeaponsMess+=colors.get("aqua")+"S";}else
					if(player.getInventory().contains(Material.IRON_SWORD)){WeaponsMess+=colors.get("gray")+"S";} else
						if(player.getInventory().contains(Material.GOLD_SWORD)){WeaponsMess+=colors.get("yellow")+"S";} else
							if(player.getInventory().contains(Material.STONE_SWORD)){WeaponsMess+=colors.get("dgray")+"S";} else
								if(player.getInventory().contains(Material.WOOD_SWORD)){WeaponsMess+=colors.get("gold")+"S";} else{WeaponsMess+=colors.get("black")+"S";}
				if(player.getInventory().contains(Material.BOW)){WeaponsMess+=colors.get("gold")+"B";} else {WeaponsMess+=colors.get("black")+"B";}
				if(player.getInventory().contains(Material.ARROW)){WeaponsMess+=colors.get("white")+"A"+FormatArrows(getItemCount(player.getInventory().all(Material.ARROW)));}
				else{WeaponsMess+=colors.get("black")+"A000";}
				
				String FoodMess=colors.get("white")+FormatFood(countFood(player.getInventory()))+colors.get("red")+"h";
				
				player.sendMessage("Vitals ------------------------------------------------");
				player.sendMessage("Player:            " +colors.get("green")+player.getName());
				player.sendMessage("Level:              " +colors.get("green")+ Integer.toString(player.getLevel()));
				player.sendMessage("Health (HP):       " +HPmess);
				player.sendMessage("Hunger (HG):      " +HGmess);
				player.sendMessage("Experience (XP): " +XPmess+" "+colors.get("yellow")+Integer.toString((int)Math.floor((player.getExperience())))+"%");
				player.sendMessage("Armor:              " +ArmorMess);
				player.sendMessage("Weapons:           " +WeaponsMess);
				player.sendMessage("Food:                " +FoodMess);
				player.sendMessage("-----------------------------------------------------" );
				return true; 
			}
			else if(args[0].equalsIgnoreCase("all"))
			{
				Player player=(Player) sender;
				Player[] Online = getServer().getOnlinePlayers();
				player.sendMessage("All Players -------------------------------------------");
				int j=0;
				while (j < Array.getLength(Online))
				{
					player.sendMessage(FormattedPlayerInfoBar(Online[j],player));
					j++;
				}
				player.sendMessage("-----------------------------------------------------" );
				return true; 
			}
			else
			{
				Player player=(Player) getServer().getPlayer(args[0]);
				if(player==null){player=(Player) sender;player.sendMessage(colors.get("dred")+"That player is not online!"); return true;}
				int i = 0;
				
				i = 0;
				String HPmess=colors.get("green");
				if(player.getHealth()<=10){HPmess=colors.get("yellow");}
				if(player.getHealth()<=5){HPmess=colors.get("red");}
				while (i<Math.floor((player.getHealth())))
				{HPmess+="|";i++;}
				HPmess+=colors.get("black");
				while (i<20)
				{HPmess+="|";i++;}
				
				i = 0;
				String HGmess=colors.get("gold");
				while (i<Math.floor((player.getFoodLevel())))
				{HGmess+="|";i++;}
				HGmess+=colors.get("black");
				while (i<20)
				{HGmess+="|";i++;}
				
				i = 0;
				String XPmess=colors.get("yellow");
				while (i<Math.floor((player.getExperience()/5)))
				{XPmess+="|";i++;}
				XPmess+=colors.get("black");
				while (i<20)
				{XPmess+="|";i++;}
				
				String ArmorMess="";
				if(player.getInventory().getHelmet().getType()==Material.DIAMOND_HELMET){ArmorMess+=colors.get("aqua")+"H";} else
					if(player.getInventory().getHelmet().getType()==Material.IRON_HELMET){ArmorMess+=colors.get("gray")+"H";} else
						if(player.getInventory().getHelmet().getType()==Material.GOLD_HELMET){ArmorMess+=colors.get("yellow")+"H";} else
							if(player.getInventory().getHelmet().getType()==Material.LEATHER_HELMET){ArmorMess+=colors.get("gold")+"H";} else
								if(player.getInventory().getHelmet().getType()==Material.CHAINMAIL_HELMET){ArmorMess+=colors.get("dgray")+"H";} else {ArmorMess+=colors.get("black")+"H";}
				
				if(player.getInventory().getChestplate().getType()==Material.DIAMOND_CHESTPLATE){ArmorMess+=colors.get("aqua")+"C";} else
					if(player.getInventory().getChestplate().getType()==Material.IRON_CHESTPLATE){ArmorMess+=colors.get("gray")+"C";} else
						if(player.getInventory().getChestplate().getType()==Material.GOLD_CHESTPLATE){ArmorMess+=colors.get("yellow")+"C";} else
							if(player.getInventory().getChestplate().getType()==Material.LEATHER_CHESTPLATE){ArmorMess+=colors.get("gold")+"C";} else
								if(player.getInventory().getChestplate().getType()==Material.CHAINMAIL_CHESTPLATE){ArmorMess+=colors.get("dgray")+"C";} else {ArmorMess+=colors.get("black")+"C";}
				
				if(player.getInventory().getLeggings().getType()==Material.DIAMOND_LEGGINGS){ArmorMess+=colors.get("aqua")+"L";} else
					if(player.getInventory().getLeggings().getType()==Material.IRON_LEGGINGS){ArmorMess+=colors.get("gray")+"L";} else
						if(player.getInventory().getLeggings().getType()==Material.GOLD_LEGGINGS){ArmorMess+=colors.get("yellow")+"L";} else
							if(player.getInventory().getLeggings().getType()==Material.LEATHER_LEGGINGS){ArmorMess+=colors.get("gold")+"L";} else
								if(player.getInventory().getLeggings().getType()==Material.CHAINMAIL_LEGGINGS){ArmorMess+=colors.get("dgray")+"L";} else {ArmorMess+=colors.get("black")+"L";}
				
				if(player.getInventory().getBoots().getType()==Material.DIAMOND_BOOTS){ArmorMess+=colors.get("aqua")+"B";} else
					if(player.getInventory().getBoots().getType()==Material.IRON_BOOTS){ArmorMess+=colors.get("gray")+"B";} else
						if(player.getInventory().getBoots().getType()==Material.GOLD_BOOTS){ArmorMess+=colors.get("yellow")+"B";} else
							if(player.getInventory().getBoots().getType()==Material.LEATHER_BOOTS){ArmorMess+=colors.get("gold")+"B";} else
								if(player.getInventory().getBoots().getType()==Material.CHAINMAIL_BOOTS){ArmorMess+=colors.get("dgray")+"B";} else {ArmorMess+=colors.get("black")+"B";}
				String WeaponsMess="";
				if(player.getInventory().contains(Material.DIAMOND_SWORD)){WeaponsMess+=colors.get("aqua")+"S";}else
					if(player.getInventory().contains(Material.IRON_SWORD)){WeaponsMess+=colors.get("gray")+"S";} else
						if(player.getInventory().contains(Material.GOLD_SWORD)){WeaponsMess+=colors.get("yellow")+"S";} else
							if(player.getInventory().contains(Material.STONE_SWORD)){WeaponsMess+=colors.get("dgray")+"S";} else
								if(player.getInventory().contains(Material.WOOD_SWORD)){WeaponsMess+=colors.get("gold")+"S";} else{WeaponsMess+=colors.get("black")+"S";}
				if(player.getInventory().contains(Material.BOW)){WeaponsMess+=colors.get("gold")+"B";} else {WeaponsMess+=colors.get("black")+"B";}
				if(player.getInventory().contains(Material.ARROW)){WeaponsMess+=colors.get("white")+"A"+FormatArrows(getItemCount(player.getInventory().all(Material.ARROW)));}
				else{WeaponsMess+=colors.get("black")+"A000";}
				
				String FoodMess=colors.get("white")+FormatFood(countFood(player.getInventory()))+colors.get("red")+"h";
				
				player.sendMessage("Vitals ------------------------------------------------");
				player.sendMessage("Player:            " +colors.get("green")+player.getName());
				player.sendMessage("Level:              " +colors.get("green")+ Integer.toString(player.getLevel()));
				player.sendMessage("Health (HP):       " +HPmess);
				player.sendMessage("Hunger (HG):      " +HGmess);
				player.sendMessage("Experience (XP): " +XPmess+" "+colors.get("yellow")+Integer.toString((int)Math.floor((player.getExperience())))+"%");
				player.sendMessage("Armor:              " +ArmorMess);
				player.sendMessage("Weapons:           " +WeaponsMess);
				player.sendMessage("Food:                " +FoodMess);
				player.sendMessage("-----------------------------------------------------" );
				return true;
			}
			
		}
		return false; 
	}
	  private int getItemCount(HashMap<Integer, ? extends ItemStack> all)
	  {
	    int count = 0;

	    for (ItemStack is : all.values())
	    {
	      count += is.getAmount();
	    }

	    return count;
	  }
	  private int countFood(PlayerInventory inv)
	  {
	    int count = 0;
	    //count foods by foods
	    count+=getItemCount(inv.all(Material.BREAD))*5;
	    count+=getItemCount(inv.all(Material.CAKE))*12;
	    count+=getItemCount(inv.all(Material.COOKIE))*1;
	    count+=getItemCount(inv.all(Material.MELON))*2;
	    count+=getItemCount(inv.all(Material.MUSHROOM_SOUP))*8;
	    count+=getItemCount(inv.all(Material.RAW_CHICKEN))*2;
	    count+=getItemCount(inv.all(Material.COOKED_CHICKEN))*6;
	    count+=getItemCount(inv.all(Material.RAW_BEEF))*3;
	    count+=getItemCount(inv.all(Material.COOKED_BEEF))*8;
	    count+=getItemCount(inv.all(Material.PORK))*3;
	    count+=getItemCount(inv.all(Material.GRILLED_PORK))*8;
	    count+=getItemCount(inv.all(Material.RAW_FISH))*2;
	    count+=getItemCount(inv.all(Material.COOKED_FISH))*5;
	    count+=getItemCount(inv.all(Material.APPLE))*4;
	    count+=getItemCount(inv.all(Material.GOLDEN_APPLE))*4;
	    count+=getItemCount(inv.all(Material.ROTTEN_FLESH))*4;
	    count+=getItemCount(inv.all(Material.SPIDER_EYE))*2;
	    return count;
	  }
	  private String FormatArrows(int a)
	  {
		  String str;
	    if(a<10){str="00"+Integer.toString(a);}else if(a<100){str="0"+Integer.toString(a);}else{str=Integer.toString(a);}
	    return str;
	  }
	  private String FormatFood(int a)
	  {
		  String str;
	    if(a<10){str="000"+Integer.toString(a);}else if(a<100){str="00"+Integer.toString(a);}else if(a<1000){str="0"+Integer.toString(a);}else{str=Integer.toString(a);}
	    return str;
	  }
	  @SuppressWarnings("deprecation")
	private String FormattedPlayerInfoBar(Player player,Player me)
	  {
		  //Name   LVL   HP   HG   XP
		  String Namestr="", HPstr = "",LVLstr="", HGstr = "", XPstr = "";
		  if(me.getName().equalsIgnoreCase(player.getName())){Namestr=colors.get("gold")+player.getName();}else{Namestr=player.getName();}
		  if(player.getHealth()<=5){HPstr=colors.get("red")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}else if(player.getHealth()<=10){HPstr=colors.get("yellow")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}else{HPstr=colors.get("green")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}
		  HGstr=colors.get("gold")+"HG: "+Integer.toString((player.getFoodLevel()*5))+"%";
		  int HHTM=player.getLevel()-me.getLevel();
		  if(HHTM<=3 || HHTM>=-3){LVLstr=colors.get("yellow")+"LV: "+Integer.toString(player.getLevel());}else if(HHTM<-3){LVLstr=colors.get("green")+"LV: "+Integer.toString(player.getLevel());} else {LVLstr=colors.get("red")+"LV: "+Integer.toString(player.getLevel());}
		  XPstr=colors.get("yellow")+"XP: "+Integer.toString(player.getExperience())+"%";
		  return Namestr+"   "+LVLstr+"   "+HPstr+"   "+HGstr+"   "+XPstr;
	  }
}
