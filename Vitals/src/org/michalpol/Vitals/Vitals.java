package org.michalpol.Vitals;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

/**
 * @author Michalpol
 * @Version 0.1.0
 */
public class Vitals extends JavaPlugin implements Listener{
	public static int CONST_AUTOSAVE_SECONDS = 300;
	public Logger log = Logger.getLogger("Minecraft");//logger
			private Map<String,String> colors= new HashMap<String,String>();
			private static Metrics metrics;
			
			
			static String mainDirectory = "plugins/Vitals"; 
			static File Config = new File(mainDirectory + File.separator + "config.properties");
			static Properties prop = new Properties();
			
			public String HOST;
			public String USER;
			public String PWD;
			public static String DB;
			
			
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
		log.info("[Vitals]Looking for config file...");
		new File(mainDirectory).mkdir();
		if(!Config.exists()){ 
			log.log(Level.WARNING,"[Vitals]Config file not found!");
			log.log(Level.WARNING,"[Vitals]Plugin will disable itself!");
			this.setEnabled(false);
			}
		else { 
			loadProcedure();
		}
		log.info("[Vitals]Configuration loaded...");
		log.info("[Vitals]Initializing MySQL...");
		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		mysql.initialize();
		try{
			if(!mysql.checkConnection()){mysql.writeError("MySQL connection problem occured!", true);}
			if(mysql.checkTable("vitals")){mysql.writeInfo("Table present...");}
			else{mysql.writeError("Table absent, creating it...", false);
			if(mysql.createTable("CREATE TABLE `"+DB+"`.`vitals` ( " +
					"`id` INT( 11 ) NOT NULL AUTO_INCREMENT PRIMARY KEY ," +
					"`playername` VARCHAR( 255 ) NOT NULL , " +
					"`HP` INT( 2 ) NOT NULL , " +
					"`Level` INT( 4 ) NOT NULL , " +
					"`TotalXP` INT( 11 ) NOT NULL)"))
			{
			mysql.writeInfo("Table created.");
			}
			else
			{
				mysql.writeInfo("Could not make table.");
			}
			}
			mysql.close();
		}catch(Exception e){e.printStackTrace();}
		log.info("[Vitals]MySQL initialized!");
		log.info("[Vitals]Enabling PluginMetrics...");
		try {
		    metrics = new Metrics();

		    // Plot the total amount of protections
		    metrics.addCustomData(this, new Metrics.Plotter("Total Health") {
		        @Override
		        public int getValue() {
		        	int val = 0;
		    		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		    		mysql.initialize();
		    		try {
		    				ResultSet r=mysql.sqlQuery("SELECT SUM(HP) FROM `vitals`");
		    				if(r==null || !r.next()){return 0;}
		    				r.absolute(1);
		    				val=r.getInt(1);
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			return 0;
		    		}
		    		mysql.close();
		    		
		    		return val;
		        }

		    });
		    metrics.addCustomData(this, new Metrics.Plotter("Total Level") {
		        @Override
		        public int getValue() {
		        	int val = 0;
		    		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		    		mysql.initialize();
		    		try {
		    				ResultSet r=mysql.sqlQuery("SELECT SUM(Level) FROM `vitals`");
		    				if(r==null || !r.next()){return 0;}
		    				r.absolute(1);
		    				val=r.getInt(1);
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			return 0;
		    		}
		    		mysql.close();
		    		
		    		return val;
		        }

		    });
		    metrics.addCustomData(this, new Metrics.Plotter("Total Expereince") {
		        @Override
		        public int getValue() {
		        	int val = 0;
		    		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		    		mysql.initialize();
		    		try {
		    				ResultSet r=mysql.sqlQuery("SELECT SUM(TotalXP) FROM `vitals`");
		    				if(r==null || !r.next()){return 0;}
		    				r.absolute(1);
		    				val=r.getInt(1);
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			return 0;
		    		}
		    		mysql.close();
		    		
		    		return val;
		        }

		    });
		    metrics.beginMeasuringPlugin(this);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		log.info("[Vitals]Registering events...");
		this.getServer().getPluginManager().registerEvents(this, this);
		log.info("[Vitals]Registering repeating tasks...");
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
				{
					public void run()
					{
						Player[] playersonline = Bukkit.getServer().getOnlinePlayers();
						for(Player player : playersonline)
						{
							SavePlayer(player.getName(),player.getHealth(),player.getLevel(),player.getTotalExperience());
						}
					}
				}, 0, 20*CONST_AUTOSAVE_SECONDS);
		log.info("[Vitals]Vitals Enabled!");
	}
	protected boolean LoadPlayer(String player) {
		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		mysql.initialize();
		try {
				ResultSet r=mysql.sqlQuery("SELECT * FROM `vitals` WHERE playername='"+player+"'");
				if(r==null || !r.next()){return false;}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		mysql.close();
		return true;
	}
	protected boolean SavePlayer(String player,int HP, int Level, int XP) {
		boolean ok = LoadPlayer(player);
		mysqlCore mysql = new mysqlCore(log,"[Vitals]",HOST,DB,USER,PWD);
		mysql.initialize();
		if(ok)
		{
		try {
				mysql.updateQuery("UPDATE `vitals` SET HP='"+HP+"', Level='"+Level+"', TotalXP='"+XP+"' WHERE playername='"+player+"'");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		}
		else
		{
			try {
				mysql.insertQuery("INSERT INTO `vitals` VALUES(null,'"+player+"','"+HP+"','"+Level+"','"+XP+"');");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		}
		mysql.close();
		return true;
	}
	
	
	public void onDisable(){ 
		log.info("[Vitals]Disabling Vitals...");
		log.info("[Vitals]Vitals Disabled!");
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
				String ToolsMess="";
				//Pickaxe
				if(player.getInventory().contains(Material.DIAMOND_PICKAXE)){ToolsMess+=colors.get("aqua")+"P";} else
					if(player.getInventory().contains(Material.IRON_PICKAXE)){ToolsMess+=colors.get("gray")+"P";} else
						if(player.getInventory().contains(Material.GOLD_PICKAXE)){ToolsMess+=colors.get("yellow")+"P";} else
							if(player.getInventory().contains(Material.WOOD_PICKAXE)){ToolsMess+=colors.get("gold")+"P";} else
								if(player.getInventory().contains(Material.STONE_PICKAXE)){ToolsMess+=colors.get("dgray")+"P";} else {ToolsMess+=colors.get("black")+"P";}
				
				//Axe
				if(player.getInventory().contains(Material.DIAMOND_AXE)){ToolsMess+=colors.get("aqua")+"A";} else
					if(player.getInventory().contains(Material.IRON_AXE)){ToolsMess+=colors.get("gray")+"A";} else
						if(player.getInventory().contains(Material.GOLD_AXE)){ToolsMess+=colors.get("yellow")+"A";} else
							if(player.getInventory().contains(Material.WOOD_AXE)){ToolsMess+=colors.get("gold")+"A";} else
								if(player.getInventory().contains(Material.STONE_AXE)){ToolsMess+=colors.get("dgray")+"A";} else {ToolsMess+=colors.get("black")+"A";}
				
				//Shovel
				if(player.getInventory().contains(Material.DIAMOND_SPADE)){ToolsMess+=colors.get("aqua")+"S";} else
					if(player.getInventory().contains(Material.IRON_SPADE)){ToolsMess+=colors.get("gray")+"S";} else
						if(player.getInventory().contains(Material.GOLD_SPADE)){ToolsMess+=colors.get("yellow")+"S";} else
							if(player.getInventory().contains(Material.WOOD_SPADE)){ToolsMess+=colors.get("gold")+"S";} else
								if(player.getInventory().contains(Material.STONE_SPADE)){ToolsMess+=colors.get("dgray")+"S";} else {ToolsMess+=colors.get("black")+"S";}
				
				//Hoe
				if(player.getInventory().contains(Material.DIAMOND_HOE)){ToolsMess+=colors.get("aqua")+"H";} else
					if(player.getInventory().contains(Material.IRON_HOE)){ToolsMess+=colors.get("gray")+"H";} else
						if(player.getInventory().contains(Material.GOLD_HOE)){ToolsMess+=colors.get("yellow")+"H";} else
							if(player.getInventory().contains(Material.WOOD_HOE)){ToolsMess+=colors.get("gold")+"H";} else
								if(player.getInventory().contains(Material.STONE_HOE)){ToolsMess+=colors.get("dgray")+"H";} else {ToolsMess+=colors.get("black")+"H";}
				
				//Flint&Steel
				if(player.getInventory().contains(Material.FLINT_AND_STEEL)){ToolsMess+=colors.get("green")+"F";} else {ToolsMess+=colors.get("black")+"F";}
				
				//Fishing Rod
				if(player.getInventory().contains(Material.FISHING_ROD)){ToolsMess+=colors.get("green")+"R";} else {ToolsMess+=colors.get("black")+"R";}				
				
				//Shears
				if(player.getInventory().contains(Material.SHEARS)){ToolsMess+=colors.get("green")+"E";} else {ToolsMess+=colors.get("black")+"E";}				
				
				//Bucket
				if(player.getInventory().contains(Material.LAVA_BUCKET)){ToolsMess+=colors.get("gold")+"B";} else
					if(player.getInventory().contains(Material.WATER_BUCKET)){ToolsMess+=colors.get("blue")+"B";} else
						if(player.getInventory().contains(Material.MILK_BUCKET)){ToolsMess+=colors.get("white")+"B";} else 
							if(player.getInventory().contains(Material.BUCKET)){ToolsMess+=colors.get("gray")+"B";} else {ToolsMess+=colors.get("black")+"B";}
				String SlotsMess=FormattedItemsSlotsString(player.getInventory());
				player.sendMessage("Vitals ------------------------------------------------");
				player.sendMessage("Player:            " +colors.get("green")+player.getName());
				player.sendMessage("Level:              " +colors.get("green")+ Integer.toString(player.getLevel()));
				player.sendMessage("Health (HP):       " +HPmess);
				player.sendMessage("Hunger (HG):      " +HGmess);
				player.sendMessage("Experience (XP): " +XPmess+" "+colors.get("yellow")+Integer.toString((int)Math.floor((player.getExperience())))+"%");
				player.sendMessage("Armor:              " +ArmorMess);
				player.sendMessage("Weapons:           " +WeaponsMess);
				player.sendMessage("Food:                " +FoodMess);
				player.sendMessage("Tools:               " +ToolsMess);
				player.sendMessage("Free Slots (FS):  " +colors.get("green")+SlotsMess);
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
			else if(args[0].equalsIgnoreCase("p"))
			{
				Player p=Bukkit.getServer().getPlayer(args[1]);
				Player player =(Player) sender;
				if(p==null){p=(Player) sender;player.sendMessage(colors.get("dred")+"That player is not online!"); return true;}
				int i = 0;
				
				i = 0;
				String HPmess=colors.get("green");
				if(p.getHealth()<=10){HPmess=colors.get("yellow");}
				if(p.getHealth()<=5){HPmess=colors.get("red");}
				while (i<Math.floor((p.getHealth())))
				{HPmess+="|";i++;}
				HPmess+=colors.get("black");
				while (i<20)
				{HPmess+="|";i++;}
				
				i = 0;
				String HGmess=colors.get("gold");
				while (i<Math.floor((p.getFoodLevel())))
				{HGmess+="|";i++;}
				HGmess+=colors.get("black");
				while (i<20)
				{HGmess+="|";i++;}
				
				i = 0;
				String XPmess=colors.get("yellow");
				while (i<Math.floor((p.getExperience()/5)))
				{XPmess+="|";i++;}
				XPmess+=colors.get("black");
				while (i<20)
				{XPmess+="|";i++;}
				
				String ArmorMess="";
				if(p.getInventory().getHelmet().getType()==Material.DIAMOND_HELMET){ArmorMess+=colors.get("aqua")+"H";} else
					if(p.getInventory().getHelmet().getType()==Material.IRON_HELMET){ArmorMess+=colors.get("gray")+"H";} else
						if(p.getInventory().getHelmet().getType()==Material.GOLD_HELMET){ArmorMess+=colors.get("yellow")+"H";} else
							if(p.getInventory().getHelmet().getType()==Material.LEATHER_HELMET){ArmorMess+=colors.get("gold")+"H";} else
								if(p.getInventory().getHelmet().getType()==Material.CHAINMAIL_HELMET){ArmorMess+=colors.get("dgray")+"H";} else {ArmorMess+=colors.get("black")+"H";}
				
				if(p.getInventory().getChestplate().getType()==Material.DIAMOND_CHESTPLATE){ArmorMess+=colors.get("aqua")+"C";} else
					if(p.getInventory().getChestplate().getType()==Material.IRON_CHESTPLATE){ArmorMess+=colors.get("gray")+"C";} else
						if(p.getInventory().getChestplate().getType()==Material.GOLD_CHESTPLATE){ArmorMess+=colors.get("yellow")+"C";} else
							if(p.getInventory().getChestplate().getType()==Material.LEATHER_CHESTPLATE){ArmorMess+=colors.get("gold")+"C";} else
								if(p.getInventory().getChestplate().getType()==Material.CHAINMAIL_CHESTPLATE){ArmorMess+=colors.get("dgray")+"C";} else {ArmorMess+=colors.get("black")+"C";}
				
				if(p.getInventory().getLeggings().getType()==Material.DIAMOND_LEGGINGS){ArmorMess+=colors.get("aqua")+"L";} else
					if(p.getInventory().getLeggings().getType()==Material.IRON_LEGGINGS){ArmorMess+=colors.get("gray")+"L";} else
						if(p.getInventory().getLeggings().getType()==Material.GOLD_LEGGINGS){ArmorMess+=colors.get("yellow")+"L";} else
							if(p.getInventory().getLeggings().getType()==Material.LEATHER_LEGGINGS){ArmorMess+=colors.get("gold")+"L";} else
								if(p.getInventory().getLeggings().getType()==Material.CHAINMAIL_LEGGINGS){ArmorMess+=colors.get("dgray")+"L";} else {ArmorMess+=colors.get("black")+"L";}
				
				if(p.getInventory().getBoots().getType()==Material.DIAMOND_BOOTS){ArmorMess+=colors.get("aqua")+"B";} else
					if(p.getInventory().getBoots().getType()==Material.IRON_BOOTS){ArmorMess+=colors.get("gray")+"B";} else
						if(p.getInventory().getBoots().getType()==Material.GOLD_BOOTS){ArmorMess+=colors.get("yellow")+"B";} else
							if(p.getInventory().getBoots().getType()==Material.LEATHER_BOOTS){ArmorMess+=colors.get("gold")+"B";} else
								if(p.getInventory().getBoots().getType()==Material.CHAINMAIL_BOOTS){ArmorMess+=colors.get("dgray")+"B";} else {ArmorMess+=colors.get("black")+"B";}
				String WeaponsMess="";
				if(p.getInventory().contains(Material.DIAMOND_SWORD)){WeaponsMess+=colors.get("aqua")+"S";}else
					if(p.getInventory().contains(Material.IRON_SWORD)){WeaponsMess+=colors.get("gray")+"S";} else
						if(p.getInventory().contains(Material.GOLD_SWORD)){WeaponsMess+=colors.get("yellow")+"S";} else
							if(p.getInventory().contains(Material.STONE_SWORD)){WeaponsMess+=colors.get("dgray")+"S";} else
								if(p.getInventory().contains(Material.WOOD_SWORD)){WeaponsMess+=colors.get("gold")+"S";} else{WeaponsMess+=colors.get("black")+"S";}
				if(p.getInventory().contains(Material.BOW)){WeaponsMess+=colors.get("gold")+"B";} else {WeaponsMess+=colors.get("black")+"B";}
				if(p.getInventory().contains(Material.ARROW)){WeaponsMess+=colors.get("white")+"A"+FormatArrows(getItemCount(p.getInventory().all(Material.ARROW)));}
				else{WeaponsMess+=colors.get("black")+"A000";}
				
				String FoodMess=colors.get("white")+FormatFood(countFood(p.getInventory()))+colors.get("red")+"h";
				
				String ToolsMess="";
				//Pickaxe
				if(p.getInventory().contains(Material.DIAMOND_PICKAXE)){ToolsMess+=colors.get("aqua")+"P";} else
					if(p.getInventory().contains(Material.IRON_PICKAXE)){ToolsMess+=colors.get("gray")+"P";} else
						if(p.getInventory().contains(Material.GOLD_PICKAXE)){ToolsMess+=colors.get("yellow")+"P";} else
							if(p.getInventory().contains(Material.WOOD_PICKAXE)){ToolsMess+=colors.get("gold")+"P";} else
								if(p.getInventory().contains(Material.STONE_PICKAXE)){ToolsMess+=colors.get("dgray")+"P";} else {ToolsMess+=colors.get("black")+"P";}
				
				//Axe
				if(p.getInventory().contains(Material.DIAMOND_AXE)){ToolsMess+=colors.get("aqua")+"A";} else
					if(p.getInventory().contains(Material.IRON_AXE)){ToolsMess+=colors.get("gray")+"A";} else
						if(p.getInventory().contains(Material.GOLD_AXE)){ToolsMess+=colors.get("yellow")+"A";} else
							if(p.getInventory().contains(Material.WOOD_AXE)){ToolsMess+=colors.get("gold")+"A";} else
								if(p.getInventory().contains(Material.STONE_AXE)){ToolsMess+=colors.get("dgray")+"A";} else {ToolsMess+=colors.get("black")+"A";}
				
				//Shovel
				if(p.getInventory().contains(Material.DIAMOND_SPADE)){ToolsMess+=colors.get("aqua")+"S";} else
					if(p.getInventory().contains(Material.IRON_SPADE)){ToolsMess+=colors.get("gray")+"S";} else
						if(p.getInventory().contains(Material.GOLD_SPADE)){ToolsMess+=colors.get("yellow")+"S";} else
							if(p.getInventory().contains(Material.WOOD_SPADE)){ToolsMess+=colors.get("gold")+"S";} else
								if(p.getInventory().contains(Material.STONE_SPADE)){ToolsMess+=colors.get("dgray")+"S";} else {ToolsMess+=colors.get("black")+"S";}
				
				//Hoe
				if(p.getInventory().contains(Material.DIAMOND_HOE)){ToolsMess+=colors.get("aqua")+"H";} else
					if(p.getInventory().contains(Material.IRON_HOE)){ToolsMess+=colors.get("gray")+"H";} else
						if(p.getInventory().contains(Material.GOLD_HOE)){ToolsMess+=colors.get("yellow")+"H";} else
							if(p.getInventory().contains(Material.WOOD_HOE)){ToolsMess+=colors.get("gold")+"H";} else
								if(p.getInventory().contains(Material.STONE_HOE)){ToolsMess+=colors.get("dgray")+"H";} else {ToolsMess+=colors.get("black")+"H";}
				
				//Flint&Steel
				if(p.getInventory().contains(Material.FLINT_AND_STEEL)){ToolsMess+=colors.get("green")+"F";} else {ToolsMess+=colors.get("black")+"F";}
				
				//Fishing Rod
				if(p.getInventory().contains(Material.FISHING_ROD)){ToolsMess+=colors.get("green")+"R";} else {ToolsMess+=colors.get("black")+"R";}				
				
				//Shears
				if(p.getInventory().contains(Material.SHEARS)){ToolsMess+=colors.get("green")+"E";} else {ToolsMess+=colors.get("black")+"E";}				
				
				//Bucket
				if(p.getInventory().contains(Material.LAVA_BUCKET)){ToolsMess+=colors.get("gold")+"B";} else
					if(p.getInventory().contains(Material.WATER_BUCKET)){ToolsMess+=colors.get("blue")+"B";} else
						if(p.getInventory().contains(Material.MILK_BUCKET)){ToolsMess+=colors.get("white")+"B";} else 
							if(p.getInventory().contains(Material.BUCKET)){ToolsMess+=colors.get("gray")+"B";} else {ToolsMess+=colors.get("black")+"B";}
				String SlotsMess=FormattedItemsSlotsString(p.getInventory());
				
				player.sendMessage("Vitals ------------------------------------------------");
				player.sendMessage("Player:            " +colors.get("green")+p.getName());
				player.sendMessage("Level:              " +colors.get("green")+ Integer.toString(p.getLevel()));
				player.sendMessage("Health (HP):       " +HPmess);
				player.sendMessage("Hunger (HG):      " +HGmess);
				player.sendMessage("Experience (XP): " +XPmess+" "+colors.get("yellow")+Integer.toString((int)Math.floor((p.getExperience())))+"%");
				player.sendMessage("Armor:              " +ArmorMess);
				player.sendMessage("Weapons:           " +WeaponsMess);
				player.sendMessage("Food:                " +FoodMess);
				player.sendMessage("Tools:               " +ToolsMess);
				player.sendMessage("Free Slots (FS):  " +colors.get("green")+SlotsMess);
				player.sendMessage("-----------------------------------------------------" );
				return true;
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				Player player =(Player) sender;
				player.sendMessage("Vitals Help -------------------------------------------");
				player.sendMessage("Modes: all - shows all players vitals, p playername - shows that player's vitals," );
				player.sendMessage("die - kills you if waiting for medic, revive playername - revives that player if he is wounded." );
				player.sendMessage("Weapons: S - Sword, B - Bow, Axxx - Arrow number." );
				player.sendMessage("Armor: H - Helmet, C - Chestplate, L - Leggings, B -Boots." );
				player.sendMessage("Tools: P - Pickaxe, A - Axe, S - Shovel, H - Hoe, F - Flint and Steel, R - Fishing Rod, E - Shears, B - Bucket." );
				player.sendMessage("Colors (Weapons/Tools): "+colors.get("aqua")+"diamond  "+colors.get("yellow")+"gold  "+colors.get("gray")+"iron  "+colors.get("dgray")+"stone  "+colors.get("gold")+"wood");
				player.sendMessage("Colors (Armor): "+colors.get("aqua")+"diamond  "+colors.get("yellow")+"gold  "+colors.get("gray")+"iron  "+colors.get("dgray")+"chain  "+colors.get("gold")+"leather");
				player.sendMessage("Colors (Bucket): "+colors.get("gold")+"lava  "+colors.get("blue")+"water  "+colors.get("white")+"milk  "+colors.get("gray")+"empty" );
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
		  //Name   LVL   HP   HG   XP   FS
		  String Namestr="", HPstr = "",LVLstr="", HGstr = "", XPstr = "";
		  if(me.getName().equalsIgnoreCase(player.getName())){Namestr=colors.get("gold")+player.getName();}else if (player.getHealth()==0){Namestr=colors.get("dred")+player.getName();}else{Namestr=player.getName();}
		  if(player.getHealth()<=5){HPstr=colors.get("red")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}else if(player.getHealth()<=10){HPstr=colors.get("yellow")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}else{HPstr=colors.get("green")+"HP: "+Integer.toString((player.getHealth()*5))+"%";}
		  HGstr=colors.get("gold")+"HG: "+Integer.toString((player.getFoodLevel()*5))+"%";
		  int HHTM=player.getLevel()-me.getLevel();
		  if(Math.abs(HHTM)<=3){LVLstr=colors.get("yellow")+"LV: "+Integer.toString(player.getLevel());}else if(HHTM<-3){LVLstr=colors.get("green")+"LV: "+Integer.toString(player.getLevel());} else {LVLstr=colors.get("red")+"LV: "+Integer.toString(player.getLevel());}
		  XPstr=colors.get("yellow")+"XP: "+Integer.toString(player.getExperience())+"%";
		  String FSstr=colors.get("green")+"FS: "+UnFormattedItemsSlotsString(player.getInventory());
		  return Namestr+"   "+LVLstr+"   "+HPstr+"   "+HGstr+"   "+XPstr+"   "+FSstr;
	  }
	  private String FormattedItemsSlotsString(PlayerInventory inv)
	  {
			ItemStack[] contents=inv.getContents();
			int count = 0;
			for (int i = 0; i < contents.length; i++) {
			    if (contents[i] == null || contents[i].getType()==Material.AIR)
			        count++;
			}
			return Integer.toString(count)+"/36";
	  }
	  private String UnFormattedItemsSlotsString(PlayerInventory inv)
	  {
			ItemStack[] contents=inv.getContents();
			int count = 0;
			for (int i = 0; i < contents.length; i++) {
			    if (contents[i] == null || contents[i].getType()==Material.AIR)
			        count++;
			}
			return Integer.toString(count);
	  }

	  private void loadProcedure() { 
			try{
			FileInputStream in = new FileInputStream(Config);
			prop.load(in);in.close();}catch(Exception e){e.printStackTrace();} 
			HOST = prop.getProperty("HOST");
			if(HOST==null)
			{
				log.log(Level.WARNING,"[Vitals]Config corrupted or node HOST not set properly!");
				log.log(Level.WARNING,"[Vitals]plugin will disable itself!");
				this.setEnabled(false);
			}
			USER = prop.getProperty("USER");
			if(USER==null)
			{
				log.log(Level.WARNING,"[Vitals]Config corrupted or node USER not set properly!");
				log.log(Level.WARNING,"[Vitals]plugin will disable itself!");
				this.setEnabled(false);
			}
			PWD = prop.getProperty("PWD");
			if(PWD==null)
			{
				log.log(Level.WARNING,"[Vitals]Config corrupted or node PWD not set properly!");
				log.log(Level.WARNING,"[Vitals]plugin will disable itself!");
				this.setEnabled(false);	
			}
			DB = prop.getProperty("DB");
			if(DB==null)
			{
				log.log(Level.WARNING,"[Vitals]Config corrupted or node DB not set properly!");
				log.log(Level.WARNING,"[Vitals]plugin will disable itself!");
				this.setEnabled(false);
			}
		}
	  
	  @EventHandler(priority=EventPriority.NORMAL)
	  void onPlayerJoin(PlayerJoinEvent event)
	  {
		  Player player=event.getPlayer();
		  String playername=player.getName();
		  SavePlayer(playername, player.getHealth(), player.getLevel(), player.getTotalExperience());
	  }
	  @EventHandler(priority=EventPriority.NORMAL)
	  void onPlayerQuit(PlayerQuitEvent event)
	  {
		  Player player=event.getPlayer();
		  String playername=player.getName();
		  SavePlayer(playername, player.getHealth(), player.getLevel(), player.getTotalExperience());
	  }
}
