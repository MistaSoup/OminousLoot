package com.ominousloot;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {
    
    private final OminousLoot plugin;
    private boolean enabled;
    private double minSpawnDelay;
    private double maxSpawnDelay;
    private double spawnHeightOffset;
    private final Map<Material, LootItem> lootItems;
    
    public ConfigManager(OminousLoot plugin) {
        this.plugin = plugin;
        this.lootItems = new HashMap<>();
    }
    
    public void loadConfig() {
        enabled = plugin.getConfig().getBoolean("enabled", true);
        minSpawnDelay = plugin.getConfig().getDouble("spawn-delay.min", 1.0);
        maxSpawnDelay = plugin.getConfig().getDouble("spawn-delay.max", 5.0);
        spawnHeightOffset = plugin.getConfig().getDouble("spawn-height-offset", 2.0);
        
        lootItems.clear();
        
        ConfigurationSection lootSection = plugin.getConfig().getConfigurationSection("loot-items");
        if (lootSection != null) {
            for (String key : lootSection.getKeys(false)) {
                ConfigurationSection itemSection = lootSection.getConfigurationSection(key);
                if (itemSection != null) {
                    boolean itemEnabled = itemSection.getBoolean("enabled", false);
                    double probability = itemSection.getDouble("probability", 50.0);
                    int minAmount = itemSection.getInt("min-amount", 1);
                    int maxAmount = itemSection.getInt("max-amount", 1);
                    
                    // Convert key to material
                    Material material = parseMaterial(key);
                    if (material != null && itemEnabled) {
                        lootItems.put(material, new LootItem(material, probability, minAmount, maxAmount));
                    }
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + lootItems.size() + " enabled loot items");
    }
    
    private Material parseMaterial(String key) {
        // Handle special case for wind burst book
        if (key.equalsIgnoreCase("wind_burst_book")) {
            return Material.ENCHANTED_BOOK;
        }
        
        // Handle damaged items
        if (key.startsWith("damaged_")) {
            String materialName = key.substring(8).toUpperCase();
            try {
                return Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown material: " + materialName);
                return null;
            }
        }
        
        // Try to parse as normal material
        try {
            return Material.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Unknown material: " + key);
            return null;
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public double getMinSpawnDelay() {
        return minSpawnDelay;
    }
    
    public double getMaxSpawnDelay() {
        return maxSpawnDelay;
    }
    
    public double getSpawnHeightOffset() {
        return spawnHeightOffset;
    }
    
    public Map<Material, LootItem> getLootItems() {
        return new HashMap<>(lootItems);
    }
    
    public List<LootItem> getEnabledLootItems() {
        return new ArrayList<>(lootItems.values());
    }
    
    public static class LootItem {
        private final Material material;
        private final double probability;
        private final int minAmount;
        private final int maxAmount;
        
        public LootItem(Material material, double probability, int minAmount, int maxAmount) {
            this.material = material;
            this.probability = probability;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }
        
        public Material getMaterial() {
            return material;
        }
        
        public double getProbability() {
            return probability;
        }
        
        public int getMinAmount() {
            return minAmount;
        }
        
        public int getMaxAmount() {
            return maxAmount;
        }
        
        public boolean isWindBurstBook() {
            return material == Material.ENCHANTED_BOOK;
        }
    }
}
