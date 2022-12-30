package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutDisconnect extends PacketOut {
    private final Component reason;
    public PacketPlayOutDisconnect(Component reason) {
        this.reason = reason;
    }

    public Component getReason() {
        return reason;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x17);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(reason), StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }
}