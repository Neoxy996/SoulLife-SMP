# SoulLife Development Guide

## Project Overview

SoulLife is a hardcore death mod for Minecraft 1.20.1 with support for multiple loaders:
- NeoForge
- Fabric
- Forge

The mod uses a **common code approach** where shared logic is in `common/` and platform-specific code is in loader folders.

---

## Project Structure

```
SoulLife/
├── common/                          Shared code for all loaders
│   ├── build.gradle                 Empty wrapper (sources included in loaders)
│   └── src/main/
│       ├── java/com/soullife/
│       │   ├── manager/             Core managers
│       │   │   ├── DeathManager.java
│       │   │   ├── GhostManager.java
│       │   │   ├── SacrificeManager.java
│       │   │   ├── CommonEvents.java
│       │   │   └── SoulLifeCommands.java
│       │   ├── data/
│       │   │   └── PlayerData.java
│       │   └── util/
│       │       ├── MessageUtil.java
│       │       └── ScoreboardManager.java
│       └── resources/
│           ├── assets/soullife/
│           │   ├── soullife.png     (mod icon)
│           │   └── lang/            (5 languages)
│           └── pack.mcmeta
│
├── neoforge-1.20.1/
│   ├── build.gradle
│   └── src/main/java/com/soullife/neoforge/
│       ├── SoulLifeMod.java
│       └── NeoForgeEvents.java
│
├── fabric-1.20.1/
│   ├── build.gradle
│   └── src/main/java/com/soullife/fabric/
│       ├── SoulLifeFabric.java
│       └── FabricEvents.java
│
├── forge-1.20.1/
│   ├── build.gradle
│   └── src/main/java/com/soullife/forge/
│       ├── SoulLifeForge.java
│       └── ForgeEvents.java
│
├── gradle/                          Gradle wrapper
├── .github/workflows/
│   └── build.yml                    GitHub Actions CI
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat
├── README.md
├── LICENSE
├── CHANGELOG.md
├── CONTRIBUTING.md
└── DEVELOPMENT.md (this file)
```

---

## Architecture

### Common Module (`common/`)

Contains all platform-agnostic logic:

**DeathManager.java** (188 lines)
- Death count tracking per player
- Default sacrifice items (20 items)
- Current editable sacrifice items
- NBT save/load for persistence

**GhostManager.java** (154 lines)
- Apply/remove ghost state
- White leather armor with Binding Curse + Unbreaking III
- Speed I + Glowing effects
- Wither sound (private to player)
- Totem particles + sound on revival

**SacrificeManager.java** (56 lines)
- Check if player has required item
- Remove item from inventory
- Get required item for current death count

**CommonEvents.java** (95 lines)
- Player death handler
- Player respawn handler
- Player login handler
- Item pickup handler
- Block break/place prevention

**SoulLifeCommands.java** (588 lines)
- `/soullife check` - show death count
- `/soullife next` - show next item
- `/soullife info` - mod info
- `/soullife gui` - list all 20 items
- `/soullife edititem <death#> <item>` - change item (admin)
- `/soullife reset` - reset to defaults (admin)
- `/soullife add <player> <amount>` - add deaths (admin)
- `/soullife remove <player> <amount>` - remove deaths (admin)
- `/soullife set <player> <amount>` - set deaths (admin)
- Full autocomplete for all arguments

**MessageUtil.java** (178 lines)
- Death messages (Actionbar, Chat, Sidebar)
- Revival messages
- Command feedback messages
- 5 language support via translation keys

**ScoreboardManager.java** (62 lines)
- Tab display for death count (yellow)
- Sidebar display for sacrifice item (white)
- Show/hide sacrifice bar

**PlayerData.java** (18 lines)
- Simple data holder
- Death count
- Ghost state flag
- Permanent spectator flag

---

### Platform Modules

#### NeoForge (`neoforge-1.20.1/`)

**SoulLifeMod.java**
- Main mod entry point
- Registers events and commands

**NeoForgeEvents.java**
- Implements NeoForge-specific events:
  - `LivingDeathEvent` → `CommonEvents.onPlayerDeath()`
  - `PlayerEvent.PlayerRespawnEvent` → `CommonEvents.onPlayerRespawn()`
  - `PlayerEvent.PlayerLoggedInEvent` → `CommonEvents.onPlayerLogin()`
  - `EntityItemPickupEvent` → `CommonEvents.onItemPickup()`
  - `BlockEvent.BreakEvent` → `CommonEvents.onBlockBreak()`
  - `BlockEvent.EntityPlaceEvent` → `CommonEvents.onBlockPlace()`
  - `PlayerEvent.SaveToFile` → NBT save
  - `PlayerEvent.LoadFromFile` → NBT load

#### Fabric (`fabric-1.20.1/`)

**SoulLifeFabric.java**
- Main mod initializer
- Registers commands via `CommandRegistrationCallback`
- Registers all event listeners

**FabricEvents.java**
- Implements Fabric-specific callbacks:
  - `ServerLivingEntityEvents.ALLOW_DEATH` → death handler
  - `ServerPlayerEvents.AFTER_RESPAWN` → respawn handler
  - `ServerPlayConnectionEvents.JOIN` → login handler
  - `ServerPlayConnectionEvents.DISCONNECT` → save on logout

#### Forge (`forge-1.20.1/`)

**SoulLifeForge.java**
- Main mod entry point
- Registers events

**ForgeEvents.java**
- Same as NeoForge (compatible APIs)

