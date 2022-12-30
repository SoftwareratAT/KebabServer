package com.uroria.kebab.network.protocol.minecraft.play.in;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayInTabComplete extends PacketIn {
    public static final byte PACKET_ID = 0x08;
    static {
        addPlayInPacket(PACKET_ID, PacketPlayInTabComplete.class);
    }

    private final int id;
    private final String text;

    public PacketPlayInTabComplete(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public PacketPlayInTabComplete(DataInputStream input) throws IOException {
        this(DataTypeIO.readVarInt(input), DataTypeIO.readString(input, StandardCharsets.UTF_8));
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
