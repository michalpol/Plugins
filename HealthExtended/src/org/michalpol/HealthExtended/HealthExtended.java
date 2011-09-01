package org.michalpol.HealthExtended;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

/**
 * Health Extended Plugin
 * @author michalpol
 * @version 0.0.2
 */

public class HealthExtended extends JavaPlugin
{
	public int BaseHealthExpPerLevel=100;
	public float HealthExpPerLevelProgressionRate=(float)1.05;
	public int HPMaxLvl=180;
	private Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/HealthExtended"; 
	static File Config = new File(mainDirectory + File.separator + "config.properties");
	static Properties prop = new Properties();
	public String mysqlhost="localhost";
	public String mysqluser="user";
	public String mysqlpassword="password";
	public String mysqldatabase="database";
	public String mysqltablename="healthextended";
	public HashMap<String,Integer> foods = new HashMap<String,Integer>();
	public String[] ignoreClickBlocks= new String[]{
			"DISPENSER",
		    "NOTE_BLOCK",
		    "BED_BLOCK",
		    "CHEST",
		    "WORKBENCH",
		    "FURNACE",
		    "BURNING_FURNACE",
		    "WOODEN_DOOR",
		    "LEVER",
		    "IRON_DOOR_BLOCK",
		    "STONE_BUTTON",
		    "JUKEBOX",
		    "LOCKED_CHEST",
		    "TRAP_DOOR"};
	public mysqlCore mysql = new mysqlCore(log,"[HealthExtended]",mysqlhost,mysqldatabase,mysqluser,mysqlpassword);
	private final HealthExtendedEntityListener entitylistener= new HealthExtendedEntityListener(this);
	private final HealthExtendedPlayerListener playerlistener= new HealthExtendedPlayerListener(this);
	public HashMap<String,Integer> PlayerHP = new HashMap<String,Integer>();
	public HashMap<String,Integer> PlayerLVL = new HashMap<String,Integer>();
	public HashMap<String,Integer> PlayerXP = new HashMap<String,Integer>();
	
