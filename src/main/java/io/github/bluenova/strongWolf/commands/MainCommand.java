package io.github.bluenova.strongWolf.commands;

import io.github.bluenova.strongWolf.commands.subcommands.CreateBond;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MainCommand implements CommandExecutor, TabCompleter {

    private Map<String,SubCommand> subCommands;

    public MainCommand() {
        this.subCommands = Map.of(
                "createbond", new CreateBond()
        );

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Available subcommands:");
            for (SubCommand subCommand : subCommands.values()) {
                commandSender.sendMessage(subCommand.getSyntax() + " - " + subCommand.getDescription());
            }
            return true;
        }
        if (!commandSender.isOp()) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        SubCommand subCommand = subCommands.get(strings[0]);
        if (subCommand != null) {
            subCommand.execute(strings, commandSender);
            return true;
        }
        commandSender.sendMessage("Unknown subcommand. Use /dbm to see available subcommands.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return subCommands.keySet().stream().filter(name -> name.startsWith(strings[0])).toList();
        }
        if (strings.length > 1) {
            SubCommand subCommand = subCommands.get(strings[0]);
            if (subCommand != null) {
                return subCommand.tabComplete(strings);
            }
        }
        return subCommands.keySet().stream().toList();
    }
}
