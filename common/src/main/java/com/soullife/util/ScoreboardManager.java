package com.soullife.util;

import com.soullife.manager.DeathManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardManager {

    private static final String SIDEBAR_OBJ = "soullife_side";
    private static final String TAB_OBJ     = "soullife_tab";

    public static void updateTabDisplay(ServerPlayer player) {
        int deaths = DeathManager.getDeathCount(player);
        boolean isGhost = DeathManager.isGhost(player);

        Scoreboard scoreboard = player.getServer().getScoreboard();
        Objective existing = scoreboard.getObjective(TAB_OBJ);

        if (existing == null) {
            Component displayName;
            
            if (isGhost) {
                displayName = Component.literal("Deaths").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            } else {
                displayName = Component.literal("Deaths").withStyle(ChatFormatting.YELLOW);
            }
            
            existing = scoreboard.addObjective(
                TAB_OBJ,
                ObjectiveCriteria.DUMMY,
                displayName,
                ObjectiveCriteria.RenderType.INTEGER
            );
        } else {
            Component newDisplayName;
            
            if (isGhost) {
                newDisplayName = Component.literal("Deaths").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            } else {
                newDisplayName = Component.literal("Deaths").withStyle(ChatFormatting.YELLOW);
            }
            
            existing.setDisplayName(newDisplayName);
        }

        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST, existing);
        scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), existing)
                  .setScore(deaths);
    }

    public static void resetTabDisplay(ServerPlayer player) {
        updateTabDisplay(player);
    }

    public static void hideSacrificeBar(ServerPlayer player) {
    }
}
