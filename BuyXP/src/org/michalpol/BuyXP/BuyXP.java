package org.michalpol.BuyXP;

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
		log.log(Level.INFO, String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
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
                sender.sendMessage(String.format("You were charged §6%s§f for §6%s XP§f and now have §6%s§f", econ.format(r.amount),String.valueOf(wantedxp) ,econ.format(r.balance)));
                player.giveExp((int)Math.floor(wantedxp));
            }else {
                sender.sendMessage(String.format("§cAn error occured: %s", r.errorMessage));
            }
            return true;
        } else {
            return false;
        }
    }
}
	
	
