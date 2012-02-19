package org.michalpol.Vitals;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.event.player.PlayerTeleportEvent;
 
public class VitalsListener implements Listener {
	private Vitals pluginhead= null;
    public VitalsListener(Vitals plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        pluginhead=plugin;
    }
 
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(EntityDeathEvent event) {
    	if(!(event.getEntity() instanceof Player)){return;}
    	if(pluginhead.timelefts.containsKey(((Player)event.getEntity()).getName())){return;}
    	//Check if damage was caused by a plugin, should make sense
    	if(event.getEntity().getLastDamageCause().getCause() != DamageCause.CUSTOM)
    	{
        Player p = (Player)event.getEntity();
        p.setHealth(1);//Prevent for looping deaths
        pluginhead.setDead(p);
        p.sendMessage("You are now wounded and waiting for a medic.");
        p.sendMessage("Use /vitals die to die immediatelly.");
        String LocStr = "";
        LocStr+="X= "+Integer.toString(p.getLocation().getBlockX())+"; ";
        LocStr+="Y= "+Integer.toString(p.getLocation().getBlockY())+"; ";
        LocStr+="Z= "+Integer.toString(p.getLocation().getBlockZ())+"";
        pluginhead.getServer().broadcastMessage(p.getName()+" ("+LocStr+") is now wounded and waiting for medic.");
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			p.teleport(event.getFrom());//just TP back
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//just cancel the event execution if block is removed/placed by dead player
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//just cancel the event execution if block is removed/placed by dead player
    		}
    	}
    }
//    @EventHandler(priority = EventPriority.LOW)
//    public void onPlayerTeleport(PlayerTeleportEvent event)
//    {
//    	if(event.isCancelled()){return;}
//    	Player p = (Player) event.getPlayer();
//    	/*
//    	 *Check if player is registered in plugin as timed,
//    	 *if player is not there that means that he most likely haven't died yet.
//    	 */
//    	if(pluginhead.timelefts.containsKey(p.getName()))
//   	{
//    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
//    		{
//    			p.teleport(event.getFrom());//just TP back
//    		}
//    	}
//    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			String x = Integer.toString(p.getLocation().getBlockX());
    			String y = Integer.toString(p.getLocation().getBlockY());
    			String z = Integer.toString(p.getLocation().getBlockZ());
    			String T = Integer.toString(pluginhead.timelefts.get(p.getName()));
    			//send the need medic message replacing any 
    			//text player would want to say, just for fun
    			//Also including coordinates and time left.
    			event.setMessage("Need a Medic! (At X= "+x+"; Y= "+y+"; Z= "+z+"; LEFT: "+T+" sec)");
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			if (event.getMessage().startsWith("/vitals"))
    			{
    				if (!event.getMessage().contains("die"))
    				{
    					p.sendMessage("You may only use /vitals die subcommand while waiting for medic.");
    				}
    			}
    			else if(event.getMessage()!=null)
    			{
    				event.setCancelled(true);
    				p.sendMessage("You may only use /vitals die command while waiting for medic.");
    			}
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent event)
    {
    	if(event.isCancelled()){return;}
    	if(!(event.getEntity() instanceof Player)){return;}
    	Player p = (Player) event.getEntity();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//cancel any damage
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event)
    {
    	if(event.isCancelled()){return;}
    	if(!(event.getEntity() instanceof Player)){return;}
    	Player p = (Player) event.getEntity();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//cancel any damage
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBedEnter(PlayerBedEnterEvent event)
    {
    	if(event.isCancelled()){return;}
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//cancel going into bed by dead player
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
    	Player p = (Player) event.getPlayer();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			dieplayer(p, true);//Player dies automatically if he/she leaves while
    			//being wounded.
    		}
    	}
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRegain(EntityRegainHealthEvent event)
    {
    	if(event.isCancelled()){return;}
    	if(!(event.getEntity() instanceof Player)){return;}
    	Player p = (Player) event.getEntity();
    	/*
    	 *Check if player is registered in plugin as timed,
    	 *if player is not there that means that he most likely haven't died yet.
    	 */
    	if(pluginhead.timelefts.containsKey(p.getName()))
    	{
    		if(pluginhead.timelefts.get(p.getName())>0)//if really is dead
    		{
    			event.setCancelled(true);//cancel any regain
    		}
    	}
    }
    
    //PERFORMING METHODS (HELPERS)
    public void dieplayer(Player p,boolean commanddeath)
    {
    	//TODO not sure if this works, should kill player without looping itself
    	p.setHealth(0);
    	p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 20));
    	if(commanddeath)
    	{
    		p.sendMessage("Died by command.");
    		pluginhead.timelefts.remove(p.getName());
    		pluginhead.getServer().broadcastMessage(p.getName()+" died by his command");
    	}
    	else
    	{
    		p.sendMessage("You died, because noone helped you in "+Integer.toString(pluginhead.CONST_TIME_TO_RESPAWN)+" seconds.");
    		pluginhead.getServer().broadcastMessage(p.getName()+" because noone helped him/her.");
    		pluginhead.timelefts.remove(p.getName());
    	}
    }
}
