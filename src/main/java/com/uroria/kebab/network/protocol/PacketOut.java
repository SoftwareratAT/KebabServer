package com.uroria.kebab.network.protocol;

import com.uroria.kebab.KebabServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class PacketOut extends Packet{
    public abstract byte[] serializePacket() throws IOException;
}
