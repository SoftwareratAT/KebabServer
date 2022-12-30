package com.uroria.kebab.utils.minecraft;

import com.uroria.kebab.location.Location;

public final class BlockPosition {
    private final int x;
    private final int y;
    private final int z;
    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static BlockPosition from(Location location) {
        return new BlockPosition((int) Math.floor(location.getX()), (int) Math.floor(location.getY()), (int) Math.floor(location.getZ()));
    }
}
