package com.uroria.kebab.network.protocol.minecraft.login.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketLoginOutDisconnect extends PacketOut {
    private final Component reason;
    public PacketLoginOutDisconnect(Component reason) {
        this.reason = reason;
    }

    public Component getReason() {
        return reason;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x00);
        DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(reason), StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }
}
