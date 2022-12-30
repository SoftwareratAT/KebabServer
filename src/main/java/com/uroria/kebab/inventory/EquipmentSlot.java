package com.uroria.kebab.inventory;

public enum EquipmentSlot {
    MAINHAND,
    OFFHAND,
    HELMET,
    CHESTPLATE,
    LEGGINS,
    BOOTS;

    public boolean isHandSlot() {
        return this == MAINHAND || this == OFFHAND;
    }

    public boolean isArmorSlot() {
        return !isHandSlot();
    }
}
