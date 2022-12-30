package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.minecraft.GameMode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class PacketPlayOutGameState extends PacketOut {
    public static final byte PACKET_ID = 0x1C;

    private final int reason;
    private final float value;

    public PacketPlayOutGameState(int reason, float value) {
        this.reason = reason;
        this.value = value;
    }

    public PacketPlayOutGameState(int reason, GameMode gameMode) {
        this.reason = reason;
        this.value = gameMode.getId();
    }

    public int getReason() {
        return reason;
    }

    public float getValue() {
        return value;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.writeByte(reason);
        output.writeFloat(value);
        return buffer.toByteArray();
    }
}
