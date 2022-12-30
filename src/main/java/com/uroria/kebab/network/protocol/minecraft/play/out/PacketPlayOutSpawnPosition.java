package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.minecraft.BlockPosition;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class PacketPlayOutSpawnPosition extends PacketOut {
    public static final byte PACKET_ID = 0x4C;

    private final BlockPosition position;
    private final float angle;

    public PacketPlayOutSpawnPosition(BlockPosition position, float angle) {
        this.position = position;
        this.angle = angle;
    }

    public BlockPosition getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeBlockPosition(output, position);
        output.writeFloat(angle);
        return buffer.toByteArray();
    }
}
