# Changelog

## [1.1.0] - 2026-08-27

### Added
- Language system: `/soullife language <code>` command
- Language autocomplete (ar_sa, en_us, fr_fr, es_es, pt_br)
- `/soullife language list` - show all supported languages
- `/soullife language reset` - reset to English
- `/soullife language current` - show current language
- Per-player language saved in NBT (persists across restarts)
- TranslationManager: server-side translation loading from lang JSON files
- LanguageManager: per-player language storage
- Broadcasts now show in each player's own language
- Dragon Egg crafting recipe (End Crystal + Nether Star + Crying Obsidian + Netherite Ingots)

### Changed
- MessageUtil now uses TranslationManager for per-player language
- DeathManager: added saveAllToNBT/loadAllFromNBT (includes language)
- All 5 lang files updated with new language command keys
- NeoForge, Forge, Fabric events updated to use new save/load

### Fixed
- Language fallback to English if key missing in player's language

---

## [1.0.0] - 2026-08-25

### Added
- Death tracking system (1 to 20 deaths)
- Ghost state on death (Spectator mode)
- White Leather Armor with Binding Curse and Unbreaking III
- Speed I and Glowing effects on ghost state
- Wither Boss sound on death
- Totem particles and sound on revival
- Sacrifice system with 20 unique items
- Permanent Spectator after 20 deaths
- Sidebar showing required sacrifice item
- Tab display showing death count
- All `/soullife` commands
- 5 language support
- NeoForge, Fabric, Forge 1.20.1 support
- GitHub Actions CI
