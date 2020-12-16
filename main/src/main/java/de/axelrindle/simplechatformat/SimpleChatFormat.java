package de.axelrindle.simplechatformat;

import de.axelrindle.simplechatformat.command.MainCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
        String[] pkg = getServer().getClass().getPackage().getName().split("\\.");
        String serverVersion = pkg[pkg.length - 1];
        try {
            Class<?> versionClass = Class.forName("de.axelrindle.simplechatformat." + serverVersion + ".ChatListener");
            Object newInstance = versionClass.getConstructor(JavaPlugin.class, Chat.class, Permission.class)
                    .newInstance(this, chat, perms);
            getServer().getPluginManager().registerEvents((Listener) newInstance, this);
            getLogger().info("Registered listener " + versionClass.getName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            getLogger().severe("The server version " + serverVersion + " is unsupported!");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
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