---

## Data Flow

### On Player Death

1. `NeoForgeEvents.onPlayerDeath()` triggered
2. Calls `CommonEvents.onPlayerDeath(player)`
3. Increment death count via `DeathManager.addDeaths()`
4. Check if death >= 20:
   - YES: Set permanent spectator, apply ghost, show dramatic message
   - NO: Apply ghost state normally
5. Get required item via `SacrificeManager.getRequiredItem()`
6. Send messages via `MessageUtil.sendDeathMessages()`
7. Show sidebar via `ScoreboardManager.showSacrificeBar()`
8. Update tab display via `ScoreboardManager.updateTabDisplay()`

### On Player Pickup Item

1. `NeoForgeEvents.onItemPickup()` triggered
2. Check if player is ghost via `DeathManager.isGhost()`
3. Get required item via `SacrificeManager.getRequiredItem()`
4. If item matches:
   - Call `SacrificeManager.trySacrifice(player)`
   - Remove ghost state via `GhostManager.removeGhostState()`
   - Play totem effect
   - Broadcast revival message
   - Hide sidebar

### On Admin Command

Example: `/soullife add PlayerName 5`

1. `SoulLifeCommands.executeAdd()` called
2. Find player via `findPlayer()`
3. Add deaths via `DeathManager.addDeaths(5)`
4. Check if >= 20 via `handleSpectatorCheck()`
5. Update tab display
6. Send confirmation messages

---

## Building

### Prerequisites
- JDK 17+
- Gradle 8.4 (included via gradlew)

### Build Individual Loaders

```bash
./gradlew :neoforge-1.20.1:build
./gradlew :fabric-1.20.1:build
./gradlew :forge-1.20.1:build
```

Output JAR files in `<loader>/build/libs/`

### Run Gradle Tasks

```bash
./gradlew help               # List available tasks
./gradlew clean             # Clean build artifacts
./gradlew build             # Build all subprojects
```

---

## Languages

Supported languages (via Minecraft's translation system):

| Language | File | Code |
|----------|------|------|
| English | `en_us.json` | `en_us` |
| Arabic | `ar_sa.json` | `ar_sa` |
| French | `fr_fr.json` | `fr_fr` |
| Spanish | `es_es.json` | `es_es` |
| Portuguese (Brazil) | `pt_br.json` | `pt_br` |

Translation keys all start with `soullife.` and are grouped by purpose:
- `soullife.death.*` - death messages
- `soullife.revive.*` - revival messages
- `soullife.cmd.*` - command feedback

---

## Configuration

Currently, the mod has no external config file. All settings are:
1. Hardcoded (20 sacrifice items)
2. Changeable via `/soullife edititem` commands
3. Stored in player NBT data (when server saves)

Future enhancement: Add external JSON config file.

---

## Permissions

All commands check permission levels:

| Level | Needed For |
|-------|-----------|
| 0 | `/soullife check`, `next`, `info`, `gui` |
| 2 | `/soullife edititem`, `reset`, `add`, `remove`, `set` |

Set via server operators or permissions plugin (LuckPerms, etc).

---

## Adding New Features

### Add a New Command

1. Add method in `SoulLifeCommands.java`:
   ```java
   private static int executeMyCommand(CommandContext<CommandSourceStack> ctx) { ... }
   ```

2. Register in `register()` method:
   ```java
   root.then(Commands.literal("mycommand")
       .requires(src -> src.hasPermission(0))
       .executes(ctx -> executeMyCommand(ctx))
   );
   ```

3. Add translation keys to all 5 `lang/*.json` files

### Add a New Event

1. Add method in `CommonEvents.java`:
   ```java
   public static void onMyEvent(ServerPlayer player) { ... }
   ```

2. Call from platform event in NeoForge/Fabric/Forge events:
   ```java
   @SubscribeEvent
   public static void onMyNeoForgeEvent(MyEvent event) {
       CommonEvents.onMyEvent(player);
   }
   ```

### Add a New Language

1. Create `common/src/main/resources/assets/soullife/lang/xx_yy.json`
2. Copy all keys from `en_us.json`
3. Translate all values
4. Test with `/soullife check` etc

---

## Testing Locally

### NeoForge

```bash
./gradlew :neoforge-1.20.1:runServer
```

Then connect with Minecraft 1.20.1 to `localhost`.

### Fabric

Requires setting up a local Fabric environment (manual setup, not included).

---

## GitHub Actions CI

The `.github/workflows/build.yml` automatically:
1. Builds NeoForge 1.20.1 on every push
2. Builds Fabric 1.20.1 on every push
3. Builds Forge 1.20.1 on every push
4. Uploads JARs as artifacts
5. Uploads to releases on tag push

---

## Common Issues

**Q: "Cannot resolve symbol 'net.minecraft'"**
A: Run `./gradlew clean` and ensure JDK 17+ is installed.

**Q: "Gradle not found"**
A: Use `./gradlew` (Unix) or `gradlew.bat` (Windows), not `gradle`.

**Q: "Event not firing"**
A: Check if event is registered in platform module. Events in `CommonEvents.java` do nothing until called from platform event.

---

## Future Versions

Planned features:
- Config file for sacrifice items (JSON)
- 1.21.1 NeoForge + Fabric + Forge
- Tags system for modded item compatibility
- Per-player difficulty settings
- Custom death messages per player

---

## License

MIT License - See LICENSE file

---

## Support

Open issues on GitHub or contact the SoulLife Team.
