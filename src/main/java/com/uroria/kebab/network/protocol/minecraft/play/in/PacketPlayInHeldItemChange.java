package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInHeldItemChange extends PacketIn {
    public static final byte PACKET_ID = 0x28;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInHeldItemChange.class);
    }

    private final short slot;

    public PacketPlayInHeldItemChange(short slot) {
        this.slot = slot;
    }

    public PacketPlayInHeldItemChange(DataInputStream input) throws IOException {
        this(input.readShort());
    }

    public short getSlot() {
        return slot;
    }
}
