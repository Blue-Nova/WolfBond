package io.github.bluenova.strongWolf;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BondKeeper {

    HashMap<WolfOwner,BondWolf> bondList = new HashMap<>();

    public void addBond(Player player) {
        if (isPlayerPaired(player)) {
            player.sendMessage("§cYou already have a bonded wolf!");
            return;
        }
        WolfOwner bond = new WolfOwner(player);
        bondList.put(bond, bond.getWolf());
        bond.getPlayer().sendMessage("§aYou have successfully bonded with a wolf!");
    }

    public HashMap<WolfOwner, BondWolf> getBondList() {
        return bondList;
    }

    public boolean isPlayerPaired(Player player) {
        for (WolfOwner owner : bondList.keySet()) {
            if (owner.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public WolfOwner getOwnerFromPlayer(Player player) {
        for (WolfOwner owner : bondList.keySet()) {
            if (owner.getPlayer().equals(player)) {
                return owner;
            }
        }
        return null;
    }

    public boolean isWolfBonded(Wolf entity) {
        for (WolfOwner owner : bondList.keySet()) {
            if (owner.getWolf().getWolfEntity().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public WolfOwner getOwnerFromWolf(Wolf entity) {
        for (WolfOwner owner : bondList.keySet()) {
            if (owner.getWolf().getWolfEntity().equals(entity)) {
                return owner;
            }
        }
        return null;
    }

    public void upgradeBond(@NotNull Player player, Wolf rightClicked) {
        WolfOwner owner = getOwnerFromPlayer(player);
        if (owner != null && isWolfBonded(rightClicked)) {
            BondWolf bondWolf = bondList.get(owner);
            // upgrade attributes in a one by one manner
            bondWolf.upgradeAttributes();
        } else {
            player.sendMessage("§cThis wolf is not bonded to you.");
        }
    }

    public void removeBond(Player player) {
        WolfOwner owner = getOwnerFromPlayer(player);
        if (owner != null) {
            BondWolf bondWolf = bondList.get(owner);
            bondWolf.remove();
            owner.remove();
            bondList.remove(owner);
            player.sendMessage("§aYour bond with your wolf has been removed.");
        } else {
            player.sendMessage("§cYou do not have a bonded wolf to remove.");
        }
    }
}
