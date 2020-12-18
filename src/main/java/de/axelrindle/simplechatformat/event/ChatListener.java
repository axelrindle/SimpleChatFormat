package de.axelrindle.simplechatformat.event;

import de.axelrindle.simplechatformat.FormatResult;
import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.function.Function;

public class ChatListener implements Listener {

    private final SimpleChatFormat plugin;

    private final HashMap<String, Function<Player, String>> PLACEHOLDERS = new HashMap<>();

    public ChatListener(SimpleChatFormat plugin) {
        this.plugin = plugin;
        initReplacers();
    }

    private void initReplacers() {
        PLACEHOLDERS.put("prefix", player -> plugin.getChat().getPlayerPrefix(player));
        PLACEHOLDERS.put("suffix", player -> plugin.getChat().getPlayerSuffix(player));
        PLACEHOLDERS.put("world", player -> player.getWorld().getName());
        // TODO: make this accessible through configuration
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        FormatResult result = format(event.getPlayer(), event.getMessage());
        event.setFormat(result.getFormat());
        event.setMessage(result.getMessage());
    }

    public FormatResult format(Player player, String message) {
        final String[] format = { plugin.getConfig().getString("Format") };
        if (format[0] == null) {
            plugin.getLogger().severe("The returned format was null! Please fix this by providing an entry " +
                    "\"Format\" in the config file.");
            return null;
        }

        // replace placeholders
        PLACEHOLDERS.forEach((key, handler) -> format[0] = format[0].replace(
                "{" + key + "}",
                handler.apply(player)
        ));
        format[0] = format[0].replace("{name}", "%1$s");
        format[0] = format[0].replace("{message}", "%2$s");
        format[0] = format[0].trim().replaceAll(" +", " "); // trim whitespace
        format[0] = ChatColor.translateAlternateColorCodes('&', format[0]);

        // replace colors in the message
        // TODO: permission per color code?
        boolean needsPermission = plugin.getConfig().getBoolean("NeedsPermissionForColors");
        if (
                !needsPermission ||
                plugin.getPerms().has(player, "simplechatformat.usecolors") ||
                player.isOp()
        ) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        return new FormatResult(format[0], message);
    }
}
