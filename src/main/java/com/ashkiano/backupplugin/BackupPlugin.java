package com.ashkiano.backupplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

//TODO udělat konfigurovatelnost
//TODO udělat async
public class BackupPlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        this.getCommand("backupConfigs").setExecutor(this);
        Metrics metrics = new Metrics(this, 22160);
        this.getLogger().info("Thank you for using the BackupPlugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("backupConfigs")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("backupplugin.backup")) {
                    player.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            }

            File pluginsFolder = new File("plugins");
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File backupFolder = new File(pluginsFolder, "backups/" + timestamp);

            if (!backupFolder.exists() && !backupFolder.mkdirs()) {
                getLogger().log(Level.SEVERE, "Could not create backup directory!");
                return true;
            }

            for (File pluginFolder : pluginsFolder.listFiles()) {
                if (pluginFolder.isDirectory()) {
                    File configFile = new File(pluginFolder, "config.yml");
                    if (configFile.exists()) {
                        try {
                            Files.copy(configFile.toPath(), new File(backupFolder, pluginFolder.getName() + "-config.yml").toPath());
                        } catch (IOException e) {
                            getLogger().log(Level.SEVERE, "Failed to backup config for " + pluginFolder.getName(), e);
                        }
                    }
                }
            }
            sender.sendMessage("Configs have been backed up to " + backupFolder.getPath());
            return true;
        }
        return false;
    }
}