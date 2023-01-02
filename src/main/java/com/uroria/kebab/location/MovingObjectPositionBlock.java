package com.uroria.kebab.location;

import com.uroria.kebab.utils.minecraft.BlockPosition;

public class MovingObjectPositionBlock extends MovingObjectPosition {
    private final BlockFace direction;
    private final BlockPosition blockPosition;
    private final boolean miss;
    private final boolean inside;

    public static MovingObjectPosition miss(Vector vector, BlockFace direction, BlockPosition blockPosition) {
        return new MovingObjectPositionBlock(true, vector, direction, blockPosition, false);
    }

    public MovingObjectPositionBlock(Vector vector, BlockFace direction, BlockPosition blockPosition, boolean flag) {
        this(false, vector, direction, blockPosition, flag);
    }

    private MovingObjectPositionBlock(boolean flag, Vector vector, BlockFace direction, BlockPosition blockPosition, boolean flag1) {
        super(vector);
        this.miss = flag;
        this.direction = direction;
        this.blockPosition = blockPosition;
        this.inside = flag1;
    }

    public MovingObjectPositionBlock withDirection(BlockFace direction) {
        return new MovingObjectPositionBlock(this.miss, this.location, direction, this.blockPosition, this.inside);
    }

    public MovingObjectPositionBlock withPosition(BlockPosition blockPosition) {
        return new MovingObjectPositionBlock(this.miss, this.location, this.direction, blockPosition, this.inside);
    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }

    public BlockFace getDirection() {
        return this.direction;
    }

    @Override
    public MovingObjectType getType() {
        return this.miss ? MovingObjectType.MISS : MovingObjectType.BLOCK;
    }

    public boolean isInside() {
        return this.inside;
    }
}
