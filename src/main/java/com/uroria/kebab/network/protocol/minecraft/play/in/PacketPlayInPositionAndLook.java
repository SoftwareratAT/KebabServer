package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInPositionAndLook extends PacketIn {
    public static final byte PACKET_ID = 0x14;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInPositionAndLook.class);
    }

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean onGround;

    public PacketPlayInPositionAndLook(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PacketPlayInPositionAndLook(DataInputStream input) throws IOException {
        this(input.readDouble(), input.readDouble(), input.readDouble(), input.readFloat(), input.readFloat(), input.readBoolean());
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
