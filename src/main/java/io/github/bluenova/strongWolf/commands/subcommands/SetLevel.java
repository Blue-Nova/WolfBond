package io.github.bluenova.strongWolf.commands.subcommands;

import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import io.github.bluenova.strongWolf.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLevel implements SubCommand {
    @Override
    public void execute(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return;
        }
        Player player = (Player) sender;
        // get number from args[0]
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /wolf setlevel <level>");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[1]);
            WolfOwner owner = StrongWolfPlugin.getPlugin().getBondKeeper().getOwnerFromPlayer(player);
            if (owner.getWolf().getMaxLevel() < level) {
                sender.sendMessage("§cLevel cannot be higher than your wolf's max level of " + owner.getWolf().getMaxLevel() + "!");
                return;
            }
            owner.getWolf().setLevel(level);
            sender.sendMessage("§aSet your wolf's level to " + level + "!");

        } catch (NumberFormatException e) {
            sender.sendMessage("§cLevel must be a number!");
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return List.of();
    }

    @Override
    public String getName() {
        return "setlevel";
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
