package com.soullife.util;

import com.soullife.manager.DeathManager;
import com.soullife.manager.LanguageManager;
import com.soullife.manager.TranslationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * SoulLife - MessageUtil
 * All messages use TranslationManager for per-player language.
 * Default: English. Player can change via /soullife language.
 */
public class MessageUtil {

 
    private static Component msg(ServerPlayer player, String key, Object... args) {
        String text = TranslationManager.get(player, key, args);
        return Component.literal(text);
    }

    private static Component msgGlobal(String key, Object... args) {
        String text = TranslationManager.get(LanguageManager.DEFAULT_LANG, key, args);
        return Component.literal(text);
    }

    public static void sendDeathMessages(ServerPlayer player, ItemStack required) {
        int deaths = DeathManager.getDeathCount(player);
        String itemName = required.getHoverName().getString();

        // 1. Actionbar - center screen (player only)
        player.displayClientMessage(
            msg(player, "soullife.death.title", deaths, 20), true);

        // 2. Chat private - sacrifice reminder (player only)
        player.sendSystemMessage(
            msg(player, "soullife.death.sacrifice", itemName));

        // 3. Chat broadcast - help message (everyone in THEIR language)
        String playerName = player.getName().getString();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendSystemMessage(Component.literal(
                TranslationManager.get(p, "soullife.death.help", playerName)));
        });
    }

    // ── Permanent Death ───────────────────────────────────────────────────────
    public static void sendPermanentDeathMessage(ServerPlayer player) {
        // Actionbar to player
        player.displayClientMessage(
            msg(player, "soullife.death.permanent"), true);
        player.sendSystemMessage(
            msg(player, "soullife.death.permanent"));

        // Broadcast in each player's language
        String playerName = player.getName().getString();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendSystemMessage(Component.literal(
                TranslationManager.get(p, "soullife.death.permanent.broadcast", playerName)));
        });
    }

    // ── Revival ───────────────────────────────────────────────────────────────
    public static void broadcastRevival(ServerPlayer player) {
        // Personal message
        player.displayClientMessage(
            msg(player, "soullife.revive.personal"), true);

        // Broadcast in each player's language
        String playerName = player.getName().getString();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendSystemMessage(Component.literal(
                TranslationManager.get(p, "soullife.revive.broadcast", playerName)));
        });

        ScoreboardManager.hideSacrificeBar(player);
    }

    // ── Commands ──────────────────────────────────────────────────────────────
    public static void sendCheckMessage(ServerPlayer player) {
        int deaths = DeathManager.getDeathCount(player);
        player.sendSystemMessage(msg(player, "soullife.cmd.check", deaths));
    }

    public static void sendNextMessage(ServerPlayer player, String itemName) {
        player.sendSystemMessage(msg(player, "soullife.cmd.next", itemName));
    }

    public static void sendInfoMessage(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(msg(player, "soullife.cmd.info.title"));
        player.sendSystemMessage(msg(player, "soullife.cmd.info.desc"));
        player.sendSystemMessage(msg(player, "soullife.cmd.info.deaths"));
        player.sendSystemMessage(msg(player, "soullife.cmd.info.sacrifice"));
        player.sendSystemMessage(msg(player, "soullife.cmd.info.permanent"));
        player.sendSystemMessage(Component.literal(""));
    }

    public static void sendGuiMessage(ServerPlayer player) {
        int currentDeaths = DeathManager.getDeathCount(player);
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(msg(player, "soullife.cmd.gui.title"));
        player.sendSystemMessage(Component.literal(
            "§7" + TranslationManager.get(player, "soullife.cmd.gui.deaths",
            currentDeaths, 20)));

        ItemStack[] items = DeathManager.getCurrentSacrificeItems();
        String[] colors = {
            "§a","§a","§a","§a","§a",
            "§9","§9","§9","§9","§9",
            "§6","§6","§6","§6","§6",
            "§c","§c","§c","§c","§c"
        };

        for (int i = 0; i < 20; i++) {
            String itemName = items[i].getHoverName().getString();
            String color = colors[i];
            String prefix;
            if (i < currentDeaths)      prefix = "§8[§a✔§8] §8";
            else if (i == currentDeaths) prefix = "§8[§e►§8] " + color;
            else                         prefix = "§8[§7" + (i+1) + "§8] " + color;

            player.sendSystemMessage(Component.literal(
                prefix + "Death " + (i+1) + ": §f" + itemName));
        }
        player.sendSystemMessage(Component.literal(""));
    }

    public static void sendAddMessage(ServerPlayer admin, ServerPlayer target,
                                       int amount, int oldDeaths, int newDeaths) {
        admin.sendSystemMessage(msg(admin, "soullife.cmd.add",
            target.getName().getString(), amount, oldDeaths, newDeaths));
    }

    public static void sendRemoveMessage(ServerPlayer admin, ServerPlayer target,
                                          int amount, int oldDeaths, int newDeaths) {
        admin.sendSystemMessage(msg(admin, "soullife.cmd.remove",
            target.getName().getString(), amount, oldDeaths, newDeaths));
    }

    public static void sendSetMessage(ServerPlayer admin, ServerPlayer target,
                                       int oldDeaths, int newDeaths) {
        admin.sendSystemMessage(msg(admin, "soullife.cmd.set",
            target.getName().getString(), oldDeaths, newDeaths));
    }

    public static void sendEditItemMessage(ServerPlayer admin, int deathIndex,
                                            String itemName) {
        admin.sendSystemMessage(msg(admin, "soullife.cmd.edititem",
            deathIndex + 1, itemName));
    }

    public static void sendResetMessage(ServerPlayer admin) {
        admin.sendSystemMessage(msg(admin, "soullife.cmd.reset"));
    }

    // ── Language Command Messages ─────────────────────────────────────────────
    public static void sendLanguageChanged(ServerPlayer player, String langCode) {
        String langName = LanguageManager.getLanguageName(langCode);
        player.sendSystemMessage(msg(player, "soullife.cmd.language.changed", langName));
    }

    public static void sendLanguageCurrent(ServerPlayer player) {
        String langCode = LanguageManager.getLanguage(player);
        String langName = LanguageManager.getLanguageName(langCode);
        player.sendSystemMessage(msg(player, "soullife.cmd.language.current",
            langCode, langName));
    }

    public static void sendLanguageList(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(msg(player, "soullife.cmd.language.list.title"));
        LanguageManager.SUPPORTED_LANGUAGES.forEach((code, name) -> {
            String current = code.equals(LanguageManager.getLanguage(player)) ? " §a◄" : "";
            player.sendSystemMessage(Component.literal(
                "§7  " + code + " §f- " + name + current));
        });
        player.sendSystemMessage(Component.literal(""));
    }

    public static void sendLanguageReset(ServerPlayer player) {
        player.sendSystemMessage(msg(player, "soullife.cmd.language.reset"));
    }

    public static void sendLanguageInvalid(ServerPlayer player, String lang) {
        player.sendSystemMessage(msg(player, "soullife.cmd.language.invalid", lang));
    }

    public static void sendNoPermission(ServerPlayer player) {
        player.sendSystemMessage(msg(player, "soullife.error.noperm"));
    }
}
