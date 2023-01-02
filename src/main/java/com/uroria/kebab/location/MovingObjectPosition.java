package com.uroria.kebab.location;

import com.uroria.kebab.entity.Entity;

public abstract class MovingObjectPosition {
    protected final Vector location;

    protected MovingObjectPosition(Vector vector) {
        this.location = vector;
    }

    public double distanceTo(Entity entity) {
        double d0 = this.location.x - entity.getX();
        double d1 = this.location.y - entity.getY();
        double d2 = this.location.z - entity.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract MovingObjectPosition.MovingObjectType getType();

    public Vector getLocation() {
        return this.location;
    }


    public enum MovingObjectType {
        MISS,
        BLOCK,
        ENTITY
    }
}
