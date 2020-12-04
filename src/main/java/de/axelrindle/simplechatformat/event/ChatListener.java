package de.axelrindle.simplechatformat.event;

import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class ChatListener implements Listener {

    private static final char ALTERNATE_COLOR_CHAR = '&';
    private final SimpleChatFormat plugin;

    public ChatListener(SimpleChatFormat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = plugin.getChat().getPlayerPrefix(player);
        String suffix = plugin.getChat().getPlayerSuffix(player);

        String format = plugin.getConfig().getString("Format");
        Objects.requireNonNull(format);
        format = format.replace("{prefix}", prefix);
        format = format.replace("{suffix}", suffix);
        format = format.replace("{name}", "%1$s");
        format = format.replace("{message}", "%2$s");
        format = format.trim().replaceAll(" +", " ");
        format = ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CHAR, format);
        event.setFormat(format);

        String msg = event.getMessage();
        boolean needsPermission = plugin.getConfig().getBoolean("NeedsPermissionForColors");
        if (
                !needsPermission ||
                plugin.getPerms().has(player, "simplechatformat.usecolors") ||
                player.isOp()
        ) {
            msg = ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CHAR, event.getMessage());
        }
        event.setMessage(msg);
    }
}
