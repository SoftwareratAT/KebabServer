package com.uroria.kebab.network.protocol;

import java.util.HashMap;
import java.util.Map;

public class Packet {
    private static final Map<Integer, Class<? extends PacketIn>> HANDSHAKE_IN = new HashMap<>();

    private static final Map<Integer, Class<? extends PacketIn>> LOGIN_IN = new HashMap<>();

    private static final Map<Integer, Class<? extends PacketIn>> PLAY_IN = new HashMap<>();

    public static Map<Integer, Class<? extends PacketIn>> getHandshakeIn() {
        return HANDSHAKE_IN;
    }

    public static Map<Integer, Class<? extends PacketIn>> getLoginIn() {
        return LOGIN_IN;
    }

    public static Map<Integer, Class<? extends PacketIn>> getPlayIn() {
        return PLAY_IN;
    }

    protected static void addLoginInPacket(byte packetId, Class<? extends PacketIn> packetClass) {
        getLoginIn().put(decodeByteToInteger(packetId), packetClass);
    }

    protected static void addHandshakeInPacket(byte packetId, Class<? extends PacketIn> packetClass) {
        getHandshakeIn().put(decodeByteToInteger(packetId), packetClass);
    }

    protected static void addPlayInPacket(byte packetId, Class<? extends PacketIn> packetClass) {
        getPlayIn().put(decodeByteToInteger(packetId), packetClass);
    }

    private static int decodeByteToInteger(byte input) {
        return Integer.decode("" + input);
    }
}
