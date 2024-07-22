package org.givinghawk.antislur;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public final class Antislur extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Logger log = Bukkit.getLogger();
        String version = getDescription().getVersion();
        log.info("[AntiSlur] Welcome! AntiSlur V" + version + " starting up...");

        // Create default config if it doesn't exist
        createDefaultConfig();

        // Register the chat, book, and sign listeners
        getServer().getPluginManager().registerEvents(this, this);

        log.info("[AntiSlur] AntiSlur V" + version + " successfully started and enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            saveResource("config.yml", false);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String playerName = event.getPlayer().getName();

        // Load the banned words and command from config
        FileConfiguration config = getConfig();
        List<String> bannedWords = config.getStringList("banned-words");
        String command = config.getString("command");

        // Check for banned words
        for (String bannedWord : bannedWords) {
            if (message.toLowerCase().contains(bannedWord.toLowerCase())) {
                // Cancel the event to prevent the message from being sent
                event.setCancelled(true);

                // Execute the command, replacing {player} with the player's name, on the main thread
                String executedCommand = command.replace("{player}", playerName);
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executedCommand));
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        String playerName = event.getPlayer().getName();
        List<String> pages = event.getNewBookMeta().getPages();

        // Load the banned words and command from config
        FileConfiguration config = getConfig();
        List<String> bannedWords = config.getStringList("banned-words");
        String command = config.getString("command");

        // Check each page for banned words
        for (String page : pages) {
            for (String bannedWord : bannedWords) {
                if (page.toLowerCase().contains(bannedWord.toLowerCase())) {
                    // Cancel the event to prevent the book from being edited
                    event.setCancelled(true);

                    // Execute the command, replacing {player} with the player's name, on the main thread
                    String executedCommand = command.replace("{player}", playerName);
                    Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executedCommand));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String playerName = event.getPlayer().getName();
        String[] lines = event.getLines();

        // Load the banned words and command from config
        FileConfiguration config = getConfig();
        List<String> bannedWords = config.getStringList("banned-words");
        String command = config.getString("command");

        // Check each line of the sign for banned words
        for (String line : lines) {
            for (String bannedWord : bannedWords) {
                if (line.toLowerCase().contains(bannedWord.toLowerCase())) {
                    // Cancel the event to prevent the sign from being changed
                    event.setCancelled(true);

                    // Execute the command, replacing {player} with the player's name, on the main thread
                    String executedCommand = command.replace("{player}", playerName);
                    Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executedCommand));
                    return;
                }
            }
        }
    }
}
