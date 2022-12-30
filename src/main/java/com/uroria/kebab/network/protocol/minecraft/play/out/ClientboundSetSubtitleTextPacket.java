package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientboundSetSubtitleTextPacket extends PacketOut {
    private final Component subtitle;
    public ClientboundSetSubtitleTextPacket(Component subtitle) {
        this.subtitle = subtitle;
    }

    public Component getSubtitle() {
        return subtitle;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x59);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(subtitle), StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }
}
