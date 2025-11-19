package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageEntityEvent implements Listener {

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Player damagerPlayer) {
            player = damagerPlayer;
            // Logic for when a player directly damages an entity
        } else if (event.getDamager() instanceof Arrow arrow
                && arrow.getShooter() instanceof Player shooterPlayer) {
            player = shooterPlayer;
        }
        BondKeeper bondKeeper = StrongWolfPlugin.getPlugin().getBondKeeper();
        if (player != null && bondKeeper.isPlayerPaired(player)) {
            // Logic for when a paired player damages an entity
            WolfOwner owner = bondKeeper.getOwnerFromPlayer(player);
            if (owner != null) {
                owner.markTarget((LivingEntity) event.getEntity());
                event.setCancelled(true);
            }
        }
    }
}
