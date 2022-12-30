package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.item.ItemStack;
import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PacketPlayOutWindowItems extends PacketOut {
    private final int containerId;
    private final int stateId;
    private final List<ItemStack> items;
    private final ItemStack carriedItem;

    public PacketPlayOutWindowItems(int containerId, int stateId, List<ItemStack> items, ItemStack carriedItem) {
        this.containerId = containerId;
        this.stateId = stateId;
        this.items = items;
        this.carriedItem = carriedItem;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getStateId() {
        return stateId;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x10);
        output.writeByte(containerId);
        DataTypeIO.writeVarInt(output, stateId);
        DataTypeIO.writeVarInt(output, items.size());
        for (ItemStack itemStack: items) {
            DataTypeIO.writeItemStack(output, itemStack);
        }
        DataTypeIO.writeItemStack(output, carriedItem);
        return buffer.toByteArray();
    }
}
