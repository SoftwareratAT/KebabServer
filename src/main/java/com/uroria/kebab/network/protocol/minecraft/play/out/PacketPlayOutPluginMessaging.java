package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class PacketPlayOutPluginMessaging extends PacketOut {
    public static final byte PACKET_ID = 0x15;

    private final String channel;
    private final byte[] data;

    public PacketPlayOutPluginMessaging(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeString(output, channel, StandardCharsets.UTF_8);
        output.write(data);
        return buffer.toByteArray();
    }
}
