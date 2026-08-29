# SoulLife Installation Guide

## Requirements

- **Minecraft Server** 1.20.1 (Vanilla, Paper, Spigot, etc.)
- **Mod Loader** installed on server:
  - NeoForge 1.20.1
  - Fabric 1.20.1 (+ Fabric API)
  - Forge 1.20.1

---

## Installation Steps

### Step 1: Download

1. Go to [Releases](../../releases)
2. Download the correct JAR for your loader:
   - `SoulLife-neoforge-1.20.1.jar` → NeoForge
   - `SoulLife-fabric-1.20.1.jar` → Fabric
   - `SoulLife-forge-1.20.1.jar` → Forge

### Step 2: Install JAR

1. Locate your server's `mods/` folder
   - If it doesn't exist, create it in the server directory
2. Place the downloaded JAR into `mods/`
3. **Important (Fabric only):** Also install Fabric API:
   - Download from [modrinth.com/mod/fabric-api](https://modrinth.com/mod/fabric-api)
   - Place `fabric-api-*.jar` in `mods/` folder

### Step 3: Start Server

```bash
# On Windows
start_server.bat

# On Linux/Mac
./start_server.sh
# or
java -Xmx30G -Xms30G -jar server.jar nogui
```

Wait for server to fully start and show "Done!" message.

### Step 4: Verify Installation

In-game (as operator):
```
/soullife check
```

Should show your death count. If it works, mod is installed! ✅

---

## Configuration

### Server Properties

No special server.properties changes needed.

### Operators

Grant players permission level 2 for admin commands:

```
/op PlayerName
```

Then players can use:
- `/soullife edititem`
- `/soullife reset`
- `/soullife add/remove/set`

### Fabric Users

**CRITICAL:** Install [Fabric API](https://modrinth.com/mod/fabric-api) alongside SoulLife:

1. Download Fabric API for 1.20.1
2. Place JAR in `mods/` folder alongside `SoulLife-fabric-*.jar`
3. Restart server

Without Fabric API, mod will crash on server startup.

---

## Troubleshooting

### "Mod won't load"

**NeoForge:**
- Check server log for `SoulLife not found in registry`
- Ensure JAR is in `mods/` folder
- Restart server

**Fabric:**
- Check if Fabric API is installed (see Configuration)
- Look for `Fabric Loader` in logs

**Forge:**
- Ensure Forge 1.20.1 is installed
- Check logs for `SoulLife` errors

### "Events not firing"

- Restart server completely (not just `/reload`)
- Check player is in Survival mode (not Creative)

### "Commands don't work"

- Player must have operator permission (level 2)
- Use `/op PlayerName` to grant permission
- Try `/soullife info` first (doesn't need permission)

### "Death counter not saving"

- Server must fully stop/start (not reload)
- Check server has write permission to world folder
- Look for errors in server logs

---

## Multiplayer Setup

### Server-Side Only

Players **do NOT** need to install SoulLife on their client.

1. Install on server only
2. Players join with vanilla Minecraft client
3. All mod features work automatically

### Multi-World Servers

If running multiple worlds:
- Each world has separate player death data
- Data stored in world's `data/` folder per player UUID
- Switching worlds preserves death count

---

## Uninstallation

To remove the mod:

1. Stop server
2. Remove `SoulLife-*.jar` from `mods/` folder
3. Start server
4. Player data is preserved (in case you reinstall)

---

## Performance

SoulLife has minimal performance impact:
- Death tracking: **< 0.1% CPU**
- Event checking: **Only when death occurs**
- No tick-based loops
- No custom dimensions

---

## Support & Issues

If you encounter problems:

1. Check server logs for error messages
2. Verify you have the correct JAR for your loader
3. Ensure Fabric API is installed (Fabric only)
4. Try fresh reinstall

Open a GitHub Issue with:
- Server logs (last 100 lines)
- Mod loader version
- Minecraft server version
- Steps to reproduce

---

## Next Steps

After installation:

1. Test with `/soullife check`
2. Read mod info with `/soullife info`
3. Grant admin permission: `/op YourName`
4. Customize items with `/soullife edititem`
5. Enjoy the hardcore experience! ⚰️

