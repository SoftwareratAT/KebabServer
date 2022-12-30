package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutUpdateViewPosition extends PacketOut {
    private final int chunkX;
    private final int chunkZ;

    public PacketPlayOutUpdateViewPosition(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkZ;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x4A);
        DataTypeIO.writeVarInt(output, chunkX);
        DataTypeIO.writeVarInt(output, chunkZ);
        return buffer.toByteArray();
    }
}
