package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.BondWolf;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class SwitchDimentionEvent implements Listener {

    @EventHandler
    public void onSwitchDimention(EntityPortalEnterEvent event) {
        if (event.getEntity().getType().equals(EntityType.WOLF)) {
            Wolf wolf = (Wolf) event.getEntity();
            if (!StrongWolfPlugin.getPlugin().getBondKeeper().isWolfBonded(wolf)) {
                return;
            }
            event.setCancelled(true);
            BondWolf bondedWolf = StrongWolfPlugin.getPlugin().getBondKeeper().getOwnerFromWolf(wolf).getWolf();
            WolfOwner owner = StrongWolfPlugin.getPlugin().getBondKeeper().getOwnerFromWolf(wolf);
            // Teleport wolf to owner in new dimension
            bondedWolf.getWolfEntity().teleport(owner.getPlayer().getLocation());
        }

        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();
            if (!StrongWolfPlugin.getPlugin().getBondKeeper().isPlayerPaired(player)) {
                return;
            }
            WolfOwner owner = StrongWolfPlugin.getPlugin().getBondKeeper().getOwnerFromPlayer(player);
            BondWolf bondedWolf = StrongWolfPlugin.getPlugin().getBondKeeper().getBondList().get(owner);
            // Teleport bonded wolf to player in new dimension
            bondedWolf.getWolfEntity().teleport(player.getLocation());
        }
    }
}
