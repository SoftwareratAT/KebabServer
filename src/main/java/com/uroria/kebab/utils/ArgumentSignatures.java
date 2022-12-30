package com.uroria.kebab.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArgumentSignatures {
    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(Collections.emptyList());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    private List<a> entries;

    public ArgumentSignatures(List<a> entries) {
        this.entries = entries;
    }

    public ArgumentSignatures(DataInputStream in) throws IOException {
        int size = DataTypeIO.readVarInt(in);
        entries = new ArrayList<>(8);
        for (int i = 0; i < size; i++) {
            entries.add(new ArgumentSignatures.a(in));
        }
    }

    public MessageSignature get(String s) {
        Iterator<a> iterator = this.entries.iterator();

        ArgumentSignatures.a argumentsignatures_a;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            argumentsignatures_a = iterator.next();
        } while (!argumentsignatures_a.name.equals(s));

        return argumentsignatures_a.signature;
    }

    public void write(DataOutputStream out) throws IOException {
        DataTypeIO.writeVarInt(out, this.entries.size());
        for (a argumentsignatures_a : this.entries) {
            argumentsignatures_a.write(out);
        }
    }

    public static class a {

        private final String name;
        private final MessageSignature signature;

        public a(String name, MessageSignature signature) {
            this.name = name;
            this.signature = signature;
        }

        public a(DataInputStream in) throws IOException {
            this(DataTypeIO.readString(in, StandardCharsets.UTF_8), MessageSignature.read(in));
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeString(out, this.name, StandardCharsets.UTF_8);
            MessageSignature.write(out, this.signature);
        }
    }

    @FunctionalInterface
    public interface b {

        MessageSignature sign(String s);

    }
}
