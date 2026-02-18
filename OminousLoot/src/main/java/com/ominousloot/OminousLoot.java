package com.ominousloot;

import com.ominousloot.commands.ReloadCommand;
import com.ominousloot.listeners.VaultListener;
import org.bukkit.plugin.java.JavaPlugin;

public class OminousLoot extends JavaPlugin {
    
    private static OminousLoot instance;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize config manager
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new VaultListener(this), this);
        
        // Register commands
        ReloadCommand reloadCommand = new ReloadCommand(this);
        getCommand("ominousloot").setExecutor(reloadCommand);
        getCommand("ominousloot").setTabCompleter(reloadCommand);
        
        getLogger().info("OminousLoot has been enabled!");
        getLogger().info("Folia-compatible mode active");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("OminousLoot has been disabled!");
    }
    
    public static OminousLoot getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        configManager.loadConfig();
        getLogger().info("Configuration reloaded!");
    }
}
