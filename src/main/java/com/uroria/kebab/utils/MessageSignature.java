package com.uroria.kebab.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
public class MessageSignature {
    public static final int BYTES = 256;

    private final byte[] bytes;

    public MessageSignature(byte[] abyte) {
        this.bytes = abyte;
    }

    public static MessageSignature read(DataInputStream in) throws IOException {
        byte[] abyte = new byte[256];
        in.readFully(abyte);
        return new MessageSignature(abyte);
    }

    public static void write(DataOutputStream out, MessageSignature messagesignature) throws IOException {
        out.write(messagesignature.bytes);
    }
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.bytes);
    }

    public boolean equals(Object object) {
        boolean flag;

        if (this != object) {
            label22:
            {
                if (object instanceof MessageSignature) {
                    MessageSignature messagesignature = (MessageSignature) object;

                    if (Arrays.equals(this.bytes, messagesignature.bytes)) {
                        break label22;
                    }
                }

                flag = false;
                return flag;
            }
        }

        flag = true;
        return flag;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }

    public static class a {

        public static final int FULL_SIGNATURE = -1;

        private final int id;
        private final MessageSignature fullSignature;

        public a(int id, MessageSignature messagesignature) {
            this.id = id;
            this.fullSignature = messagesignature;
        }

        public a(MessageSignature messagesignature) {
            this(-1, messagesignature);
        }

        public a(int i) {
            this(i, null);
        }

        public int id() {
            return id;
        }

        public MessageSignature fullSignature() {
            return fullSignature;
        }

        public static MessageSignature.a read(DataInputStream in) throws IOException {
            int i = DataTypeIO.readVarInt(in) - 1;
            return i == -1 ? new MessageSignature.a(MessageSignature.read(in)) : new MessageSignature.a(i);
        }

        public static void write(DataOutputStream out, MessageSignature.a messagesignature_a) throws IOException {
            DataTypeIO.writeVarInt(out, messagesignature_a.id() + 1);
            if (messagesignature_a.fullSignature() != null) {
                MessageSignature.write(out, messagesignature_a.fullSignature());
            }

        }
    }
}
