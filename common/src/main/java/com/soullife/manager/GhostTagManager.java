package com.soullife.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GhostTagManager {

    public static void addGhostTag(ServerPlayer player) {
        String originalName = player.getName().getString();
        String ghostName = "💀 [GHOST] " + originalName;
        
        player.setCustomName(Component.literal(ghostName)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        player.setCustomNameVisible(true);
    }

    public static void removeGhostTag(ServerPlayer player) {
        player.setCustomName(null);
        player.setCustomNameVisible(false);
    }

    public static boolean hasGhostTag(ServerPlayer player) {
        if (player.getCustomName() == null) return false;
        return player.getCustomName().getString().contains("[GHOST]");
    }
}
