package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.Packet;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PacketPlayOutPositionAndLook extends PacketOut {
    public static final byte PACKET_ID = 0x38;

    public enum PlayerTeleportFlags {
        X((byte) 0x01),
        Y((byte) 0x02),
        Z((byte) 0x04),
        X_ROT((byte) 0x08),
        Y_ROT((byte) 0x10);

        private final byte bit;

        PlayerTeleportFlags(byte bit) {
            this.bit = bit;
        }

        public byte getBit() {
            return bit;
        }
    }

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final Set<PlayerTeleportFlags> flags;
    private final int teleportId;
    private final boolean dismountVehicle;
    public PacketPlayOutPositionAndLook(double x, double y, double z, float yaw, float pitch, int teleportId, boolean dismountVehicle, PlayerTeleportFlags... flags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.teleportId = teleportId;
        this.flags = new HashSet<>(Arrays.asList(flags));
        this.dismountVehicle = dismountVehicle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Set<PlayerTeleportFlags> getFlags() {
        return flags;
    }

    public int getTeleportId() {
        return teleportId;
    }

    public boolean isDismountVehicle() {
        return dismountVehicle;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.writeDouble(x);
        output.writeDouble(y);
        output.writeDouble(z);
        output.writeFloat(yaw);
        output.writeFloat(pitch);
        byte flag = 0;
        for (PlayerTeleportFlags flag1 : flags) {
            flag = (byte) (flag | flag1.getBit());
        }
        output.writeByte(flag);
        DataTypeIO.writeVarInt(output, teleportId);
        output.writeBoolean(dismountVehicle);
        return buffer.toByteArray();
    }
}
