package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class PacketPlayOutTabComplete extends PacketOut {
    public static final byte PACKET_ID = 0x0D;

    private final int id;
    private final int start;
    private final int length;
    private TabCompleteMatches[] matches;

    public PacketPlayOutTabComplete(int id, int start, int length, TabCompleteMatches... matches) {
        this.id = id;
        this.start = start;
        this.length = length;
        this.matches = matches;
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public TabCompleteMatches[] getMatches() {
        return matches;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeVarInt(output, id);
        DataTypeIO.writeVarInt(output, start);
        DataTypeIO.writeVarInt(output, length);
        DataTypeIO.writeVarInt(output, matches.length);
        for (TabCompleteMatches match : matches) {
            DataTypeIO.writeString(output, match.getMatch(), StandardCharsets.UTF_8);
            if (match.getTooltip().isPresent()) {
                output.writeBoolean(true);
                DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(match.getTooltip().get()), StandardCharsets.UTF_8);
            } else output.writeBoolean(false);
        }
        return buffer.toByteArray();
    }

    public static class TabCompleteMatches {
        private final String match;
        private final Optional<Component> tooltip;

        public TabCompleteMatches(String match) {
            this.match = match;
            this.tooltip = Optional.empty();
        }

        public TabCompleteMatches(String match, Component tooltip) {
            this.match = match;
            this.tooltip = Optional.ofNullable(tooltip);
        }

        public String getMatch() {
            return match;
        }

        public Optional<Component> getTooltip() {
            return tooltip;
        }
    }
}
