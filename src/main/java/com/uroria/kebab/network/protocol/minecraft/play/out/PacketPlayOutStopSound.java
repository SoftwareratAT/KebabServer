package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class PacketPlayOutStopSound extends PacketOut {
    public static final byte PACKET_ID = 0x5F;

    private final Key sound;
    private final Sound.Source source;

    public PacketPlayOutStopSound(Key sound, Sound.Source source) {
        this.sound = sound;
        this.source = source;
    }

    public Key getSound() {
        return sound;
    }

    public Sound.Source getSource() {
        return source;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        if (source != null) {
            if (sound != null) {
                output.writeByte(3);
                DataTypeIO.writeVarInt(output, source.ordinal());
                DataTypeIO.writeString(output, sound.toString(), StandardCharsets.UTF_8);
            } else {
                output.writeByte(1);
                DataTypeIO.writeVarInt(output, source.ordinal());
            }
        } else if (sound != null) {
            output.writeByte(2);
            DataTypeIO.writeString(output, sound.toString(), StandardCharsets.UTF_8);
        } else {
            output.writeByte(0);
        }
        return buffer.toByteArray();
    }
}