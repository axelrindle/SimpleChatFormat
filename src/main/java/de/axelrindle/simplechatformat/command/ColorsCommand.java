package de.axelrindle.simplechatformat.command;

import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ColorsCommand implements ISubCommand {

    private static final HashMap<String, String> CASE_CACHE = new HashMap<>();

    @Override
    public String getPermission() {
        return "simplechatformat.colors";
    }

    @Override
    public String getDescription() {
        return "Displays a list of all available chat colors to use.";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(SimpleChatFormat.PREFIX + " §rColor Codes");
        sender.sendMessage("");
        for (ChatColor color : ChatColor.values()) {
            String msg = String.format("&%1$s - §%1$s%2$s", color.getChar(), toTitleCase(color.name()));
            sender.sendMessage(msg);
        }
        return true;
    }

    /**
     * Converts the {@link ChatColor} enum names to title case.
     *
     * @param text The text to convert.
     * @return The given text in title case.
     */
    private static String toTitleCase(String text) {
        if (CASE_CACHE.containsKey(text)) {
            return CASE_CACHE.get(text);
        }
        if (text == null || text.isEmpty()) {
            return text;
        }

        // https://www.baeldung.com/java-string-title-case#2-splitting-into-words
        String result = Arrays
                .stream(text.split("_"))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0)) + word
                        .substring(1)
                        .toLowerCase())
                .map(word -> word.replace('_', ' '))
                .collect(Collectors.joining(" "));
        CASE_CACHE.put(text, result);
        return result;
    }
}
