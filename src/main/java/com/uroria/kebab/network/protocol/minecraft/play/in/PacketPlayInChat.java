package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.LastSeenMessages;
import com.uroria.kebab.utils.MessageSignature;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class PacketPlayInChat extends PacketIn {
    public static final byte PACKET_ID = 0x05;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInChat.class);
    }

    private final String message;
    private final Instant time;
    private final long salt;
    private final MessageSignature signature;
    private final LastSeenMessages.b lastSeenMessages;

    public PacketPlayInChat(String message, Instant time, long salt, MessageSignature signature, LastSeenMessages.b lastSeenMessages) {
        this.message = message;
        this.time = time;
        this.salt = salt;
        this.signature = signature;
        this.lastSeenMessages = lastSeenMessages;
    }

    public PacketPlayInChat(DataInputStream input) throws IOException {
        this(DataTypeIO.readString(input, StandardCharsets.UTF_8), Instant.ofEpochMilli(input.readLong()), input.readLong(), input.readBoolean() ? MessageSignature.read(input) : null, new LastSeenMessages.b(input));
    }

    public String getMessage() {
        return message;
    }

    public Instant getTime() {
        return time;
    }

    public long getSalt() {
        return salt;
    }

    public MessageSignature getSignature() {
        return signature;
    }

    public LastSeenMessages.b getLastSeenMessages() {
        return lastSeenMessages;
    }
}
