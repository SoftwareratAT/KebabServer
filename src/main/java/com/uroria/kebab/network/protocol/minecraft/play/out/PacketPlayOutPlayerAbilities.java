package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutPlayerAbilities extends PacketOut {
    public static final byte PACKET_ID = 0x30;

    public enum PlayerAbilityFlags {
        INVULNERABLE(0x01),
        FLY(0x02),
        ALLOW_FLYING(0x04),
        CREATIVE(0x08);

        private final int bitvalue;

        PlayerAbilityFlags(int bitvalue) {
            this.bitvalue = bitvalue;
        }

        public int getBitvalue() {
            return bitvalue;
        }
    }

    private final PlayerAbilityFlags[] flags;
    private final float flySpeed;
    private final float fieldOfField;

    public PacketPlayOutPlayerAbilities(float flySpeed, float fieldOfField, PlayerAbilityFlags... flags) {
        this.flags = flags;
        this.flySpeed = flySpeed;
        this.fieldOfField = fieldOfField;
    }

    public PlayerAbilityFlags[] getFlags() {
        return flags;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public float getFieldOfField() {
        return fieldOfField;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        int value = 0;
        for (PlayerAbilityFlags flag : flags) {
            value = value | flag.getBitvalue();
        }
        output.writeByte(value);
        output.writeFloat(flySpeed);
        output.writeFloat(fieldOfField);
        return buffer.toByteArray();
    }
}