	public void onEnable()
	{
		log.info("Enabling HealthExtended");
		log.info("Creating HealthExtended foods list");
		foods.put("APPLE", 4);
		foods.put("PORK", 3);
		foods.put("MUSHROOM_SOUP", 10);
		foods.put("GOLDEN_APPLE", 20);
		foods.put("GRILLED_PORK", 8);
		foods.put("BREAD", 5);
		foods.put("COOKIE",1);
		foods.put("RAW_FISH", 2);
		foods.put("COOKED_FISH", 5);
		foods.put("CAKE", 3);
		log.info("Created HealthExtended foods list");
		log.info("Registering HealthExtended Events");
		PluginManager pm=this.getServer().getPluginManager();
		new File(mainDirectory).mkdir();
		log.info("Checking HealthExtended Config files");
		if(!Config.exists()){ 
			log.log(Level.WARNING,"HealthExtended config not found!");
			log.log(Level.WARNING,"HealthExtended will disable itself!");
			this.setEnabled(false);
			}
		else { 
			log.info("HealthExtended Config file found");
			loadProcedure();
			log.info("HealthExtended Config loaded");
		} 
		pm.registerEvent(Event.Type.ENTITY_DAMAGE,entitylistener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerlistener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerlistener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerlistener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerlistener, Event.Priority.Normal, this);
		log.info("HealthExtended Initializes MySQL...");
		mysql.database=mysqldatabase;
		mysql.host=mysqlhost;
		mysql.username=mysqluser;
		mysql.password=mysqlpassword;
		mysql.initialize();
		try{
			if(!mysql.checkConnection()){mysql.writeError("MySQL connection problem occured!", true);}
			if(mysql.checkTable(mysqltablename)){mysql.writeInfo("Table present...");}
			else{mysql.writeError("Table absent, creating it...", false);
			if(mysql.createTable("CREATE TABLE `"+mysqldatabase+"`.`"+mysqltablename+"` ( `id` INT( 11 ) NOT NULL AUTO_INCREMENT PRIMARY KEY , `playername` VARCHAR( 255 ), `HP` INT( 11 ) , `XP` INT( 11 ), `LVL` INT(11))"))
			{
			mysql.writeInfo("Table created.");
			}
			else
			{
				mysql.writeInfo("Could not Create table.");
			}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		log.info("Enabled HealthExtended");
	}
	
	
	public void onDisable()
	{
		log.info("Disabled HealthExtended");
	}

	/**
	 * called to load configs
	 */
	
	public void loadProcedure() { 
		try{
		FileInputStream in = new FileInputStream(Config);
		prop.load(in);in.close();}catch(Exception e){e.printStackTrace();} 
		
		mysqlhost = prop.getProperty("mysqlhost");
		if(mysqlhost==null)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node mysqlhost not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}

		mysqluser = prop.getProperty("mysqluser");
		if(mysqluser==null)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node mysqluser not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		mysqlpassword = prop.getProperty("mysqlpassword");
		if(mysqlpassword==null)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node mysqlpassword not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		mysqldatabase = prop.getProperty("mysqldatabase");
		if(mysqldatabase==null)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node mysqldatabase not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		mysqltablename = prop.getProperty("mysqltablename");
		if(mysqltablename==null)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node mysqltablename not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		BaseHealthExpPerLevel = Integer.parseInt(prop.getProperty("baseexp"));
		if(BaseHealthExpPerLevel<=0)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node baseexp not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		HealthExpPerLevelProgressionRate = Float.parseFloat(prop.getProperty("expprogression"));
		if(HealthExpPerLevelProgressionRate<=0)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node expprogression not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
		HPMaxLvl = Integer.parseInt(prop.getProperty("maxlevel"));
		if(HPMaxLvl<=0)
		{
			log.log(Level.WARNING,"[HealthExtended] config corrupted or node maxlevel not set properly!");
			log.log(Level.WARNING,"[HealthExtended] Disabling!");
			this.setEnabled(false);	
		}
		
	}
	
	/**
	 * Handles player eat event
	 * @param player Player eating block
	 * @param block Block to eat
	 * @return State of eating
	 */
	public boolean handlePlayerEat(Player player, Block block) {
		String blockMatName = block.getType().name();
		blockMatName=blockMatName.toUpperCase();
		if (!foods.containsKey(blockMatName)) {
			return false;
		}
		if((player.getHealth()+foods.get(blockMatName))>=20)
		{
		this.healplayer(player,foods.get(blockMatName)-(20-player.getHealth()));
		return true;
		}
		else
		{
		return false;
		}
	}
	
	/**
     * Handles player eat event
	 * @param player Player eating block
	 * @param item Item to eat
	 * @return State of eating
	 */
	
	public boolean handlePlayerEat(Player player, ItemStack item) {
		Block block = player.getTargetBlock(null, 100);
		String blockMatName = block.getType().name();
		blockMatName=blockMatName.toUpperCase();
		List<String> ignoreClickBlock = Arrays.asList(ignoreClickBlocks);
		if (block != null) {
			if (ignoreClickBlock.contains(blockMatName)) {
				return false;
			}
		}
		String itemMatName = item.getType().name();
		if (!foods.containsKey(itemMatName)) {
			return false;
		}
		if((player.getHealth()+foods.get(itemMatName))>=20)
		{
		this.healplayer(player,foods.get(itemMatName)-(20-player.getHealth()));
		return true;
		}
		else
		{
		return false;
		}
	}
	
	/**
	 *  Called to recognize command
	 */
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("hl"))
		{
			Player player=(Player) sender;
			String playertocheck=player.getName();
			if(PlayerHP.containsKey(playertocheck))
			{
				int curHP=PlayerHP.get(playertocheck);
				int curXP=PlayerXP.get(playertocheck);
				int maxHP=PlayerLVL.get(playertocheck);
				int maxXP=(int)(BaseHealthExpPerLevel*(Math.pow(HealthExpPerLevelProgressionRate,maxHP)));
				String colorcode="§f";	
				if(curHP>0)
				{
				if(((curHP*100)/maxHP)>60){colorcode="§a";}
				if(((curHP*100)/maxHP)<=60 && ((curHP*100)/maxHP)>25){colorcode="§e";}
				if(((curHP*100)/maxHP)<=25){colorcode="§4";}
				}
				player.sendMessage(colorcode+"HP: "+curHP+"/"+maxHP);
				player.sendMessage("Experience: "+curXP+"/"+maxXP);
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * Helper method to heal player
	 * @param player Player to heal
	 * @param healbyamount How much Half-Hearts to heal
	 */
	
	public void healplayer(Player player, int healbyamount)
	{
		String playern=player.getName();
				int HP=PlayerHP.get(playern);
				int maxHP=PlayerLVL.get(playern);
				HP+=healbyamount;
				if(HP>maxHP){HP=maxHP;}
			PlayerHP.put(playern,HP);
	}

	/**
	 * Damage handling
	 * @param eventt Event to handle
	 */
	public void do_damage(EntityDamageEvent eventt)
	{
	Entity damaged=eventt.getEntity();
	int damage=eventt.getDamage();
	int HitPointsInBase=0;
	int HitPointsExpInBase=0;
	int HitPointsMaxInBase=0;
	
if(eventt instanceof EntityDamageByEntityEvent)
{
EntityDamageByEntityEvent event=(EntityDamageByEntityEvent)eventt;
Entity damager=event.getDamager();

if(damager instanceof Arrow)//If referenced by entity Arrow
{
	damager=((Arrow) damager).getShooter();//Get arrow shooter for further actions
	if(damager instanceof Player)//If damager was arrow and after getting its shooter check if shooter was player
	{
		//If it WAS player
	String damagedplayername=((Player) damager).getName();
	HitPointsExpInBase=PlayerXP.get(damagedplayername);
	HitPointsMaxInBase=PlayerLVL.get(damagedplayername);
	HitPointsExpInBase+=damage;//Increase XP
	int expfornextlvl=(int)(BaseHealthExpPerLevel*(Math.pow(HealthExpPerLevelProgressionRate,HitPointsMaxInBase)));
	while(HitPointsExpInBase>=expfornextlvl && HitPointsMaxInBase<HPMaxLvl)
	{
		HitPointsMaxInBase++;//Dodaj level
		HitPointsInBase=HitPointsMaxInBase;//Set HP to Max on Level
		HitPointsExpInBase-=expfornextlvl;//Odejmij Wymagane XP
		PlayerHP.put(damagedplayername,HitPointsInBase);
		((Player) damager).sendMessage("§e--Health Level Up!--");
	}
	PlayerXP.put(damagedplayername,HitPointsExpInBase);
	PlayerLVL.put(damagedplayername,HitPointsMaxInBase);
	}
	else
	{
		//Skeletons etc... do not get xp, they are not eligible
	}
	}
else if(damager instanceof Player)//jeœli zadaj¹cy nie jest strza³¹ to zadanie bezpoœrednie
	{
		String damagedplayername=((Player) damager).getName();
		HitPointsExpInBase=PlayerXP.get(damagedplayername);
		HitPointsMaxInBase=PlayerLVL.get(damagedplayername);
		HitPointsExpInBase+=damage;//Increase XP
		int expfornextlvl=(int)(BaseHealthExpPerLevel*(Math.pow(HealthExpPerLevelProgressionRate,HitPointsMaxInBase)));
		while(HitPointsExpInBase>=expfornextlvl && HitPointsMaxInBase<HPMaxLvl)
		{
			HitPointsMaxInBase++;//Dodaj level
			HitPointsInBase=HitPointsMaxInBase;//Set HP to Max on Level
			HitPointsExpInBase-=expfornextlvl;//Odejmij Wymagane XP
			PlayerHP.put(damagedplayername,HitPointsInBase);
			((Player) damager).sendMessage("§e--Health Level Up!--");
		}
		PlayerXP.put(damagedplayername,HitPointsExpInBase);
		PlayerLVL.put(damagedplayername,HitPointsMaxInBase);
		}
 if(damaged instanceof Player)
	{
		Player damagedplayer=(Player)damaged;
		String damagedplayername=damagedplayer.getName();
		HitPointsInBase=PlayerHP.get(damagedplayername);
		if (damage<=HitPointsInBase)//If enough additional HP
		{
			HitPointsInBase-=damage;//Sub damage from additional HP
			PlayerHP.put(damagedplayername,HitPointsInBase);
			eventt.setDamage(0);//Cancel DamageEvent
		}
		else//If not enough Additional HP
		{
			damage-=HitPointsInBase;//Lower damage by as much as you can
			PlayerHP.put(damagedplayername,0);
			eventt.setDamage(damage);
		}
		}
}
else
{
	if(damaged instanceof Player)
	{
		Player damagedplayer=(Player)damaged;
		String damagedplayername=damagedplayer.getName();
		HitPointsInBase=PlayerHP.get(damagedplayername);
		if (damage<=HitPointsInBase)//If enough additional HP
		{
			HitPointsInBase-=damage;//Sub damage from additional HP
			PlayerHP.put(damagedplayername,HitPointsInBase);
			eventt.setDamage(0);//Cancel DamageEvent
		}
		else//If not enough Additional HP
		{
			damage-=HitPointsInBase;//Lower damage by as much as you can
			PlayerHP.put(damagedplayername,0);
			eventt.setDamage(damage);
		}
		}
	}
}
	/**
	 * Saving data for player
	 * @param player Player to save for
	 */
	public void savedata(Player player)
	{
		String playername=player.getName();
		int HP=PlayerHP.get(playername);
		int XP=PlayerXP.get(playername);
		int LVL=PlayerLVL.get(playername);
		try
		{
		mysql.updateQuery("UPDATE `"+mysqldatabase+"`.`"+mysqltablename+"` SET HP='"+HP+"', XP='"+XP+"', LVL='"+LVL+"' WHERE playername='"+playername+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void loaddata(Player player)
	{
		String playername=player.getName();
		try
		{
		ResultSet result=mysql.sqlQuery("SELECT HP,XP,LVL FROM `"+mysqldatabase+"`.`"+mysqltablename+"` WHERE playername='"+playername+"'");
		if(result.next())
		{
			result.absolute(1);
			PlayerHP.put(playername, result.getInt("HP"));
			PlayerXP.put(playername, result.getInt("XP"));
			PlayerLVL.put(playername, result.getInt("LVL"));
		}
		else
		{
			mysql.insertQuery("INSERT INTO `"+mysqldatabase+"`.`"+mysqltablename+"` VALUES (null,'"+playername+"','0','0','0')");
			PlayerHP.put(playername, 0);
			PlayerXP.put(playername, 0);
			PlayerLVL.put(playername,0);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
