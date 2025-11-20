package io.github.bluenova.strongWolf.commands.subcommands;

import io.github.bluenova.strongWolf.BondWolf;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import io.github.bluenova.strongWolf.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class RemoveBond implements SubCommand {
    @Override
    public void execute(String[] args, CommandSender sender) {
        // Implementation for creating a bond between a player and a wolf
        if (args.length < 2) {
            sender.sendMessage("Usage: " + getSyntax());
            return;
        }
        String playerName = args[1];
        Player player = sender.getServer().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("Player not found: " + playerName);
            return;
        }
        // Here you would add the logic to create the bond
        StrongWolfPlugin.getPlugin().getBondKeeper().removeBond(player);
    }

    @Override
    public List<String> tabComplete(String[] args) {
        Map<WolfOwner, BondWolf> bondList = StrongWolfPlugin.getPlugin().getBondKeeper().getBondList();
        for (WolfOwner owner : bondList.keySet()) {
            if (owner.getPlayer().isOnline()) {
                if (owner.getPlayer().getName().startsWith(args[1])) {
                    return List.of(owner.getPlayer().getName());
                }
            }
        }
        return List.of();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "";
    }
}
