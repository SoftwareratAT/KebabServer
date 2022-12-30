package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class PacketPlayOutWindowData extends PacketOut {
    public static final byte PACKET_ID = 0x11;

    private final int containerId;
    private final int id;
    private final int value;

    public PacketPlayOutWindowData(int containerId, int id, int value) {
        this.containerId = containerId;
        this.id = id;
        this.value = value;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.writeByte(containerId);
        output.writeShort(id);
        output.writeShort(value);
        return buffer.toByteArray();
    }
}
