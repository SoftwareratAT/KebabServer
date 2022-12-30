package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ClientboundSystemChatPacket extends PacketOut {
    public static final byte PACKET_ID = 0x60;

    private final Component message;
    private final boolean overlay;
    public ClientboundSystemChatPacket(Component message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    public Component getMessage() {
        return message;
    }

    public boolean isOverlay() {
        return overlay;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(message), StandardCharsets.UTF_8);
        output.writeBoolean(this.overlay);
        return buffer.toByteArray();
    }
}
