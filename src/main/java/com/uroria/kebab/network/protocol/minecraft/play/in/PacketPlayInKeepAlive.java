package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInKeepAlive extends PacketIn {
    public static byte PACKET_ID = 0x11;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInKeepAlive.class);
    }

    private final long payload;

    public PacketPlayInKeepAlive(long payload) {
        this.payload = payload;
    }

    public PacketPlayInKeepAlive(DataInputStream input) throws IOException {
        this(input.readLong());
    }

    public static byte getPacketId() {
        return PACKET_ID;
    }
}
