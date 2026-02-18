package com.ominousloot.listeners;

import com.ominousloot.ConfigManager;
import com.ominousloot.OminousLoot;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Vault;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VaultListener implements Listener {
    
    private final OminousLoot plugin;
    private final Random random;
    private final java.util.Set<Location> processedVaults;
    
    public VaultListener(OminousLoot plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.processedVaults = new java.util.HashSet<>();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVaultInteract(PlayerInteractEvent event) {
        // Check if plugin is enabled
        if (!plugin.getConfigManager().isEnabled()) {
            return;
        }
        
        // Check if player has permission
        Player player = event.getPlayer();
        if (!player.hasPermission("ominousloot.use")) {
            return;
        }
        
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.VAULT) {
            return;
        }
        
        // Check if it's an ominous vault
        if (!(block.getBlockData() instanceof Vault vault)) {
            return;
        }
        
        if (!vault.isOminous()) {
            return;
        }
        
        // Check if this vault has already been processed
        Location vaultLocation = block.getLocation();
        if (processedVaults.contains(vaultLocation)) {
            return;
        }
        
        // Mark this vault as processed
        processedVaults.add(vaultLocation);
        
        // Schedule cleanup of this vault from the set after 30 seconds
        // This allows the vault to be opened again later
        plugin.getServer().getRegionScheduler().runDelayed(plugin, vaultLocation, scheduledTask -> {
            processedVaults.remove(vaultLocation);
        }, 20L * 30); // 30 seconds
        
        // Schedule the loot spawn using Folia's region scheduler
        scheduleLootSpawn(vaultLocation);
    }
    
    private void scheduleLootSpawn(Location vaultLocation) {
        ConfigManager configManager = plugin.getConfigManager();
        
        // Get random delay
        double minDelay = configManager.getMinSpawnDelay();
        double maxDelay = configManager.getMaxSpawnDelay();
        double delay = minDelay + (random.nextDouble() * (maxDelay - minDelay));
        long delayTicks = (long) (delay * 20); // Convert seconds to ticks
        
        // Get enabled loot items
        List<ConfigManager.LootItem> enabledItems = configManager.getEnabledLootItems();
        if (enabledItems.isEmpty()) {
            return;
        }
        
        // Randomly select one item from enabled items
        ConfigManager.LootItem selectedItem = enabledItems.get(random.nextInt(enabledItems.size()));
        
        // Check probability
        if (random.nextDouble() * 100 > selectedItem.getProbability()) {
            // Failed probability check, no loot spawns
            return;
        }
        
        // Schedule the spawn using Folia's region scheduler
        World world = vaultLocation.getWorld();
        if (world == null) {
            return;
        }
        
        // Use Folia's region scheduler through the server's region scheduler
        plugin.getServer().getRegionScheduler().runDelayed(plugin, vaultLocation, scheduledTask -> {
            spawnLoot(vaultLocation, selectedItem);
        }, delayTicks);
    }
    
    private void spawnLoot(Location location, ConfigManager.LootItem lootItem) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        
        // Calculate random amount
        int amount = lootItem.getMinAmount();
        if (lootItem.getMaxAmount() > lootItem.getMinAmount()) {
            amount += random.nextInt(lootItem.getMaxAmount() - lootItem.getMinAmount() + 1);
        }
        
        // Get the spawn height offset from config
        double heightOffset = plugin.getConfigManager().getSpawnHeightOffset();
        
        // Special handling for Wind Burst books (non-stackable)
        if (lootItem.isWindBurstBook()) {
            // Spawn each book individually since enchanted books don't stack
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = createWindBurstBook();
                Location spawnLocation = location.clone().add(0.5, heightOffset, 0.5);
                world.dropItemNaturally(spawnLocation, itemStack);
            }
            
            plugin.getLogger().info("Spawned extra loot: " + amount + "x " + 
                                   lootItem.getMaterial().name() + " (Wind Burst books) at " + 
                                   location.getBlockX() + ", " + 
                                   (location.getBlockY() + heightOffset) + ", " + 
                                   location.getBlockZ());
            return;
        }
        
        // For regular items, handle amounts larger than max stack size
        Material material = lootItem.getMaterial();
        int maxStackSize = material.getMaxStackSize();
        int remainingAmount = amount;
        int stacksSpawned = 0;
        
        while (remainingAmount > 0) {
            // Create a stack with either the max stack size or remaining amount, whichever is smaller
            int stackAmount = Math.min(remainingAmount, maxStackSize);
            ItemStack itemStack = new ItemStack(material, stackAmount);
            
            // Spawn the item above the vault using the configured height offset
            Location spawnLocation = location.clone().add(0.5, heightOffset, 0.5);
            world.dropItemNaturally(spawnLocation, itemStack);
            
            remainingAmount -= stackAmount;
            stacksSpawned++;
        }
        
        plugin.getLogger().info("Spawned extra loot: " + amount + "x " + 
                               material.name() + " (" + stacksSpawned + " stack(s)) at " + 
                               location.getBlockX() + ", " + 
                               (location.getBlockY() + heightOffset) + ", " + 
                               location.getBlockZ());
    }
    
    private ItemStack createWindBurstBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        
        if (meta != null) {
            // Randomly select Wind Burst level (1, 2, or 3)
            int level = 1 + random.nextInt(3);
            
            // Add Wind Burst enchantment
            meta.addStoredEnchant(Enchantment.WIND_BURST, level, true);
            book.setItemMeta(meta);
        }
        
        return book;
    }
}
