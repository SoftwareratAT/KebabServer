package com.uroria.kebab.entity;

import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.player.Player;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum EntityType {
    PLAYER("player", KebabPlayer.class, 106, false),
    UNKNOWN(null, null, -1, false);
    private final String name;
    private final Class<? extends Entity> clazz;
    private final short typeId;
    private final boolean independent;
    private final boolean living;
    private final Key key;
    private static final Map<String, EntityType> NAME_MAP = new HashMap<>();
    private static final Map<Short, EntityType> ID_MAP = new HashMap<>();

    static {
        for (EntityType type : values()) {
            if (type.name != null) NAME_MAP.put(type.name.toLowerCase(Locale.ENGLISH), type);
            if (type.typeId > 0) ID_MAP.put(type.typeId, type);
        }
    }

    EntityType(String name, Class<? extends Entity> clazz, int typeId) {
        this(name, clazz, typeId, true);
    }

    EntityType(String name, Class<? extends Entity> clazz, int typeId, boolean independent) {
        this.name = name;
        this.clazz = clazz;
        this.typeId = (short) typeId;
        this.independent = independent;
        this.living = clazz != null && LivingEntity.class.isAssignableFrom(clazz);
        this.key = (name == null) ? null : Key.key(Key.MINECRAFT_NAMESPACE, name);
    }

    @Deprecated
    public String getName() {
        return this.name;
    }

    public Key getKey() {
        return this.key;
    }

    public Class<? extends Entity> getEntityClass() {
        return this.clazz;
    }

    public short getTypeId() {
        return this.typeId;
    }

    @Deprecated
    public static EntityType fromName(String name) {
        if (name == null) return null;
        return NAME_MAP.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Deprecated
    public static EntityType fromId(int id) {
        if (id > Short.MAX_VALUE) return null;
        return ID_MAP.get((short) id);
    }

    public boolean isSpawnAble() {
        return independent;
    }

    public boolean isAlive() {
        return living;
    }
}
