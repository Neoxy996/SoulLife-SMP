<div align="center">

# ⚰️ SoulLife SMP

**A hardcore death mod for Minecraft multiplayer servers.**

Die. Become a ghost. Sacrifice rare items to return to life.  
After 20 deaths, you are trapped as a spectator **forever**.

![Version](https://img.shields.io/badge/version-1.1.0-gold)
![NeoForge](https://img.shields.io/badge/NeoForge-1.21.1-orange)
![Fabric](https://img.shields.io/badge/Fabric-1.20.1-blue)
![Forge](https://img.shields.io/badge/Forge-1.20.1-red)
![Server Side](https://img.shields.io/badge/Side-Server--Side-purple)
![License](https://img.shields.io/badge/license-MIT-green)

</div>

---

## About

SoulLife SMP is a hardcore survival mod designed for Minecraft multiplayer servers. When you die, you become a ghost trapped in Spectator mode wearing white leather armor. To return to life, you must sacrifice a specific item from your inventory. Each death requires a progressively rarer and more valuable item. After your 20th death, your soul is imprisoned forever as a permanent spectator.

---

## How It Works

When you die:
- You enter **Spectator mode** as a ghost
- You receive **White Leather Armor** with Binding Curse and Unbreaking III
- You get **Speed I** and **Glowing** effects permanently
- The required **sacrifice item** is shown in the sidebar
- Everyone sees a **red message** that you need help

To return to life:
- Collect the required **sacrifice item**
- Hold it in your inventory
- It is **automatically consumed**
- **Totem particles and sound** play
- A **green message** broadcasts your revival
- You return to **Survival mode**

---

## Sacrifice Items (Death 1 to 20)

| Death | Item | Death | Item |
|:-----:|------|:-----:|------|
| 1 | Iron Ingot | 11 | Netherite Scrap |
| 2 | Gold Ingot | 12 | Netherite Ingot |
| 3 | Emerald | 13 | Totem of Undying |
| 4 | Diamond | 14 | Netherite Upgrade Template |
| 5 | Golden Apple | 15 | Wither Skeleton Skull |
| 6 | Iron Block | 16 | **Nether Star** |
| 7 | Gold Block | 17 | Enchanted Golden Apple |
| 8 | Emerald Block | 18 | Netherite Block |
| 9 | Diamond Block | 19 | Beacon |
| 10 | End Crystal | 20 | **Dragon Egg** (Permanent Spectator) |

**Death 20 is final.** There is no coming back.

---

## Commands

### Player Commands

| Command | Description |
|---------|-------------|
| `/soullife check` | Shows your current death count |
| `/soullife next` | Shows the next item you must sacrifice |
| `/soullife info` | Shows mod information |
| `/soullife gui` | Lists all 20 sacrifice items in chat |
| `/soullife language` | Change your language |
| `/soullife language list` | View all supported languages |
| `/soullife language <code>` | Change to a specific language |

### Admin Commands (OP Level 2)

| Command | Description |
|---------|-------------|
| `/soullife edititem <death#> <item>` | Change a sacrifice item |
| `/soullife reset` | Reset all items to default |
| `/soullife add <player> <amount>` | Add deaths to a player |
| `/soullife remove <player> <amount>` | Remove deaths from a player |
| `/soullife set <player> <amount>` | Set a player's death count |

All commands support **Tab autocomplete**.

---

## Languages

The mod automatically detects your Minecraft language setting. You can override it with `/soullife language <code>`.

| Language | Code |
|----------|------|
| English | `en_us` |
| العربية | `ar_sa` |
| Français | `fr_fr` |
| Español | `es_es` |
| Português | `pt_br` |

---

## Crafting Recipe

### Dragon Egg Crafting

```
      End Crystal
Crying Obsidian | Nether Star | Crying Obsidian
Netherite Ingot | Netherite Ingot | Netherite Ingot
Netherite Ingot | Netherite Ingot | Netherite Ingot
         ↓
     Dragon Egg
```

---

## Installation

1. Download the correct JAR for your mod loader from [Releases](../../releases)
   - `SoulLife-SMP-neoforge-1.21.1.jar` → NeoForge 1.21.1
   - `SoulLife-SMP-fabric-1.20.1.jar` → Fabric 1.20.1
   - `SoulLife-SMP-forge-1.20.1.jar` → Forge 1.20.1

2. Place the JAR in your server's `mods/` folder

3. Start your server

**Fabric users:** Also install [Fabric API](https://modrinth.com/mod/fabric-api)

---

## Features

✅ **20-Death Progression System**  
✅ **Ghost State** (Spectator mode with effects)  
✅ **White Leather Armor** with Binding Curse  
✅ **Speed I + Glowing** effects  
✅ **Wither Sound** on death  
✅ **Totem Particles & Sound** on revival  
✅ **Permanent Spectator** after death 20  
✅ **5 Languages** (with per-player selection)  
✅ **Customizable Items** (via `/soullife edititem`)  
✅ **Dragon Egg Recipe** (craftable with rare items)  
✅ **Server-Side Only** (no client installation needed)  
✅ **Multi-Loader Support** (NeoForge, Fabric, Forge)

---

## Supported Versions

| Loader | Version |
|--------|---------|
| NeoForge | 1.21.1 |
| Fabric | 1.20.1 |
| Forge | 1.20.1 |

---

## Building from Source

Requirements: JDK 21 or higher

```bash
# Build NeoForge
./gradlew :neoforge-1.21.1:build

# Build Fabric
./gradlew :fabric-1.20.1:build

# Build Forge
./gradlew :forge-1.20.1:build
```

Output JARs in `<loader>/build/libs/`

---

## Project Structure

```
SoulLife-SMP/
├── common/                   Shared code
├── neoforge-1.21.1/         NeoForge 1.21.1
├── fabric-1.20.1/           Fabric 1.20.1
├── forge-1.20.1/            Forge 1.20.1
├── .github/workflows/        GitHub Actions CI
└── docs/                     Documentation
```

---

## License

MIT License — Free to use, modify, and distribute. See [LICENSE](LICENSE) for details.

---

<div align="center">

Made with ❤️ by the SoulLife Team

</div>
