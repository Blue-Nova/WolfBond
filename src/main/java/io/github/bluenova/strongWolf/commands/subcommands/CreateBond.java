package io.github.bluenova.strongWolf.commands.subcommands;

import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import io.github.bluenova.strongWolf.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateBond implements SubCommand {
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
        StrongWolfPlugin.getPlugin().getBondKeeper().addBond(new WolfOwner(player));
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return List.of(Bukkit.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith(args[1]))
                .toArray(String[]::new));
    }

    @Override
    public String getName() {
        return "createbond";
    }

    @Override
    public String getDescription() {
        return "Create a bond between a player and a wolf. (starting the game)";
    }

    @Override
    public String getSyntax() {
        return "/strongwolf createbond <player>";
    }
}
