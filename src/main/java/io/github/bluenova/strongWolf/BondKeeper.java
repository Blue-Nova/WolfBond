package io.github.bluenova.strongWolf;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BondKeeper {

    HashMap<WolfOwner,BondWolf> bondList = new HashMap<>();

    public void addBond(WolfOwner bond) {
        if (isPlayerPaired(bond.getPlayer())) {
            bond.getPlayer().sendMessage("§cYou already have a bonded wolf!");
            return;
        }
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
            bondWolf.upgradeStrength(1);
            player.sendMessage("§aYour bonded wolf has been upgraded!");
        } else {
            player.sendMessage("§cThis wolf is not bonded to you.");
        }
    }
}
