package org.michalpol.ServerUptime;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

public class ServerUptimeHBPoller implements Runnable {

	
	ServerUptime plugin;

	ServerUptimeHBPoller (ServerUptime instance){
		plugin = instance;
	}

	@Override
	public void run() {
		
		mysqlCore mysql = new mysqlCore(plugin.log,"[ServerUptime]",plugin.HOST,plugin.DB,plugin.USER,plugin.PWD);
		mysql.initialize();
		try{
		mysql.checkConnection();
		mysql.insertQuery("INSERT INTO uptime VALUES (null,UNIX_TIMESTAMP(),'HBEvent',0)");
		mysql.writeInfo("HeartBeatEvent Written to DB.");
		mysql.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
