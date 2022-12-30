package com.uroria.kebab.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class LastSeenMessages {
    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(Collections.emptyList());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    private List<MessageSignature> entries;

    public static class a {

        public static final LastSeenMessages.a EMPTY = new LastSeenMessages.a(Collections.emptyList());

        private final List<MessageSignature.a> entries;

        public a(List<MessageSignature.a> entries) {
            this.entries = entries;
        }

        public a(DataInputStream in) throws IOException {
            int size = DataTypeIO.readVarInt(in);
            entries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                entries.add(MessageSignature.a.read(in));
            }
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeVarInt(out, this.entries.size());
            for (MessageSignature.a entry : this.entries) {
                MessageSignature.a.write(out, entry);
            }
        }
    }

    public static class b {

        private final int offset;
        private final BitSet acknowledged;

        public b(int offset, BitSet acknowledged) {
            this.offset = offset;
            this.acknowledged = acknowledged;
        }

        public b(DataInputStream in) throws IOException {
            this(DataTypeIO.readVarInt(in), DataTypeIO.readFixedBitSet(in, 20));
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeVarInt(out, this.offset);
            DataTypeIO.writeFixedBitSet(out, this.acknowledged, 20);
        }
    }
}
