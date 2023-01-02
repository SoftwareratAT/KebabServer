package com.uroria.kebab.network;

import com.uroria.kebab.network.protocol.PacketIn;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.Pair;
import net.kyori.adventure.key.Key;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel implements AutoCloseable {
    private final List<Pair<Key, ChannelPacketHandler>> handlers;
    private final AtomicBoolean valid;
    protected final DataInputStream input;
    protected final DataOutputStream output;

    public Channel(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        this.handlers = new CopyOnWriteArrayList<>();
        this.valid = new AtomicBoolean(true);
    }

    private void ensureOpen() {
        if (!valid.get()) close();
    }

    public void addHandlerBefore(Key key, ChannelPacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    public void addHandlerAfter(Key key, ChannelPacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    public void removeHandler(Key key) {
        handlers.removeIf(handler -> handler.getFirst().equals(key));
    }

    protected PacketIn readPacket() throws Exception {
        return readPacket(-1);
    }

    protected PacketIn readPacket(int size) throws IOException {
        PacketIn packet = null;
        do {
            ensureOpen();
            size = size < 0 ? DataTypeIO.readVarInt(input) : size;
            int packetId = DataTypeIO.readVarInt(input);
            ChannelPacketRead read = new ChannelPacketRead(size, packetId, input);
            for (Pair<Key, ChannelPacketHandler> pair : handlers) {
                read = pair.getSecond().read(read);
                if (read == null) {
                    packet = null;
                    break;
                }
                packet = read.getReadPacket();
            }
            size = -1;
        } while (packet == null);
        System.out.println("return");
        return packet;
    }

    protected boolean writePacket(PacketOut packet) throws IOException {
        ensureOpen();
        ChannelPacketWrite write = new ChannelPacketWrite(packet);
        for (Pair<Key, ChannelPacketHandler> pair : handlers) {
            write = pair.getSecond().write(write);
            if (write == null) return false;
        }
        packet = write.getPacket();
        byte[] packetByte = packet.serializePacket();
        DataTypeIO.writeVarInt(output, packetByte.length);
        output.write(packetByte);
        output.flush();
        return true;
    }

    @Override
    public void close() {
        if (valid.compareAndSet(true, false)) {
            try {
                input.close();
                output.close();
            } catch (Exception ignored) {}
        }
    }
}
