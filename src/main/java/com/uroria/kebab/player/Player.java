package com.uroria.kebab.player;

import com.uroria.kebab.network.protocol.PacketOut;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.time.Duration;

public interface Player {
    /**
     * Send a message to the player
     *
     * @param component The message
     */
    void sendMessage(Component component);

    /**
     * Check if the player has a specific permission
     *
     * @param permission The permission
     * @return Result
     */
    boolean hasPermission(String permission);

    /**
     * Send a title to the player
     *
     * @param title The title
     * @param subtitle The subtitle
     * @param fadeIn How long should the title fade in
     * @param stay How long should the title stay
     * @param fadeOut How long should the title fade out
     */
    void sendTitle(Component title, Component subtitle, Duration fadeIn, Duration stay, Duration fadeOut);

    /**
     * Send an outgoing packet to the player
     *
     * @param outgoingPacket The Packet
     */
    void sendPacket(PacketOut outgoingPacket);

    /**
     * Send a plugin message to the player
     *
     * @param channel The channel
     * @param data The given data
     */
    void sendPluginMessage(String channel, byte[] data) throws IOException;

    /**
     * Send a new server brand to the player.
     * The servers brand normally shows up in the F3 menu
     *
     * @param brand The new brand
     */
    void sendServerBrand(Component brand);
}
