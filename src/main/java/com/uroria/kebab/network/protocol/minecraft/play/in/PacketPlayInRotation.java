package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInRotation extends PacketIn {
    public static final byte PACKET_ID = 0x15;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInRotation.class);
    }

    private final float yaw;
    private final float pitch;
    private final boolean onGround;

    public PacketPlayInRotation(float yaw, float pitch, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PacketPlayInRotation(DataInputStream input) throws IOException {
        this(input.readFloat(), input.readFloat(), input.readBoolean());
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
