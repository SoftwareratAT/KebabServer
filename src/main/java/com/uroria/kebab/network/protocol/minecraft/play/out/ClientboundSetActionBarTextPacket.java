package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ClientboundSetActionBarTextPacket extends PacketOut {
    public static final byte PACKET_ID = 0x42;

    private final Component actionBar;

    public ClientboundSetActionBarTextPacket(Component actionBar) {
        this.actionBar = actionBar;
    }

    public Component getActionBar() {
        return actionBar;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(actionBar), StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }
}
