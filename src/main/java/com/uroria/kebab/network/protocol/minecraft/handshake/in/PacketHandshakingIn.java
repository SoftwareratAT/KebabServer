package com.uroria.kebab.network.protocol.minecraft.handshake.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketHandshakingIn extends PacketIn {
    public static byte PACKET_ID = 0x00;
    static {
        addHandshakeInPacket(PACKET_ID, PacketHandshakingIn.class);
    }

    public enum HandshakeType {
        STATUS(1),
        LOGIN(2);
        final int networkId;

        HandshakeType(int networkId) {
            this.networkId = networkId;
        }

        public int getNetworkId() {
            return this.networkId;
        }

        public static HandshakeType fromNetworkId(int networkId) {
            for (HandshakeType type : HandshakeType.values()) {
                if (type.networkId == networkId) {
                    return type;
                }
            }
            return null;
        }
    }


    private final int protocolVersion;
    private final String serverAddress;
    private final int serverPort;
    private final HandshakeType handshakeType;

    public PacketHandshakingIn(int protocolVersion, String serverAddress, int serverPort, HandshakeType handshakeType) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.handshakeType = handshakeType;
    }

    public PacketHandshakingIn(DataInputStream input) throws IOException {
        this(DataTypeIO.readVarInt(input), DataTypeIO.readString(input, StandardCharsets.UTF_8), input.readShort() & 0xFFFF, HandshakeType.fromNetworkId(DataTypeIO.readVarInt(input)));
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public HandshakeType getHandshakeType() {
        return handshakeType;
    }
}
