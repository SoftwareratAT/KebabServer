package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.ArgumentSignatures;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.LastSeenMessages;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class ServerBoundChatCommandPacket extends PacketIn {
    public static final byte PACKET_ID = 0x04;
    static {
        addPlayInPacket(PACKET_ID, ServerBoundChatCommandPacket.class);
    }

    private final String command;
    private final Instant time;
    private final long salt;
    private final ArgumentSignatures argumentSignatures;
    private final LastSeenMessages.b lastSeenMessages;

    public ServerBoundChatCommandPacket(String command, Instant time, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.b lastSeenMessages) {
        this.command = command;
        this.time = time;
        this.salt = salt;
        this.argumentSignatures = argumentSignatures;
        this.lastSeenMessages = lastSeenMessages;
    }

    public ServerBoundChatCommandPacket(DataInputStream input) throws IOException {
        this.command = DataTypeIO.readString(input, StandardCharsets.UTF_8);
        this.time = Instant.ofEpochMilli(input.readLong());
        this.salt = input.readLong();
        this.argumentSignatures = new ArgumentSignatures(input);
        this.lastSeenMessages = new LastSeenMessages.b(input);
    }

    public String getCommand() {
        return command;
    }

    public Instant getTime() {
        return time;
    }

    public long getSalt() {
        return salt;
    }

    public ArgumentSignatures getArgumentSignatures() {
        return argumentSignatures;
    }
}
