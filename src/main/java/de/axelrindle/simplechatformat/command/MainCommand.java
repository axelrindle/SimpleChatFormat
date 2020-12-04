package de.axelrindle.simplechatformat.command;

import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {

    private static final List<String> EMPTY_LIST = new ArrayList<>();

    private final HashMap<String, ISubCommand> subCommands = new HashMap<>();
    private final SimpleChatFormat plugin;

    public MainCommand(SimpleChatFormat plugin) {
        this.plugin = plugin;
        subCommands.put("colors", new ColorsCommand());
        subCommands.put("reload", new ReloadCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String key = args[0];
            if (subCommands.containsKey(key)) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                return subCommands.get(key).onCommand(sender, command, label, newArgs);
            } else {
                sender.sendMessage("§cUnknown subcommand!");
                return false;
            }
        } else {
            sender.sendMessage(SimpleChatFormat.PREFIX + " Help");
            sender.sendMessage("");
            sender.sendMessage("/scf");
            for (String subCommand : subCommands.keySet()) {
                sender.sendMessage("/scf " + subCommand);
            }
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.entrySet().stream()
                    .filter(entry -> {
                        String permission = entry.getValue().getPermission();
                        return permission == null || plugin.getPerms().has(sender, permission);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
        return EMPTY_LIST;
    }
}
