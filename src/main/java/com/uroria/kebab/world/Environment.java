package com.uroria.kebab.world;

import net.kyori.adventure.key.Key;

import java.util.HashSet;
import java.util.Set;

public class Environment {

    public static final Environment NORMAL = new Environment(Key.key("minecraft:overworld"), true);
    public static final Environment NETHER = new Environment(Key.key("minecraft:the_nether"), false);
    public static final Environment END = new Environment(Key.key("minecraft:the_end"), false);

    public static final Set<Environment> REGISTERED_ENVIRONMENTS = new HashSet<>();

    public static Environment fromKey(Key key) {
        if (key.equals(NORMAL.getKey())) return NORMAL;
        if (key.equals(NETHER.getKey())) return NETHER;
        if (key.equals(END.getKey())) return END;
        return null;
    }

    public static Environment createCustom(Key key, boolean hasSkyLight) {
        if (REGISTERED_ENVIRONMENTS.stream().anyMatch(environment -> environment.getKey().equals(key))) {
            throw new IllegalArgumentException("An Environment is already created with this Key!");
        }
        return new Environment(key, hasSkyLight);
    }

    //=======================

    private final Key key;
    private final boolean hasSkyLight;

    private Environment(Key key, boolean hasSkyLight) {
        this.key = key;
        this.hasSkyLight = hasSkyLight;
    }

    public Key getKey() {
        return key;
    }

    public boolean hasSkyLight() {
        return hasSkyLight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (hasSkyLight ? 1231 : 1237);
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;
        Environment other = (Environment) object;
        if (hasSkyLight != other.hasSkyLight) return false;
        if (key == null) return false;
        if (other.key == null) return false;
        return key.equals(other.key);
    }
}
