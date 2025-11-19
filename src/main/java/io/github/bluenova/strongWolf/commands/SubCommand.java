package io.github.bluenova.strongWolf.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    void execute(String[] args, CommandSender sender);

    List<String> tabComplete(String[] args);

    String getName();

    String getDescription();

    String getSyntax();


}
