package com.uroria.kebab.utils.minecraft;

public enum GameMode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);
    private final int id;
    GameMode(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
