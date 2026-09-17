package com.soullife.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GhostDisplayManager {

    public static void updatePlayerDisplay(ServerPlayer player) {
        if (DeathManager.isGhost(player)) {
            setGhostDisplay(player);
        } else {
            setNormalDisplay(player);
        }
    }

    private static void setGhostDisplay(ServerPlayer player) {
        Component redName = Component.literal(player.getName().getString())
            .withStyle(net.minecraft.ChatFormatting.RED);
        
        player.setCustomName(redName);
        player.setCustomNameVisible(true);
    }

    private static void setNormalDisplay(ServerPlayer player) {
        Component whiteName = Component.literal(player.getName().getString())
            .withStyle(net.minecraft.ChatFormatting.WHITE);
        
        player.setCustomName(whiteName);
        player.setCustomNameVisible(true);
    }

    public static void refreshDisplay(ServerPlayer player) {
        updatePlayerDisplay(player);
        
        for (ServerPlayer otherPlayer : player.getServer().getPlayerList().getPlayers()) {
            otherPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket(
                net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                player
            ));
        }
    }
}
