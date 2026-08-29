<div align="center">

# ⚰️ SoulLife

**Die. Become a Ghost. Sacrifice. Survive.**

![Version](https://img.shields.io/badge/version-1.0.0-gold)
![MC](https://img.shields.io/badge/Minecraft-1.20.1-green)
![NeoForge](https://img.shields.io/badge/NeoForge-1.20.1-orange)
![Fabric](https://img.shields.io/badge/Fabric-1.20.1-blue)
![Forge](https://img.shields.io/badge/Forge-1.20.1-red)
![Server Side](https://img.shields.io/badge/Side-Server--Side-purple)
![License](https://img.shields.io/badge/license-MIT-green)

</div>

---

## About

SoulLife is a hardcore survival mod. When you die, you become a ghost wearing white leather armor and enter Spectator mode. To return to life, you must sacrifice a specific item from your inventory. Each death requires a rarer and more valuable item. After **20 deaths**, you become a permanent spectator — forever.

---

## How It Works

When you die:
- You enter **Spectator mode** as a ghost
- You receive **White Leather Armor** with Binding Curse and Unbreaking III (cannot be removed)
- You receive **Speed I** and **Glowing** effects permanently
- A **Wither Boss sound** plays (only you hear it)
- The **Sidebar** shows the item you must sacrifice
- A **red message** broadcasts to all players that you need help

To return to life:
- Place the required sacrifice item in your inventory
- It will be consumed automatically
- **Totem particles and sound** play on revival
- A **green message** broadcasts your revival to all players
- Your armor and effects are removed
- You return to **Survival mode**

---

## Sacrifice Items (Death 1 to 20)

| Death | Item Required |
|:-----:|:-------------|
| 1 | Iron Ingot |
| 2 | Gold Ingot |
| 3 | Emerald |
| 4 | Diamond |
| 5 | Golden Apple |
| 6 | Iron Block |
| 7 | Gold Block |
| 8 | Emerald Block |
| 9 | Diamond Block |
| 10 | End Crystal |
| 11 | Netherite Scrap |
| 12 | Netherite Ingot |
| 13 | Totem of Undying |
| 14 | Netherite Upgrade Smithing Template |
| 15 | Wither Skeleton Skull |
| 16 | Nether Star |
| 17 | Enchanted Golden Apple |
| 18 | Netherite Block |
| 19 | Beacon |
| 20 | **Dragon Egg — Permanent Spectator Forever** |

After the 20th death, there is no way back. You are a spectator for the rest of the world's lifetime unless an admin resets your deaths.

---

## Commands

### Player Commands

| Command | Description |
|---------|-------------|
| `/soullife check` | Shows your current death count |
| `/soullife next` | Shows the next item you must sacrifice |
| `/soullife info` | Shows information about the mod |
| `/soullife gui` | Lists all 20 sacrifice items in chat with your progress |

### Admin Commands (OP Level 2)

| Command | Description |
|---------|-------------|
| `/soullife edititem <death1-20> <item>` | Change the sacrifice item for a specific death |
| `/soullife reset` | Reset all sacrifice items back to default |
| `/soullife add <player> <amount>` | Add deaths to a player |
| `/soullife remove <player> <amount>` | Remove deaths from a player |
| `/soullife set <player> <amount>` | Set a player's death count to a specific number |

All admin commands support **Tab autocomplete** for player names, death slots, and item IDs including modded items and blocks.

Setting a player's deaths below 20 with `/soullife set` or `/soullife remove` will automatically revive them from permanent spectator if applicable.

---

## Languages

The mod automatically uses your Minecraft language setting. Supported languages:

| Language | Code |
|----------|------|
| English | `en_us` |
| Arabic | `ar_sa` |
| French | `fr_fr` |
| Spanish | `es_es` |
| Portuguese (Brazil) | `pt_br` |

---

## Installation

1. Download the correct JAR file for your mod loader from the [Releases](../../releases) page
2. Place the JAR in your server's `mods/` folder
3. Start the server

This mod is **server-side only**. Players do not need to install anything on their client.

> Fabric users also need [Fabric API](https://modrinth.com/mod/fabric-api) installed on the server.

---

## Supported Versions

| Loader | Version |
|--------|---------|
| NeoForge | 1.20.1 |
| Fabric | 1.20.1 |
| Forge | 1.20.1 |

---

## Building from Source

Requirements: JDK 17 or higher

```bash
git clone https://github.com/YourUsername/SoulLife.git
cd SoulLife

# Build NeoForge
./gradlew :neoforge-1.20.1:build

# Build Fabric
./gradlew :fabric-1.20.1:build

# Build Forge
./gradlew :forge-1.20.1:build
```

Output JARs are located in `<loader>/build/libs/`

---

## Project Structure

```
SoulLife/
├── common/                  Shared code for all loaders
│   └── src/main/java/com/soullife/
│       ├── manager/         DeathManager, GhostManager, SacrificeManager, Commands
│       ├── data/            PlayerData
│       └── util/            MessageUtil, ScoreboardManager
├── neoforge-1.20.1/         NeoForge platform code
├── fabric-1.20.1/           Fabric platform code
├── forge-1.20.1/            Forge platform code
└── .github/workflows/       GitHub Actions CI build
```

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contributing

Pull requests are welcome. For major changes please open an issue first to discuss what you would like to change.

---

<div align="center">
Made with ❤️ by the SoulLife Team
</div>
