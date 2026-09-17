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
        player.setCustomNameVisible(false);
    }

    private static void setNormalDisplay(ServerPlayer player) {
        player.setCustomName(null);
        player.setCustomNameVisible(false);
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
