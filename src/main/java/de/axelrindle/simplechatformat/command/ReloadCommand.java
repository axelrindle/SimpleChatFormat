package de.axelrindle.simplechatformat.command;

import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements ISubCommand {

    private final SimpleChatFormat plugin;

    public ReloadCommand(SimpleChatFormat plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "simplechatformat.reload";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(SimpleChatFormat.PREFIX + " §aConfig has been reloaded.");
        return true;
    }
}
