# OminousLoot Plugin

A Folia-compatible Minecraft plugin for Minecraft 1.21.x that enhances loot drops from ominous trial vaults.

## Features

- **Folia Compatible**: Uses Folia's region-based scheduler for thread-safe operation
- **Configurable Loot Enhancement**: Add extra loot drops from ominous trial vaults
- **Wind Burst Books**: Spawn Wind Burst enchanted books (levels 1-3)
- **Customizable Probabilities**: Each item has its own enable/disable toggle and spawn probability
- **Random Timing**: Items spawn at random times within a configurable delay range
- **Permission Based**: Control who can benefit from the enhanced loot

## Compilation

### Requirements
- Java 21 or higher
- Maven 3.6 or higher

### Build Instructions

1. Navigate to the plugin directory:
```bash
cd /path/to/OminousLoot
```

2. Compile with Maven:
```bash
mvn clean package
```

3. The compiled JAR will be in the `target` folder:
```
target/OminousLoot-1.0.0.jar
```

## Installation

1. Download or compile the plugin JAR
2. Place `OminousLoot-1.0.0.jar` in your server's `plugins` folder
3. Start or restart your server
4. Configure the plugin by editing `plugins/OminousLoot/config.yml`
5. Reload the configuration with `/ominousloot reload`

## Configuration

The `config.yml` file contains all configurable options:

### Global Settings
- `enabled`: Enable or disable the entire plugin (true/false)
- `spawn-delay.min`: Minimum delay in seconds before spawning extra loot
- `spawn-delay.max`: Maximum delay in seconds before spawning extra loot

### Loot Items
Each item in the `loot-items` section has:
- `enabled`: Whether this item can be selected (true/false)
- `probability`: Chance (0-100) that the item spawns after being randomly selected
- `min-amount`: Minimum stack size
- `max-amount`: Maximum stack size

## How It Works

1. When a player opens an ominous trial vault, the plugin detects it
2. One item is randomly selected from all enabled items
3. The selected item's probability is checked
4. If successful, the item spawns on top of the vault after a random delay
5. Only one extra item can spawn per vault opening

## Permissions

- `ominousloot.use`: Allows the plugin to work for this player (default: true)
- `ominousloot.reload`: Allows reloading the plugin configuration (default: op)

## Commands

- `/ominousloot reload` - Reload the plugin configuration
- Aliases: `/ol`, `/oloot`

## Supported Items

The plugin includes all items from the ominous trial vault loot table:
- Wind Burst Enchanted Books (levels 1-3)
- Ominous Bottles
- Emeralds
- Diamond Blocks
- Enchanted Golden Apples
- Heavy Core
- Flow & Bolt Armor Trim Smithing Templates
- Golden Apples
- Damaged Diamond Equipment
- Experience Bottles
- Food items
- Iron Ingots
- Diamonds
- And more!

## Example Configuration

```yaml
enabled: true

spawn-delay:
  min: 1.0
  max: 5.0

loot-items:
  wind_burst_book:
    enabled: true
    probability: 50.0
    min-amount: 1
    max-amount: 1
  
  emerald:
    enabled: true
    probability: 60.0
    min-amount: 2
    max-amount: 8
```

## Folia Compatibility

This plugin is designed specifically for Folia and uses region-based scheduling. It will work on Paper servers as well, but is optimized for Folia's multi-threaded architecture.

## Support

For issues, questions, or suggestions, please create an issue on the project repository.

## License

This plugin is provided as-is. Feel free to modify and distribute.
