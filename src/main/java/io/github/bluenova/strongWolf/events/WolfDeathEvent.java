package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class WolfDeathEvent implements Listener {

    @EventHandler
    public void onWolfDeath(EntityDeathEvent event) {
        // is entity a wolf?
        if (!event.getEntity().getType().equals(EntityType.WOLF)) {
            return;
        }
        // is wolf part of the plugin?
        BondKeeper bondKeeper = StrongWolfPlugin.getPlugin().getBondKeeper();
        if (bondKeeper.isWolfBonded((Wolf)event.getEntity())) {
            WolfOwner owner = bondKeeper.getOwnerFromWolf((Wolf)event.getEntity());
            owner.getWolf().death();
        }
    }

}
