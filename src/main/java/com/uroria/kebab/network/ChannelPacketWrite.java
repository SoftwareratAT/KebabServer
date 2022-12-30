package com.uroria.kebab.network;

import com.uroria.kebab.network.protocol.PacketOut;

public final class ChannelPacketWrite {
    private PacketOut packet;
    ChannelPacketWrite(PacketOut packet) {
        this.packet = packet;
    }

    public PacketOut getPacket() {
        return packet;
    }

    public void setPacket(PacketOut packet) {
        this.packet = packet;
    }
}
