package com.uroria.kebab.entity;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.entity.DataWatcher.WatchableField;
import com.uroria.kebab.entity.DataWatcher.WatchableObjectType;
import com.uroria.kebab.inventory.EquipmentSlot;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.utils.minecraft.BlockPosition;
import com.uroria.kebab.world.World;

import java.util.UUID;

public abstract class LivingEntity extends Entity {
    @WatchableField(MetadataIndex = 8, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x01)
    protected boolean handActive = false;
    @WatchableField(MetadataIndex = 8, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x02)
    protected boolean activeHand = false; //false = main hand, true = off hand
    @WatchableField(MetadataIndex = 8, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x04)
    protected boolean inRiptideSpinAttack = false;
    @WatchableField(MetadataIndex = 9, WatchableObjectType = WatchableObjectType.FLOAT)
    protected float health = 1.0F;
    @WatchableField(MetadataIndex = 10, WatchableObjectType = WatchableObjectType.VARINT)
    protected int potionEffectColor = 0;
    @WatchableField(MetadataIndex = 11, WatchableObjectType = WatchableObjectType.BOOLEAN)
    protected boolean potionEffectAmbient = false;
    @WatchableField(MetadataIndex = 12, WatchableObjectType = WatchableObjectType.VARINT)
    protected int arrowsInEntity = 0;
    @WatchableField(MetadataIndex = 13, WatchableObjectType = WatchableObjectType.VARINT)
    protected int absorption = 0;
    @WatchableField(MetadataIndex = 14, WatchableObjectType = WatchableObjectType.POSITION, IsOptional = true)
    protected BlockPosition sleepingLocation = null;

    public LivingEntity(EntityType type, int entityId, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
        super(type, entityId, uuid, world, x, y, z, yaw, pitch);
    }

    public LivingEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
        this(type, KebabServer.getInstance().getNextEntityId(), uuid, world, x, y, z, yaw, pitch);
    }

    public LivingEntity(EntityType type, World world, double x, double y, double z, float yaw, float pitch) {
        this(type, KebabServer.getInstance().getNextEntityId(), UUID.randomUUID(), world, x, y, z, yaw, pitch);
    }

    public LivingEntity(EntityType type, UUID uuid, Location location) {
        this(type, KebabServer.getInstance().getNextEntityId(), uuid, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public LivingEntity(EntityType type, Location location) {
        this(type, KebabServer.getInstance().getNextEntityId(), UUID.randomUUID(), location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public boolean isHandActive() {
        return handActive;
    }

    public void setHandActive(boolean handActive) {
        this.handActive = handActive;
    }

    public EquipmentSlot getActiveHand() {
        return activeHand ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
    }

    public void setActiveHand(EquipmentSlot activeHand) {
        if (activeHand.equals(EquipmentSlot.MAINHAND)) {
            this.activeHand = false;
        } else if (activeHand.equals(EquipmentSlot.OFFHAND)) {
            this.activeHand = true;
        } else {
            throw new IllegalArgumentException("Invalid EquipmentSlot " + activeHand.toString());
        }
    }

    public boolean isInRiptideSpinAttack() {
        return inRiptideSpinAttack;
    }

    public void setInRiptideSpinAttack(boolean inRiptideSpinAttack) {
        this.inRiptideSpinAttack = inRiptideSpinAttack;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public int getPotionEffectColor() {
        return potionEffectColor;
    }

    public void setPotionEffectColor(int potionEffectColor) {
        this.potionEffectColor = potionEffectColor;
    }

    public boolean isPotionEffectAmbient() {
        return potionEffectAmbient;
    }

    public void setPotionEffectAmbient(boolean potionEffectAmbient) {
        this.potionEffectAmbient = potionEffectAmbient;
    }

    public int getArrowsInEntity() {
        return arrowsInEntity;
    }

    public void setArrowsInEntity(int arrowsInEntity) {
        this.arrowsInEntity = arrowsInEntity;
    }

    public int getAbsorption() {
        return absorption;
    }

    public void setAbsorption(int absorption) {
        this.absorption = absorption;
    }

    public BlockPosition getSleepingLocation() {
        return sleepingLocation;
    }

    public void setSleepingLocation(BlockPosition sleepingLocation) {
        this.sleepingLocation = sleepingLocation;
    }
}
