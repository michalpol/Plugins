package org.michalpol.BuyXP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.Vault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BuyXP extends JavaPlugin {
	 private static final Logger log = Logger.getLogger("Minecraft");
	    public static Economy econ = null;
	    private static Vault vault = null;
	    private static double xpprice= 5;
	    private static double xpprice2= 0.02;
		static String mainDirectory = "plugins/BuyXP"; 
		static File Config = new File(mainDirectory + File.separator + "config.properties");
		static Properties prop = new Properties();
	public void onEnable()
	{
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if(x != null & x instanceof Vault) {
            vault = (Vault) x;
            log.info(String.format("[%s] Hooked %s %s", getDescription().getName(), vault.getDescription().getName(), vault.getDescription().getVersion()));
		if (!setupEconomy() ) {
            log.log(Level.SEVERE, String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		}
		else
		{
			log.log(Level.SEVERE, String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
		}
		log.info("BuyXP looks for config files...");
		 new File(mainDirectory).mkdir();
		 
			if(!Config.exists()){ 
				log.log(Level.WARNING,"BuyXP config not found!");
				log.log(Level.WARNING,"BuyXP will disable itself!");
				this.setEnabled(false);
				}
			else { 
				loadProcedure();
			}
		log.log(Level.INFO, String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}
	
	private void loadProcedure() {
		try{
			FileInputStream in = new FileInputStream(Config);
			prop.load(in);in.close();}catch(Exception e){e.printStackTrace();} 
			xpprice =Double.parseDouble(prop.getProperty("XPprice"));
			xpprice2 = Double.parseDouble(prop.getProperty("XPpriceSell"));
			if(xpprice<=0||xpprice2<=0)
			{
				log.log(Level.WARNING,"[BuyXP]XPprice not set properly, defaulting to 5.");
				xpprice=5;
				prop.setProperty("XPprice", Double.toString(xpprice));
				prop.setProperty("XPpriceSell", Double.toString(xpprice));
				try{
					FileOutputStream out = new FileOutputStream(Config);
					prop.store(out, "");
					out.close();
				}catch(Exception e){e.printStackTrace();}
				log.log(Level.WARNING,"[BuyXP]Regenerating config file...");
			}
		
	}

	public void onDisable()
	{
		log.log(Level.INFO, String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        Player player = (Player) sender;

        if(command.getLabel().equalsIgnoreCase("buyxp")) {
        	if(args.length == 0)
        	{
        		return false;	
        	}
        	double wantedxp=Double.parseDouble(args[0]);
        	if(Math.floor(wantedxp)!=wantedxp)
        	{
        		sender.sendMessage("§cYou may only specify integers in this command - NO FRACTIONS!");
        		return true;	
        	}
        	if(wantedxp<1)
        	{
        		sender.sendMessage("§cYou may only specify values GREATER THAN 0!");
        		return true;	
        	}
            sender.sendMessage(String.format("You have §6%s§f", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.withdrawPlayer(player.getName(), wantedxp*xpprice);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were charged §6%s§f for §6%s XP§f and now have §6%s§f", econ.format(r.amount),String.valueOf((int)wantedxp) ,econ.format(r.balance)));
                player.giveExp((int)Math.floor(wantedxp));
            }else {
                sender.sendMessage(String.format("§cAn error occured: %s", r.errorMessage));
            }
            return true;
        }else if(command.getLabel().equalsIgnoreCase("sellxp"))
        {
        	if(args.length == 0)
        	{
        		return false;	
        	}
        	double grantedlvls=Double.parseDouble(args[0]);
        	if(Math.floor(grantedlvls)!=grantedlvls)
        	{
        		sender.sendMessage("§cYou may only specify integers in this command - NO FRACTIONS!");
        		return true;	
        	}
        	if(grantedlvls<1)
        	{
        		sender.sendMessage("§cYou may only specify values GREATER THAN 0!");
        		return true;	
        	}
        	if(grantedlvls>player.getLevel())
        	{
        		sender.sendMessage("§cYou may only specify values LESS OR EQUAL TO YOUR LEVEL!");
        		return true;	
        	}
        	
        	int lvl = player.getLevel();
        	int tolvl=(int)(lvl-grantedlvls);
        	int xp = 0;
        	int totxp=0;
        	int ptotxp=player.getTotalExperience();
        	while (lvl>tolvl) 
        	{
        	  xp= 17+((Math.max(0, lvl-16))*3);
        	  totxp+=xp;
        	  lvl--;
        	}
        	ptotxp=ptotxp-totxp;
            sender.sendMessage(String.format("You have §6%s§f", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.depositPlayer(player.getName(), totxp*(xpprice2));
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were credited §6%s§f for §6%s§f XP levels (worth §6%s XP§f) and now have §6%s§f", econ.format(r.amount),String.valueOf((int)grantedlvls),String.valueOf(totxp) ,econ.format(r.balance)));
                player.setLevel(tolvl);
                player.setTotalExperience(ptotxp);
            }else {
                sender.sendMessage(String.format("§cAn error occured: %s", r.errorMessage));
            }
            return true;
        	
        }
        else {
            return false;
        }
    }
}
	
	
