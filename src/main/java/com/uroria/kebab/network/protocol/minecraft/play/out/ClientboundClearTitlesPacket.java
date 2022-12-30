package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ClientboundClearTitlesPacket extends PacketOut {
    public static final byte PACKET_ID = 0x0C;

    private final boolean reset;
    public ClientboundClearTitlesPacket(boolean reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return reset;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.writeBoolean(reset);
        return buffer.toByteArray();
    }
}
