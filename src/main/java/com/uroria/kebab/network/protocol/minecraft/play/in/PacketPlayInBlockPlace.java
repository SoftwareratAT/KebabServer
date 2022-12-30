package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.inventory.EquipmentSlot;
import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInBlockPlace extends PacketIn {
    public static final byte PACKET_ID = 0x32;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInBlockPlace.class);
    }

    private final EquipmentSlot hand;
    private final int sequence;

    public PacketPlayInBlockPlace(EquipmentSlot hand, int sequence) {
        this.hand = hand;
        this.sequence = sequence;
    }

    public PacketPlayInBlockPlace(DataInputStream input) throws IOException {
        this(EquipmentSlot.values()[DataTypeIO.readVarInt(input)], DataTypeIO.readVarInt(input));
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public int getSequence() {
        return sequence;
    }
}
