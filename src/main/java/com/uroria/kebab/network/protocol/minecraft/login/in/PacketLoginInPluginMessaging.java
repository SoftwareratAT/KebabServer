package com.uroria.kebab.network.protocol.minecraft.login.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;

public class PacketLoginInPluginMessaging extends PacketIn {
    public static byte PACKET_ID = 0x02;
    static {
        addLoginInPacket(PACKET_ID, PacketLoginInPluginMessaging.class);
    }

    private final int messageId;
    private final boolean successful;
    private final Optional<byte[]> data;

    public PacketLoginInPluginMessaging(int messageId, boolean successful, byte[] data) {
        this.messageId = messageId;
        this.successful = successful;
        this.data = successful ? Optional.of(data) : Optional.empty();
    }

    public PacketLoginInPluginMessaging(DataInputStream input, int packetLength, int packetId) throws IOException {
        messageId = DataTypeIO.readVarInt(input);
        successful = input.readBoolean();
        if (successful) {
            int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getVarIntLength(messageId) -1;
            if (dataLength != 0) {
                byte[] data = new byte[dataLength];
                input.readFully(data);
                this.data = Optional.of(data);
            } else this.data = Optional.of(new byte[0]);
        } else data = Optional.empty();
    }

    public int getMessageId() {
        return messageId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Optional<byte[]> getData() {
        return data;
    }
}
