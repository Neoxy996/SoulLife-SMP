package com.soullife.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerPlayer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * SoulLife - TranslationManager
 * Loads lang JSON files and returns translated strings per player.
 * Falls back to English if key not found.
 */
public class TranslationManager {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();

    // Cache: langCode → {key → value}
    private static final Map<String, Map<String, String>> cache = new HashMap<>();

    // ── Load Language File ────────────────────────────────────────────────────
    private static Map<String, String> loadLang(String langCode) {
        if (cache.containsKey(langCode)) {
            return cache.get(langCode);
        }

        String path = "/assets/soullife/lang/" + langCode + ".json";
        try (InputStream is = TranslationManager.class.getResourceAsStream(path)) {
            if (is == null) {
                // Fallback to English
                if (!langCode.equals(LanguageManager.DEFAULT_LANG)) {
                    return loadLang(LanguageManager.DEFAULT_LANG);
                }
                return new HashMap<>();
            }
            Map<String, String> map = GSON.fromJson(
                new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8),
                MAP_TYPE
            );
            // Strip formatting codes for raw storage (Minecraft adds them later)
            cache.put(langCode, map);
            return map;

        } catch (Exception e) {
            cache.put(langCode, new HashMap<>());
            return new HashMap<>();
        }
    }

    // ── Get Translation ───────────────────────────────────────────────────────
    /**
     * Get translated string for a player.
     * Falls back to English if key not in player's language.
     */
    public static String get(ServerPlayer player, String key, Object... args) {
        String lang = LanguageManager.getLanguage(player);
        return get(lang, key, args);
    }

    public static String get(String lang, String key, Object... args) {
        Map<String, String> langMap = loadLang(lang);
        String value = langMap.get(key);

        // Fallback to English
        if (value == null && !lang.equals(LanguageManager.DEFAULT_LANG)) {
            Map<String, String> enMap = loadLang(LanguageManager.DEFAULT_LANG);
            value = enMap.get(key);
        }

        // Final fallback
        if (value == null) {
            return key;
        }

        // Format with args if provided
        if (args != null && args.length > 0) {
            try {
                return String.format(value, args);
            } catch (Exception e) {
                return value;
            }
        }

        return value;
    }

    // ── Clear Cache (for hot reload) ──────────────────────────────────────────
    public static void clearCache() {
        cache.clear();
    }
}
