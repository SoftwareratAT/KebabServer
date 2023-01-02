package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.entity.DataWatcher.WatchableObjectType;
import com.uroria.kebab.entity.DataWatcher.WatchableObject;
import com.uroria.kebab.entity.Entity;
import com.uroria.kebab.entity.Pose;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.minecraft.BlockPosition;
import com.uroria.kebab.utils.minecraft.Rotation3f;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;


public class PacketPlayOutEntityMetadata extends PacketOut {
    public static final byte PACKET_ID = 0x4E;

    private static final int END_OFF_METADATA = 0xff;

    private Entity entity;
    public boolean allFields;
    public Field[] fields;

    public PacketPlayOutEntityMetadata(Entity entity, boolean allFields, Field... fields) {
        this.entity = entity;
        this.allFields = allFields;
        this.fields = fields;
    }

    public PacketPlayOutEntityMetadata(Entity entity) {
        this(entity, true);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeVarInt(output, entity.getEntityId());
        Collection<WatchableObject> watches;
        if (allFields) {
            watches = new HashSet<>(entity.getDataWatcher().getWatchableObjects().values());
        } else {
            watches = new HashSet<>();
            Map<Field, WatchableObject> entries = entity.getDataWatcher().getWatchableObjects();
            for (Field field : fields) {
                WatchableObject watch = entries.get(field);
                if (watch != null) {
                    watches.add(watch);
                }
            }
        }
        Map<Integer, Integer> bitmasks = new HashMap<>();
        Iterator<WatchableObject> itr = watches.iterator();
        while (itr.hasNext()) {
            WatchableObject watch = itr.next();
            if (watch.isBitmask()) {
                itr.remove();
                Integer bitmask = bitmasks.get(watch.getIndex());
                if (bitmask == null) {
                    bitmask = 0;
                }
                if ((boolean) watch.getValue()) {
                    bitmask |= watch.getBitmask();
                } else {
                    bitmask &= ~watch.getBitmask();
                }
                bitmasks.put(watch.getIndex(), bitmask);
            }
        }
        for (Entry<Integer, Integer> entry : bitmasks.entrySet()) {
            watches.add(new WatchableObject(entry.getValue().byteValue(), entry.getKey(), WatchableObjectType.BYTE));
        }
        for (WatchableObject watch : watches) {
            output.writeByte(watch.getIndex());
            if (watch.isOptional()) {
                DataTypeIO.writeVarInt(output, watch.getType().getOptionalTypeId());
                output.writeBoolean(watch.getValue() != null);
            } else {
                DataTypeIO.writeVarInt(output, watch.getType().getTypeId());
            }
            if (!watch.isOptional() || watch.getValue() != null) {
                switch (watch.getType()) {
                    case POSITION -> DataTypeIO.writeBlockPosition(output, (BlockPosition) watch.getValue());
                    case BOOLEAN -> output.writeBoolean((boolean) watch.getValue());
                    case BYTE -> output.writeByte((byte) watch.getValue());
                    case CHAT ->
                            DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize((Component) watch.getValue()), StandardCharsets.UTF_8);
                    case FLOAT -> output.writeFloat((float) watch.getValue());
                    case POSE -> DataTypeIO.writeVarInt(output, ((Pose) watch.getValue()).getId());
                    case ROTATION -> {
                        Rotation3f rotation = (Rotation3f) watch.getValue();
                        output.writeFloat((float) rotation.getX());
                        output.writeFloat((float) rotation.getY());
                        output.writeFloat((float) rotation.getZ());
                    }
                    case STRING -> DataTypeIO.writeString(output, watch.getValue().toString(), StandardCharsets.UTF_8);
                    case UUID -> DataTypeIO.writeUUID(output, (UUID) watch.getValue());
                    case VARINT -> DataTypeIO.writeVarInt(output, (int) watch.getValue());
                    default -> {
                    }
                }
            }
        }
        output.writeByte(END_OFF_METADATA);
        return buffer.toByteArray();
    }

}
