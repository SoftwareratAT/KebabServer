package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundSetTitlesAnimationPacket extends PacketOut {
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    public ClientboundSetTitlesAnimationPacket(int fadeIn, int stay, int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x5C);
        output.writeInt(fadeIn);
        output.writeInt(stay);
        output.writeInt(fadeOut);
        return buffer.toByteArray();
    }
}
