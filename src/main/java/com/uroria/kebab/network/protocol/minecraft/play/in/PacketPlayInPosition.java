package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInPosition extends PacketIn {
    public static final byte PACKET_ID = 0x13;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInPosition.class);
    }

    private final double x;
    private final double y;
    private final double z;
    private final boolean onGround;

    public PacketPlayInPosition(double x, double y, double z, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    public PacketPlayInPosition(DataInputStream input) throws IOException {
        this(input.readDouble(), input.readDouble(), input.readDouble(), input.readBoolean());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
