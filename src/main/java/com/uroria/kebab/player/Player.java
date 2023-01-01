package com.uroria.kebab.player;

import com.uroria.kebab.location.Location;
import com.uroria.kebab.network.ClientConnection;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.minecraft.GameMode;
import com.uroria.kebab.world.World;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

public interface Player {
    /**
     * Gets the UUID of the player
     *
     * @return UUID
     */
    UUID getUniqueId();

    /**
     * Gets the name of the player
     *
     * @return String of name
     */
    String getName();

    /**
     * Send a message to the player
     *
     * @param component The message
     */
    void sendMessage(Component component);

    /**
     * Gets the GamMeode of the player
     *
     * @return GameMode
     */
    GameMode getGameMode();

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
    void sendPacket(PacketOut outgoingPacket) throws IOException;

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

    /**
     * Disconnects the player with a given reason
     *
     * @param reason The Reason
     */
    void disconnect(Component reason);

    /**
     * Teleports the player to a given location
     *
     * @param toLocation The new Location
     */
    void teleport(Location toLocation);

    /**
     * Gets the world the player currently is on
     *
     * @return World
     */
    World getWorld();

    /**
     * Gets the ClientConnection of the player
     *
     * @return ClientConnection
     */
    ClientConnection getClientConnection();

    /**
     * Gets the PlayerInteractManager of the player
     *
     * @return PlayerInteractManager
     */
    PlayerInteractManager getPlayerInteractManager();
}
