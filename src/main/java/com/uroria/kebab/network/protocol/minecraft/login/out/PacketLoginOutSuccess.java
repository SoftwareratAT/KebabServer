package com.uroria.kebab.network.protocol.minecraft.login.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketLoginOutSuccess extends PacketOut {
    private final UUID uuid;
    private final String username;

    public PacketLoginOutSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x02);
        DataTypeIO.writeUUID(output, uuid);
        DataTypeIO.writeString(output, username, StandardCharsets.UTF_8);
        DataTypeIO.writeVarInt(output, 0);
        return buffer.toByteArray();
    }
}
