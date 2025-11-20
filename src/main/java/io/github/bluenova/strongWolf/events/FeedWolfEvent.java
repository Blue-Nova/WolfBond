package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.StrongWolfPlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FeedWolfEvent implements Listener {

    @EventHandler
    public void onFeedWolf(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().getType().equals(EntityType.WOLF)) return;

        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) return;

        Material type = hand.getType();

        StrongWolfPlugin plugin = StrongWolfPlugin.getPlugin();

        // Bones = taming attempt
        if (type == Material.BONE) {
            if (plugin.getBondKeeper().isWolfBonded((Wolf) event.getRightClicked())) {
                plugin.getBondKeeper().upgradeBond(event.getPlayer(), (Wolf) event.getRightClicked());
                // remove one bone from player's hand
                hand.setAmount(hand.getAmount() - 1);
                event.setCancelled(true);
            }
            return;
        }

        // Common meats that heal wolves
        switch (type) {
            case BEEF:
            case COOKED_BEEF:
            case PORKCHOP:
            case COOKED_PORKCHOP:
            case MUTTON:
            case COOKED_MUTTON:
            case CHICKEN:
            case COOKED_CHICKEN:
            case RABBIT:
            case COOKED_RABBIT:

                return;
            default:
                return;
        }
    }
}
