package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutDeclareCommands extends PacketOut {
    public static final byte PACKET_ID = 0x0E;

    private final byte[] data;

    public PacketPlayOutDeclareCommands(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.write(data);
        return buffer.toByteArray();
    }
}
