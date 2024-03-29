package com.uroria.kebab.player;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.commands.CommandSource;
import com.uroria.kebab.entity.DataWatcher;
import com.uroria.kebab.entity.EntityType;
import com.uroria.kebab.entity.LivingEntity;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.network.ClientConnection;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.network.protocol.minecraft.play.out.*;
import com.uroria.kebab.utils.MessageSignature;
import com.uroria.kebab.utils.minecraft.GameMode;
import com.uroria.kebab.entity.DataWatcher.WatchableField;
import com.uroria.kebab.entity.DataWatcher.WatchableObjectType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class KebabPlayer extends LivingEntity implements CommandSource, Audience, Player {
    public final ClientConnection clientConnection;
    public final PlayerInteractManager playerInteractManager;

    protected String userName;
    protected GameMode gameMode;
    protected DataWatcher watcher;
    protected byte selectedSlot;
    private final AtomicInteger containerIdCounter;

    @WatchableField(MetadataIndex = 15, WatchableObjectType = WatchableObjectType.FLOAT)
    protected float additionalHearts = 0.0F;
    @WatchableField(MetadataIndex = 16, WatchableObjectType = WatchableObjectType.VARINT)
    protected int score = 0;
    @WatchableField(MetadataIndex = 17, WatchableObjectType = WatchableObjectType.BYTE)
    protected byte skinLayers = 0;
    @WatchableField(MetadataIndex = 18, WatchableObjectType = WatchableObjectType.BYTE)
    protected byte mainHand = 1;


    public KebabPlayer(ClientConnection clientConnection, String userName, UUID uuid, int entityId, Location location) throws IllegalArgumentException, IllegalAccessException {
        super(EntityType.PLAYER, uuid, location);
        this.clientConnection = clientConnection;
        this.userName = userName;
        this.entityId = entityId;
        this.containerIdCounter = new AtomicInteger(1);
        //TODO PlayerInventory
        //TODO InventoryView
        this.playerInteractManager = new PlayerInteractManager(this);
        this.watcher = new DataWatcher(this);
        this.watcher.update();
    }

    protected int nextContainerId() {
        return containerIdCounter.updateAndGet(i -> i++ > Byte.MAX_VALUE ? 1 : i);
    }

    public byte getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(byte slot) {
        if (slot == selectedSlot) return;
        try {
            sendPacket(null);
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to change " + userName + " selected slot!", exception);
        }
        this.selectedSlot = slot;
    }

    public float getAdditionalHearts() {
        return additionalHearts;
    }

    public void setAdditionalHearts(float additionalHearts) {
        this.additionalHearts = additionalHearts;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public byte getSkinLayers() {
        return this.skinLayers;
    }

    public void setSkinLayers(byte skinLayers) {
        this.skinLayers = skinLayers;
    }

    public byte getMainHand() {
        return this.mainHand;
    }

    public void setMainHand(byte mainHand) {
        this.mainHand = mainHand;
    }

    public DataWatcher getDataWatcher() {
        return this.watcher;
    }

    @Override
    public boolean isValid() {
        return KebabServer.getInstance().getPlayers().contains(this);
    }

    public void remove() {

    }

    @Override
    public void sendPluginMessage(String channel, byte[] data) throws IOException {
        this.clientConnection.sendPluginMessage(channel, data);
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;//TODO PERMISSION
    }

    @Override
    public void teleport(Location location) {
        //TODO TELEPORT METHOD
    }

    @Override
    public ClientConnection getClientConnection() {
        return this.clientConnection;
    }

    @Override
    public PlayerInteractManager getPlayerInteractManager() {
        return this.playerInteractManager;
    }

    @Override
    public void sendServerBrand(Component brand) {
        try {
            this.clientConnection.sendServerBrand(brand);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected void setLocation(Location location) {
        super.teleport(location);
    }

    public void sendPluginMessage(Key channel, byte[] data) throws IOException {
        clientConnection.sendPluginMessage(channel.toString(), data);
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(Component component) {
        sendMessage(component);
    }

    @Override
    public void disconnect(Component reason) {
        this.clientConnection.disconnect(reason);
    }

    public void chat(String message, MessageSignature saltSignature, Instant time) {
        //TODO CHATEVENT
        KebabServer.getInstance().getLogger().info(this.userName + ": " + message);
        //TODO Send to everyone
    }

    public void setPlayerListHeaderFooter(Component headerComponent, Component footerComponent) {
        //try {
        //
        //        } catch (IOException exception) {
        //            KebabServer.getInstance().getLogger().error("Error while trying to send PlayerListHeaderFooter to " + userName, exception);
        //        }
    }

    public void sendTitle(Component title, Component subtitle, Duration fadeIn, Duration stay, Duration fadeOut) {
        sendTitlePart(TitlePart.TITLE, title);
        sendTitlePart(TitlePart.SUBTITLE, subtitle);
        sendTitlePart(TitlePart.TIMES, Title.Times.times(fadeIn, stay, fadeOut));
    }

    @Override
    public void sendPacket(PacketOut outgoingPacket) throws IOException {
        this.clientConnection.sendPacket(outgoingPacket);
    }

    @Override
    public <T> void sendTitlePart(TitlePart<T> part, T value) {
        if (part.equals(TitlePart.TITLE)) {
            try {
                ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket((Component) value);
                sendPacket(titleTextPacket);
            } catch (IOException exception) {
                KebabServer.getInstance().getLogger().error("Error while trying to send title to " + userName, exception);
            }
            return;
        }
        if (part.equals(TitlePart.SUBTITLE)) {
            try {
                ClientboundSetSubtitleTextPacket subtitleTextPacket = new ClientboundSetSubtitleTextPacket((Component) value);
                sendPacket(subtitleTextPacket);
            } catch (IOException exception) {
                KebabServer.getInstance().getLogger().error("Error while trying to send subtitle to " + userName, exception);
            }
            return;
        }
        if (part.equals(TitlePart.TIMES)) {
            try {
                Title.Times times = (Title.Times) value;
                ClientboundSetTitlesAnimationPacket titlesAnimationPacket = new ClientboundSetTitlesAnimationPacket(Math.round(times.fadeIn().getSeconds() * 20), Math.round(times.stay().getSeconds() * 20), Math.round(times.fadeOut().getSeconds() * 20));
                sendPacket(titlesAnimationPacket);
            } catch (IOException exception) {
                KebabServer.getInstance().getLogger().error("Error while trying to send title times to " + userName, exception);
            }
        }
    }

    @Override
    public void clearTitle() {
        try {
            ClientboundClearTitlesPacket clearTitlesPacket = new ClientboundClearTitlesPacket(false);
            sendPacket(clearTitlesPacket);
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to clear title of " + userName, exception);
        }
    }

    @Override
    public void resetTitle() {
        try {
            ClientboundClearTitlesPacket clearTitlesPacket = new ClientboundClearTitlesPacket(true);
            sendPacket(clearTitlesPacket);
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to reset title of " + userName, exception);
        }
    }

    //TODO Bossbar

    public void updateInventory() {
        //TODO Inventory update
    }

    public void openInventory() {
        //TODO Open inventory
    }


    public void closeInventory() {
        //TODO close inventory
    }

    //TODO getEquipment & getInventoryHolder

    public void setGameMode(GameMode gameMode) {
        if (!this.gameMode.equals(gameMode)) {
            try {
                PacketPlayOutGameState gameState = new PacketPlayOutGameState(3, gameMode);
                sendPacket(gameState);
            } catch (IOException exception) {
                KebabServer.getInstance().getLogger().error("Error while trying to change GameMode of " + this.userName, exception);
            }
        }
        this.gameMode = gameMode;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public String getName() {
        return this.userName;
    }
}
