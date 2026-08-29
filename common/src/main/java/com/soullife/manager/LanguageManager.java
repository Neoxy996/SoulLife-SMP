package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * SoulLife - LanguageManager
 * Handles per-player language selection.
 * Default: English (en_us)
 * Player can change via: /soullife language <lang>
 */
public class LanguageManager {

    // ── Supported Languages ───────────────────────────────────────────────────
    public static final Map<String, String> SUPPORTED_LANGUAGES = new LinkedHashMap<>();
    static {
        SUPPORTED_LANGUAGES.put("en_us", "English");
        SUPPORTED_LANGUAGES.put("ar_sa", "العربية");
        SUPPORTED_LANGUAGES.put("fr_fr", "Français");
        SUPPORTED_LANGUAGES.put("es_es", "Español");
        SUPPORTED_LANGUAGES.put("pt_br", "Português (Brasil)");
    }

    // Default language
    public static final String DEFAULT_LANG = "en_us";

    // Per-player language storage (UUID → lang code)
    private static final Map<UUID, String> playerLanguages = new HashMap<>();

    // ── Get Player Language ───────────────────────────────────────────────────
    public static String getLanguage(ServerPlayer player) {
        return playerLanguages.getOrDefault(player.getUUID(), DEFAULT_LANG);
    }

    // ── Set Player Language ───────────────────────────────────────────────────
    public static boolean setLanguage(ServerPlayer player, String lang) {
        if (!SUPPORTED_LANGUAGES.containsKey(lang)) return false;
        playerLanguages.put(player.getUUID(), lang);
        return true;
    }

    // ── Reset to Default ──────────────────────────────────────────────────────
    public static void resetLanguage(ServerPlayer player) {
        playerLanguages.remove(player.getUUID());
    }

    // ── Check if Language Supported ───────────────────────────────────────────
    public static boolean isSupported(String lang) {
        return SUPPORTED_LANGUAGES.containsKey(lang);
    }

    // ── Get Language Name ─────────────────────────────────────────────────────
    public static String getLanguageName(String code) {
        return SUPPORTED_LANGUAGES.getOrDefault(code, "Unknown");
    }

    // ── Save/Load from NBT ────────────────────────────────────────────────────
    public static void saveLanguage(ServerPlayer player,
                                     net.minecraft.nbt.CompoundTag tag) {
        String lang = playerLanguages.get(player.getUUID());
        if (lang != null) {
            tag.putString("soullife_language", lang);
        }
    }

    public static void loadLanguage(ServerPlayer player,
                                     net.minecraft.nbt.CompoundTag tag) {
        if (tag.contains("soullife_language")) {
            String lang = tag.getString("soullife_language");
            if (isSupported(lang)) {
                playerLanguages.put(player.getUUID(), lang);
            }
        }
    }
}
