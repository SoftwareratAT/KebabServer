package com.uroria.kebab.network;

public abstract class ChannelPacketHandler {
    public ChannelPacketRead read(ChannelPacketRead read) {
        return read;
    }

    public ChannelPacketWrite write(ChannelPacketWrite write) {
        return write;
    }
}
