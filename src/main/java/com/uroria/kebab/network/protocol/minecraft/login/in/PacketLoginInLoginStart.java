package com.uroria.kebab.network.protocol.minecraft.login.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PacketLoginInLoginStart extends PacketIn {
    public static byte PACKET_ID = 0x00;
    static {
        addLoginInPacket(PACKET_ID, PacketLoginInLoginStart.class);
    }
    private final String username;
    private final Optional<UUID> uuid;

    public PacketLoginInLoginStart(DataInputStream input) throws IOException {
        this.username = DataTypeIO.readString(input, StandardCharsets.UTF_8);
        if (input.readBoolean()) this.uuid = Optional.of(DataTypeIO.readUUID(input));
        else this.uuid = Optional.empty();
    }

    public String getUsername() {
        return this.username;
    }

    public boolean hasUniqueId() {
        return this.uuid.isPresent();
    }

    public UUID getUniqueId() {
        return this.uuid.orElse(null);
    }
}
