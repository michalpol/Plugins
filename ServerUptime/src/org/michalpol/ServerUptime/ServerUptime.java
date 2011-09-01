/**
 * 
 */
package org.michalpol.ServerUptime;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alta189.sqlLibrary.MySQL.*;

import org.bukkit.plugin.java.JavaPlugin;
/**
 * @author Michalpol
 *
 */
public class ServerUptime extends JavaPlugin {
	
	public Logger log = Logger.getLogger("Minecraft");//logger
	static String mainDirectory = "plugins/ServerUptime"; 
	static File Config = new File(mainDirectory + File.separator + "config.properties");
	static Properties prop = new Properties();
	boolean SELF_DISABLED=false;
	public String HOST="";
	public String USER="";
	public String PWD="";
	public String DB="";
	long HBInterval=1200;
	ServerUptimeHBPoller HeartBeat = new ServerUptimeHBPoller(this);
	public void onEnable(){ 
		 log.info("ServerUptime looks for config files...");
		 new File(mainDirectory).mkdir();
		 
			if(!Config.exists()){ 
				log.log(Level.WARNING,"ServerUptime config not found!");
				log.log(Level.WARNING,"ServerUptime will disable itself!");
				SELF_DISABLED=true;
				this.setEnabled(false);
				}
			else { 
				loadProcedure();
			} 
			mysqlCore mysql = new mysqlCore(log,"[ServerUptime]",HOST,DB,USER,PWD);
			mysql.initialize();
			try{
				if(!mysql.checkConnection()){mysql.writeError("MySQL connection problem occured!", true);}
				if(mysql.checkTable("uptime")){mysql.writeInfo("Table present...");}
				else{mysql.writeError("Table absent, creating it...", false);
				if(mysql.createTable("CREATE TABLE `minecraft`.`uptime` ( `id` INT( 11 ) NOT NULL AUTO_INCREMENT PRIMARY KEY , `timestamp` INT( 11 ) NOT NULL , `event` VARCHAR( 10 ) NOT NULL , `timetaken` INT( 11 ) NOT NULL)"))
				{
				mysql.writeInfo("Table created.");
				}
				else
				{
					mysql.writeInfo("Could not make table.");
				}
				}
				ResultSet result=mysql.sqlQuery("SELECT event from uptime ORDER BY id DESC LIMIT 1");
				if(result.next()){
				result.absolute(1);
				String res=result.getString("event");
				if(res.equals("StopEvent") || res.equals(null) || res.equals(""))
				{
					mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'StartEvent',0)");
					mysql.writeInfo("Latest server shutdown was normal.");
				}
				else
				{
					ResultSet TIMEOFCRASH1=mysql.sqlQuery("SELECT `timestamp` from uptime ORDER BY id DESC LIMIT 1");
					TIMEOFCRASH1.absolute(1);
					int TIMEOFCRASH=TIMEOFCRASH1.getInt("timestamp");
					mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'CrashEvent',UNIX_TIMESTAMP()-"+TIMEOFCRASH+")");
					mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'StartEvent',0)");
					mysql.writeInfo("Latest server shutdown was caused by a crash.");
				}
				}
				else
				{
					mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'StartEvent',0)");
					mysql.writeInfo("Latest server shutdown was Empty (This is first startup record).");
				}
				mysql.close();
			}catch(Exception e){e.printStackTrace();}
			getServer().getScheduler().scheduleSyncRepeatingTask(this,HeartBeat,0,HBInterval);
	}
	 
	public void onDisable(){ 
	 if(!SELF_DISABLED)
	 {
		 mysqlCore mysql = new mysqlCore(log,"[ServerUptime]",HOST,DB,USER,PWD);
			mysql.initialize();
			try{
			mysql.checkConnection();
			mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'StopEvent',0)");
			mysql.writeInfo("Shutdown successfully writen to DB.");
			mysql.close();
			}catch(Exception e){e.printStackTrace();}
	 }
	 else
	 {
		 log.log(Level.WARNING, "Plugin was disabled due to initialisation error.");
	 }
	}
	public void loadProcedure() { 
		try{
		FileInputStream in = new FileInputStream(Config);
		prop.load(in);in.close();}catch(Exception e){e.printStackTrace();} 
		HOST = prop.getProperty("HOST");
		if(HOST==null)
		{
			log.log(Level.WARNING,"[ServerUptime] config corrupted or node HOST not set properly!");
			log.log(Level.WARNING,"ServerUptime will disable itself!");
			SELF_DISABLED=true;
			this.setEnabled(false);
		}
		USER = prop.getProperty("USER");
		if(USER==null)
		{
			log.log(Level.WARNING,"[ServerUptime] config corrupted or node USER not set properly!");
			log.log(Level.WARNING,"ServerUptime will disable itself!");
			SELF_DISABLED=true;
			this.setEnabled(false);
		}
		PWD = prop.getProperty("PWD");
		if(PWD==null)
		{
			log.log(Level.WARNING,"[ServerUptime] config corrupted or node PWD not set properly!");
			log.log(Level.WARNING,"ServerUptime will disable itself!");
			SELF_DISABLED=true;
			this.setEnabled(false);	
		}
		DB = prop.getProperty("DB");
		if(DB==null)
		{
			log.log(Level.WARNING,"[ServerUptime] config corrupted or node DB not set properly!");
			log.log(Level.WARNING,"ServerUptime will disable itself!");
			SELF_DISABLED=true;
			this.setEnabled(false);
		}
		HBInterval = Long.parseLong(prop.getProperty("HBInterval"));
		if(HBInterval<=0)
		{
			log.log(Level.WARNING,"[ServerUptime]HBInterval not set properly, defaulting to 1200.");
			HBInterval=1200;
		}
	}
}
