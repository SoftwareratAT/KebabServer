package com.uroria.kebab.entity;

public enum Pose {
    STANDING(0),
    FALL_FLYING(1),
    SLEEPING(2),
    SWIMMING(3),
    SPIN_ATTACK(4),
    SNEAKING(5),
    DYING(6);

    private static final Pose[] VALUES = values();

    private int id;

    Pose(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Pose fromId(int id) {
        for (Pose pose : VALUES) {
            if (id == pose.id) {
                return pose;
            }
        }
        return null;
    }
}
