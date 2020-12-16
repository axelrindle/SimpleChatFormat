package de.axelrindle.simplechatformat.v1_16_R3;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final Chat chat;
    private final Permission permission;

    public ChatListener(JavaPlugin plugin, Chat chat, Permission permission) {
        this.plugin = plugin;
        this.chat = chat;
        this.permission = permission;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = chat.getPlayerPrefix(player);
        String suffix = chat.getPlayerSuffix(player);

        String format = plugin.getConfig().getString("Format");
        if (format == null) {
            plugin.getLogger().severe("The returned format was null! Please fix this by providing an entry " +
                    "\"Format\" in the config file.");
            return;
        }
        format = format.replace("{prefix}", prefix);
        format = format.replace("{suffix}", suffix);
        format = format.replace("{name}", "%1$s");
        format = format.replace("{message}", "%2$s");
        format = format.trim().replaceAll(" +", " ");
        format = ChatColor.translateAlternateColorCodes('&', format);
        event.setFormat(format);

        String msg = event.getMessage();
        boolean needsPermission = plugin.getConfig().getBoolean("NeedsPermissionForColors");
        if (
                !needsPermission ||
                        permission.has(player, "simplechatformat.usecolors") ||
                        player.isOp()
        ) {
            msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        }
        event.setMessage(msg);
    }
}
