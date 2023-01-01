package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutEntityDestroy extends PacketOut {
    public static final byte PACKET_ID = 0x3A;

    private final int[] entityIds;

    public PacketPlayOutEntityDestroy(int... entityIds) {
        this.entityIds = entityIds;
    }

    public int[] getEntityIds() {
        return entityIds;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeVarInt(output, entityIds.length);
        for (int entityId : entityIds) {
            DataTypeIO.writeVarInt(output, entityId);
        }
        return buffer.toByteArray();
    }
}
