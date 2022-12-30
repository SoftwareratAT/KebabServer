package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundClearTitlesPacket extends PacketOut {
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
        output.writeByte(0x0C);
        output.writeBoolean(reset);
        return buffer.toByteArray();
    }
}
