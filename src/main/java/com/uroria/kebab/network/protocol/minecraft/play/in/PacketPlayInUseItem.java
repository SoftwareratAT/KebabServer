package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.inventory.EquipmentSlot;
import com.uroria.kebab.network.protocol.PacketIn;

import javax.swing.*;

public class PacketPlayInUseItem extends PacketIn {
    public static final byte PACKET_ID = 0x31;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInUseItem.class);
    }

    private final EquipmentSlot hand;
    private //TODO DO REST
    private final int sequence;



}
