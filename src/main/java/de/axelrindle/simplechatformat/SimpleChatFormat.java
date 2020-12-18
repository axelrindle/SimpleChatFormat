package de.axelrindle.simplechatformat;

import de.axelrindle.simplechatformat.command.MainCommand;
import de.axelrindle.simplechatformat.event.ChatListener;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public final class SimpleChatFormat extends JavaPlugin {

    public static final String PREFIX = "§7[§6SimpleChatFormat§7]§r";

    private Permission perms;
    private Chat chat;

    @Override
    public void onEnable() {
        // integrate with Vault
        if (!setupChat() || !setupPermissions()) {
            getLogger().severe("Failed to load Vault integrations! Is is installed?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // TODO: use PocketKnife for future updates
        // create data directory
        if (!getDataFolder().exists()) {
            try {
                Files.createDirectory(getDataFolder().toPath());
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe("Failed to create a data directory at " + getDataFolder().getAbsolutePath() + "!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        // load config
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            try (InputStream defConfig = getResource("config.yml")) {
                Files.copy(Objects.requireNonNull(defConfig), config.toPath());
                getLogger().info("Created a new configuration file at " + config.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe("Failed to create a new configuration file!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        // register commands
        PluginCommand command = getCommand("simplechatformat");
        MainCommand mainCommand = new MainCommand(this);
        Objects.requireNonNull(command);
        command.setExecutor(mainCommand);
        command.setTabCompleter(mainCommand);

        // register event listeners
        try {
            Class.forName("org.bukkit.event.player.AsyncPlayerChatEvent");
            getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        } catch (ClassNotFoundException e) {
            getLogger().warning("Unable to find class AsyncPlayerChatEvent! Falling back to legacy event.");
        }

        getLogger().info(String.format("%s v%s initialized.", getDescription().getName(), getDescription().getVersion()));
    }

    public Chat getChat() {
        return chat;
    }

    public Permission getPerms() {
        return perms;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;
        perms = rsp.getProvider();
        return true;
    }
}
