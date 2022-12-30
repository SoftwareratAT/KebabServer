package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ClientboundSetTitleTextPacket extends PacketOut {
    public static final byte PACKET_ID = 0x5B;

    private final Component title;
    public ClientboundSetTitleTextPacket(Component title) {
        this.title = title;
    }

    public Component getTitle() {
        return title;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(title), StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }
}
