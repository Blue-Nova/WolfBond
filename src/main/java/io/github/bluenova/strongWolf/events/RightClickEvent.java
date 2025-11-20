package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.BondWolf;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickEvent implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        // Only care about right-clicking a block (i.e., right-clicking the ground)
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        // Only trigger while the player is sneaking (crouching)
        if (!player.isSneaking()) return;

        // Defensive: ensure clicked block exists
        if (event.getClickedBlock() == null) return;

        // TODO: Replace with real handling logic (call plugin API, custom event, etc.)

        // is player bonded
        BondKeeper bondKeeper = StrongWolfPlugin.getPlugin().getBondKeeper();
        if (bondKeeper.isPlayerPaired(player)) {
            WolfOwner owner = bondKeeper.getOwnerFromPlayer(player);
            BondWolf bondedWolf = bondKeeper.getBondList().get(owner);
            // Teleport bonded wolf to right-clicked location
            bondedWolf.getWolfEntity().teleport(event.getClickedBlock().getLocation().add(0.5, 1, 0.5));
            bondedWolf.sit();
        }

        /**Bukkit.getConsoleSender().sendMessage(player.getName() + " right-clicked a block while crouching at "
           + player.getLocation().getBlockX() + ", "
                + player.getLocation().getBlockY() + ", "
                + player.getLocation().getBlockZ());**/
    }

}
