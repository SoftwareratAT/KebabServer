package com.uroria.kebab.network;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.events.player.PlayerLoginEvent;
import com.uroria.kebab.events.player.PlayerPostLoginEvent;
import com.uroria.kebab.events.player.PlayerPreLoginEvent;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.network.protocol.Packet;
import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.network.protocol.minecraft.handshake.in.PacketHandshakingIn;
import com.uroria.kebab.network.protocol.minecraft.login.in.PacketLoginInLoginStart;
import com.uroria.kebab.network.protocol.minecraft.login.in.PacketLoginInPluginMessaging;
import com.uroria.kebab.network.protocol.minecraft.login.out.PacketLoginOutDisconnect;
import com.uroria.kebab.network.protocol.minecraft.login.out.PacketLoginOutSuccess;
import com.uroria.kebab.network.protocol.minecraft.play.out.*;
import com.uroria.kebab.network.protocol.minecraft.play.out.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer.PlayerSkinProperty;
import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.network.protocol.minecraft.play.out.PacketPlayOutPlayerAbilities.PlayerAbilityFlags;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.MojangAPI.SkinResponse;
import com.uroria.kebab.utils.minecraft.BlockPosition;
import com.uroria.kebab.utils.minecraft.GameMode;
import com.uroria.kebab.world.World;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ClientConnection extends Thread {

    private static final Key DEFAULT_HANDLER_NAMESPACE = Key.key("default");
    private static final String BRAND_ANNOUNCE_CHANNEL = Key.key("brand").toString();

    private final Random random = new Random();
    private final Socket clientSocket;
    protected Channel channel;
    private boolean running;
    private ClientState state;

    private KebabPlayer player;
    private TimerTask keepAliveTask;
    private AtomicLong lastPacketTimestamp;
    private AtomicLong lastKeepAlivePayload;
    private InetAddress inetAddress;
    private boolean ready;

    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.inetAddress = clientSocket.getInetAddress();
        this.lastPacketTimestamp = new AtomicLong(-1);
        this.lastKeepAlivePayload = new AtomicLong(-1);
        this.channel = null;
        this.running = false;
        this.ready = false;
    }

    public InetAddress getAddress() {
        return this.inetAddress;
    }

    public long getLastKeepAlivePayload() {
        return this.lastKeepAlivePayload.get();
    }

    public long getLastPacketTimestamp() {
        return this.lastPacketTimestamp.get();
    }

    public void setLastPacketTimestamp(long payLoad) {
        this.lastPacketTimestamp.set(payLoad);
    }

    public TimerTask getKeepAliveTask() {
        return this.keepAliveTask;
    }

    public KebabPlayer getPlayer() {
        return this.player;
    }

    public ClientState getClientState() {
        return this.state;
    }

    public Socket getSocket() {
        return this.clientSocket;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void sendPluginMessage(String channel, byte[] data) throws IOException {
        PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(channel, data);
        sendPacket(packet);
    }

    public synchronized void sendPacket(PacketOut packet) throws IOException {
        if (channel.writePacket(packet)) {
            setLastPacketTimestamp(System.currentTimeMillis());
        }
    }

    public void disconnect(Component reason) {
        try {
            PacketPlayOutDisconnect packet = new PacketPlayOutDisconnect(reason);
            sendPacket(packet);
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to send PacketPlayOutDisconnect Packet to player " + this.getName(), exception);
        }
        try {
            clientSocket.close();
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to close SocketConnection of " + this.getName(), exception);
        }
    }

    private void disconnectDuringLogin(Component reason) {
        try {
            PacketLoginOutDisconnect packet = new PacketLoginOutDisconnect(reason);
            sendPacket(packet);
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to send PacketLoginOutDisconnect Packet to " + this.getName(), exception);
        }
        try {
            this.clientSocket.close();
        } catch (IOException exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to close SocketConnection of " + this.getName(), exception);
        }
    }

    private void setChannel(DataInputStream input, DataOutputStream output) {
        this.channel = new Channel(input, output);

        this.channel.addHandlerBefore(DEFAULT_HANDLER_NAMESPACE, new ChannelPacketHandler() {
            @Override
            public ChannelPacketRead read(ChannelPacketRead read) {
                if (read.hasReadPacket()) {
                    return super.read(read);
                }
                try {
                    DataInput input = read.getDataInput();
                    int size = read.getSize();
                    int packetId = read.getPacketId();
                    Class<? extends PacketIn> packetType;
                    switch (state) {
                        case HANDSHAKE:
                            packetType = Packet.getHandshakeIn().get(packetId);
                            break;
                        case LOGIN:
                            packetType = Packet.getLoginIn().get(packetId);
                            break;
                        case PLAY:
                            packetType = Packet.getPlayIn().get(packetId);
                            break;
                        default:
                            throw new IllegalStateException("Illegal ClientState!");
                    }
                    if (packetType == null) {
                        input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
                        return null;
                    }
                    Constructor<?>[] constructors = packetType.getConstructors();
                    Constructor<?> constructor = Arrays.stream(constructors).filter(each -> each.getParameterCount() > 0 && each.getParameterTypes()[0].equals(DataInputStream.class)).findFirst().orElse(null);
                    if (constructor == null) {
                        throw new NoSuchMethodException(packetType + " has no valid constructors!");
                    } else if (constructor.getParameterCount() == 1) {
                        read.setPacket((PacketIn) constructor.newInstance(input));
                    } else if (constructor.getParameterCount() == 3) {
                        read.setPacket((PacketIn) constructor.newInstance(input, size, packetId));
                    } else {
                        throw new NoSuchMethodException(packetType + " has no valid constructors!");
                    }
                    return super.read(read);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to read packet", e);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        running = true;
        //Setting the State to HANDSHAKE
        state = ClientState.HANDSHAKE;

        try {
            PlayerSkinProperty skinProperty = null;
            try {
                KebabServer.getInstance().getLogger().info("Trying to connect...");
                skinProperty = handleHandshake();
            } catch (Exception exception) {
                //Give up if something fails
                channel.close();
                clientSocket.close();
                state = ClientState.DISCONNECTED;
                exception.printStackTrace();
            }
            //Handle play packets and events
            if (state == ClientState.PLAY) {
                KebabServer.getInstance().getLogger().info("Connecting...");
                handleJoin(skinProperty);
                handlePlay();
                KebabServer.getInstance().getLogger().info("Disconnected!");
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        //Handle disconnect when ClientSocket is not connected anymore
        try {
            channel.close();
            clientSocket.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        this.state = ClientState.DISCONNECTED;
        if (player != null) KebabServer.getInstance().getUnsafe().addPlayer(player);
        KebabServer.getInstance().getServerConnection().getClients().remove(this);
        this.running = false;
    }

    private PlayerSkinProperty handleHandshake() throws IOException, Exception {
        clientSocket.setKeepAlive(true);
        //Setting up channel
        setChannel(new DataInputStream(clientSocket.getInputStream()), new DataOutputStream(clientSocket.getOutputStream()));
        int handShakeSize = DataTypeIO.readVarInt(channel.input);
        //Receiving incoming Handshaking packet
        System.out.println(channel.input);
        System.out.println(handShakeSize);

        PacketHandshakingIn handshake = (PacketHandshakingIn) channel.readPacket(handShakeSize);
        System.out.println("Read");
        String bungeeForwarding = handshake.getServerAddress();
        UUID bungeeUUID = null;
        SkinResponse forwardedSkin = null;
        if (handshake.getHandshakeType() == PacketHandshakingIn.HandshakeType.LOGIN) {
            state = ClientState.LOGIN;
            try {
                String[] data = bungeeForwarding.split("\\x00");
                String clientIp = "";
                String bungee = "";
                String skinData = "";
                int state = 0;
                for (String datum : data) {
                    switch (state) {
                        default -> {
                        }
                        case 0 -> {
                            state = 1;
                        }
                        case 1 -> {
                            clientIp = datum;
                            state = 2;
                        }
                        case 2 -> {
                            bungee = datum;
                            state = 3;
                        }
                        case 3 -> {
                            skinData = datum;
                            state = 4;
                        }
                    }
                }
                KebabServer.getInstance().getLogger().info("Adding...");
                if (state != 4) throw new IllegalStateException("Illegal bungee state: " + state);

                bungeeUUID = UUID.fromString(bungee.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
                inetAddress = InetAddress.getByName(clientIp);

                if (!skinData.equals("")) {
                    JSONArray skinJSON = (JSONArray) new JSONParser().parse(skinData);

                    for (Object object : skinJSON) {
                        JSONObject property = (JSONObject) object;
                        if (property.get("name").toString().equals("textures")) {
                            String skin = property.get("value").toString();
                            String signature = property.get("signature").toString();
                            forwardedSkin = new SkinResponse(skin, signature);
                        }
                    }
                }

                PlayerPreLoginEvent playerPreLoginEvent = new PlayerPreLoginEvent(this, false, Component.newline().content("§cDisconnected before Login"));
                KebabServer.getInstance().getEventManager().callEvent(playerPreLoginEvent);
                if (playerPreLoginEvent.isCancelled()) {
                    disconnectDuringLogin(playerPreLoginEvent.getReason());
                    return forwardedSkin != null ? new PlayerSkinProperty(forwardedSkin.getSkin(), forwardedSkin.getSignature()) : null;
                }
            } catch (Exception exception) {
                KebabServer.getInstance().getLogger().error("Error while trying to resolve bungees ip forwarding!", exception);
                disconnectDuringLogin(Component.newline().content("§cPlease connect from the proxy!"));
                return null;
            }
        }
        return null;
    }

    private void handlePlay() throws Exception {
        int messageId = this.random.nextInt();
        while (clientSocket.isConnected()) {
            PacketIn packetIn = channel.readPacket();
            if (packetIn instanceof PacketLoginInLoginStart start) {
                String username = start.getUsername();
                UUID uuid = start.hasUniqueId() ? start.getUniqueId() : null;
                if (uuid == null) uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

                //Login success, switching to PLAY mode
                PacketLoginOutSuccess success = new PacketLoginOutSuccess(uuid, username);
                sendPacket(success);
                state = ClientState.PLAY;

                //player = new KebabPlayer(this, username,uuid);
                //TODO Create player
                break;
            } else if (packetIn instanceof PacketLoginInPluginMessaging response) {
                if (response.getMessageId() != messageId) {
                    disconnectDuringLogin(Component.newline().content("§cInternal error, messageId did not match!"));
                    break;
                }
                if (response.getData().isEmpty()) {
                    disconnectDuringLogin(Component.newline().content("§cUnknown login plugin response packet!"));
                    break;
                }
                break;
            }
            PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(player, false, Component.newline().content("§cDisconnected during Login"));
            KebabServer.getInstance().getEventManager().callEvent(playerLoginEvent);
            if (playerLoginEvent.isCancelled()) disconnect(playerLoginEvent.getReason());
            break;
        }


    }

    private void handleJoin(PlayerSkinProperty forwardedSkin) throws IOException, InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);

        //TODO Spawn-handling
        PlayerPostLoginEvent postLoginEvent = null;
        //PlayerPostLoginEvent postLoginEvent = new PlayerPostLoginEvent(player, GameMode.ADVENTURE, WORLD, LOCATION, 6, 5, true, true, false, Component.newline().content("§cDisconnected after Login"));
        //        KebabServer.getInstance().getEventManager().callEvent(postLoginEvent);
        //        if (postLoginEvent.isCancelled()) {
        //            disconnect(postLoginEvent.getReason());
        //            return;
        //        }
        World spawnWorld = postLoginEvent.getSpawnWorld();
        Location spawnLocation = postLoginEvent.getSpawnLocation();

        //PacketPlayOutLogin loginPacket = new PacketPlayOutLogin(player.getEntityId(), postLoginEvent.isHardcore(), postLoginEvent.getGameMode(), );
        //sendPacket(loginPacket);

        //Send default server brand
        sendServerBrand(postLoginEvent.getServerBrand());

        //Update needed information for player
        PacketPlayOutPlayerInfo playerInfoPacket = new PacketPlayOutPlayerInfo(EnumSet.of(PacketPlayOutPlayerInfo.PlayerInfoAction.ADD_PLAYER, PacketPlayOutPlayerInfo.PlayerInfoAction.UPDATE_GAME_MODE, PacketPlayOutPlayerInfo.PlayerInfoAction.UPDATE_LISTED, PacketPlayOutPlayerInfo.PlayerInfoAction.UPDATE_LATENCY, PacketPlayOutPlayerInfo.PlayerInfoAction.UPDATE_DISPLAY_NAME), player.getUniqueId(), new PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer(player.getName(), true, Optional.ofNullable(forwardedSkin), postLoginEvent.getGameMode(), 0, false, Optional.empty()));
        sendPacket(playerInfoPacket);

        //Send players abilities
        Set<PlayerAbilityFlags> flags = new HashSet<>();
        if (player.getGameMode().equals(GameMode.CREATIVE)) flags.add(PlayerAbilityFlags.CREATIVE);
        //if (player.canFly()) flags.add(PlayerAbilityFlags.ALLOW_FLYING);
        PacketPlayOutPlayerAbilities abilities = new PacketPlayOutPlayerAbilities(0.05F, 0.1F, flags.toArray(new PlayerAbilityFlags[flags.size()]));
        sendPacket(abilities);

        //TODO Update PlayerInteractManager

        //Send spawn position to player
        PacketPlayOutSpawnPosition spawnPosition = new PacketPlayOutSpawnPosition(BlockPosition.from(spawnLocation), spawnLocation.getPitch());
        sendPacket(spawnPosition);

        //Teleport to spawn position
        PacketPlayOutPositionAndLook positionAndLook = new PacketPlayOutPositionAndLook(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch(), 1, false);
        //TODO Unsafe teleport
        sendPacket(positionAndLook);
    }

    public void sendServerBrand(Component serverBrand) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(serverBrand), StandardCharsets.UTF_8);
        sendPluginMessage("minecraft:brand", buffer.toByteArray());
    }


    public enum ClientState {
        HANDSHAKE,
        LOGIN,
        PLAY,
        DISCONNECTED
    }
}
