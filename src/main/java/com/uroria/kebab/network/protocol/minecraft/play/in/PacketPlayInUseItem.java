package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.inventory.EquipmentSlot;
import com.uroria.kebab.location.MovingObjectPositionBlock;
import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInUseItem extends PacketIn {
    public static final byte PACKET_ID = 0x31;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInUseItem.class);
    }

    private final EquipmentSlot hand;
    private final MovingObjectPositionBlock blockHit;
    private final int sequence;
    public PacketPlayInUseItem(EquipmentSlot hand, MovingObjectPositionBlock blockHit, int sequence) {
        this.hand = hand;
        this.blockHit = blockHit;
        this.sequence = sequence;
    }

    public PacketPlayInUseItem(DataInputStream input) throws IOException {
        this(EquipmentSlot.values()[DataTypeIO.readVarInt(input)], DataTypeIO.readBlockHitResult(input), DataTypeIO.readVarInt(input));
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public MovingObjectPositionBlock getBlockHit() {
        return blockHit;
    }

    public int getSequence() {
        return sequence;
    }
}
