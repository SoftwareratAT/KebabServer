package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayInPluginMessaging extends PacketIn {
    public static final byte PACKET_ID = 0x0C;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInPluginMessaging.class);
    }

    private final String channel;
    private final byte[] data;

    public PacketPlayInPluginMessaging(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    public PacketPlayInPluginMessaging(DataInputStream input, int packetLength, int packetId) throws IOException {
        this.channel = DataTypeIO.readString(input, StandardCharsets.UTF_8);
        int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getStringLength(channel, StandardCharsets.UTF_8);
        this.data = new byte[dataLength];
        input.readFully(this.data);
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }
}
