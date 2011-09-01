package org.michalpol.HealthExtended;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
/**
 * Health Extended Plugin
 * @author michalpol
 * @version 0.0.2
 */

public class HealthExtendedEntityListener extends EntityListener{
	public static HealthExtended plugin; public HealthExtendedEntityListener(HealthExtended instance) {
        plugin = instance;
}
	/**
	 * called to perform entity damage events
	 */
	public void onEntityDamage(EntityDamageEvent eventt)
	{
		plugin.do_damage(eventt);
	}
}
