package com.soullife.util;

import com.soullife.manager.DeathManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * SoulLife - ScoreboardManager
 * Handles:
 * - Tab display: death count in yellow
 * - Sidebar: sacrifice item reminder (white, persistent until revival)
 */
public class ScoreboardManager {

    private static final String SIDEBAR_OBJ = "soullife_side";
    private static final String TAB_OBJ     = "soullife_tab";

    // ─── Tab: Show death count ────────────────────────────────────────────────
    public static void updateTabDisplay(ServerPlayer player) {
        int deaths = DeathManager.getDeathCount(player);

        // Format: Deaths: 5/20 (yellow)
        Component tabText = Component.literal("§e☠ Deaths: " + deaths + "/20");
        // Update the player list header or use scoreboard below-name display
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            // Refresh for all visible players
        });

        // Use scoreboard BELOW_NAME slot (shows under player name in tab)
        Scoreboard scoreboard = player.getServer().getScoreboard();
        Objective existing = scoreboard.getObjective(TAB_OBJ);

        if (existing == null) {
            existing = scoreboard.addObjective(
                TAB_OBJ,
                ObjectiveCriteria.DUMMY,
                Component.literal("§eDeaths"),
                ObjectiveCriteria.RenderType.INTEGER
            );
        }

        // Set display slot to TAB list
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST, existing);

        // Set score
        scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), existing)
                  .setScore(deaths);
    }

    // ─── Sidebar: Show sacrifice item reminder ────────────────────────────────
    public static void showSacrificeBar(ServerPlayer player, String itemName) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        Objective obj = scoreboard.getObjective(SIDEBAR_OBJ);

        if (obj == null) {
            obj = scoreboard.addObjective(
                SIDEBAR_OBJ,
                ObjectiveCriteria.DUMMY,
                Component.literal("§c§l⚰ SoulLife"),
                ObjectiveCriteria.RenderType.INTEGER
            );
        }

        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, obj);

        // Show the required sacrifice item name
        String displayLine = "§f" + itemName;
        scoreboard.getOrCreatePlayerScore(displayLine, obj).setScore(1);
    }

    // ─── Sidebar: Hide ────────────────────────────────────────────────────────
    public static void hideSacrificeBar(ServerPlayer player) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, null);
    }
}
