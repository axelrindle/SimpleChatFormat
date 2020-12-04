package de.axelrindle.simplechatformat.command;

import org.bukkit.command.CommandExecutor;

public interface ISubCommand extends CommandExecutor {

    String getPermission();

}
