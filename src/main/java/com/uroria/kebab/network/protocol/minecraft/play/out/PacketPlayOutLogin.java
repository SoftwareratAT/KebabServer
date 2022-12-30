package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.minecraft.GameMode;
import com.uroria.kebab.world.Environment;
import com.uroria.kebab.world.World;
import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class PacketPlayOutLogin extends PacketOut {
    public static final byte PACKET_ID = 0x24;

    private final int entityId;
    private final boolean isHardcore;
    private final GameMode gameMode;
    private final List<World> worlds;
    private final CompoundTag dimensionCodec;
    private final Environment dimension;
    private final World world;
    private final long hashedSeed;
    private final byte maxPlayers;
    private final int viewDistance;
    private final int simulationDistance;
    private final boolean reducedDebugInfo;
    private final boolean enableRespawnScreen;
    private final boolean isDebug;
    private final boolean isFlat;

    public PacketPlayOutLogin(int entityId, boolean isHardcore, GameMode gameMode, List<World> worlds, CompoundTag dimensionCodec, World world, long hashedSeed, byte maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean isDebug, boolean isFlat) {
        this.entityId = entityId;
        this.isHardcore = isHardcore;
        this.gameMode = gameMode;
        this.worlds = worlds;
        this.dimensionCodec = dimensionCodec;
        this.dimension = world.getEnvironment();
        this.world = world;
        this.hashedSeed = hashedSeed;
        this.maxPlayers = maxPlayers;
        this.viewDistance = viewDistance;
        this.simulationDistance = simulationDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
        this.isDebug = isDebug;
        this.isFlat = isFlat;
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isHardcore() {
        return isHardcore;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public CompoundTag getDimensionCodec() {
        return dimensionCodec;
    }

    public Environment getDimension() {
        return dimension;
    }

    public World getWorld() {
        return world;
    }

    public long getHashedSeed() {
        return hashedSeed;
    }

    public byte getMaxPlayers() {
        return maxPlayers;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getSimulationDistance() {
        return simulationDistance;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }

    public boolean isEnableRespawnScreen() {
        return enableRespawnScreen;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isFlat() {
        return isFlat;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        output.writeInt(entityId);
        output.writeBoolean(isHardcore);
        output.writeByte((byte) gameMode.getId());
        output.writeByte(-1);
        DataTypeIO.writeVarInt(output, worlds.size());
        for (World world1 : worlds) {
            DataTypeIO.writeString(output, Key.key(world1.getName()).toString(), StandardCharsets.UTF_8);
        }
        DataTypeIO.writeCompoundTag(output, dimensionCodec);
        DataTypeIO.writeString(output, world.getEnvironment().getKey().toString(), StandardCharsets.UTF_8);
        DataTypeIO.writeString(output, Key.key(world.getName()).toString(), StandardCharsets.UTF_8);
        output.writeLong(this.hashedSeed);
        DataTypeIO.writeVarInt(output, maxPlayers);
        DataTypeIO.writeVarInt(output, viewDistance);
        DataTypeIO.writeVarInt(output, simulationDistance);
        output.writeBoolean(reducedDebugInfo);
        output.writeBoolean(enableRespawnScreen);
        output.writeBoolean(isDebug);
        output.writeBoolean(isFlat);
        output.writeBoolean(false);
        return buffer.toByteArray();
    }
}
