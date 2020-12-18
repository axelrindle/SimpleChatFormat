package de.axelrindle.simplechatformat.event;

import de.axelrindle.simplechatformat.FormatResult;
import de.axelrindle.simplechatformat.SimpleChatFormat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListenerLegacy implements Listener {

    private final ChatListener listener;

    public ChatListenerLegacy(SimpleChatFormat plugin) {
        listener = new ChatListener(plugin);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        FormatResult result = listener.format(event.getPlayer(), event.getMessage());
        event.setFormat(result.getFormat());
        event.setMessage(result.getMessage());
    }
}
