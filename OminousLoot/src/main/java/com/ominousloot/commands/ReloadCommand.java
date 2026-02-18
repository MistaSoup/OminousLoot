package com.ominousloot.commands;

import com.ominousloot.OminousLoot;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {
    
    private final OminousLoot plugin;
    
    public ReloadCommand(OminousLoot plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /ominousloot reload");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            // Check permission
            if (!sender.hasPermission("ominousloot.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload this plugin.");
                return true;
            }
            
            // Reload the configuration
            try {
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.GREEN + "OminousLoot configuration reloaded successfully!");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
                plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        
        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /ominousloot reload");
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("ominousloot.reload")) {
                completions.add("reload");
            }
        }
        
        return completions;
    }
}
