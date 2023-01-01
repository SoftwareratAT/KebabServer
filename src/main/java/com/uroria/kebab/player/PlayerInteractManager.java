package com.uroria.kebab.player;

import com.google.common.collect.ImmutableList;
import com.uroria.kebab.KebabServer;
import com.uroria.kebab.entity.Entity;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.network.protocol.minecraft.play.out.*;
import com.uroria.kebab.world.ChunkPosition;
import com.uroria.kebab.world.World;
import net.querz.mca.Chunk;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerInteractManager {
    private final KebabPlayer player;
    private Set<Entity> entities;
    private Map<ChunkPosition, Chunk> currentViewing;

    public PlayerInteractManager(KebabPlayer player) {
        this.player = player;
        this.entities = new HashSet<>();
        this.currentViewing = new HashMap<>();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void update() throws IOException {
        int viewDistanceChunks = KebabServer.getInstance().getViewDistance();
        int viewDistanceBlocks = viewDistanceChunks << 4;
        Location location = player.getLocation();
        Set<Entity> entitiesInRange = player.getWorld().getEntities().stream().filter(entity -> entity.getLocation().distanceSquared(location) < viewDistanceBlocks * viewDistanceBlocks).collect(Collectors.toSet());
        for (Entity entity : entitiesInRange) {
            if (!entities.contains(entity)) {
                PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(entity.getEntityId(), entity.getUniqueId(), entity.getType(), entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch(), (float) 0, 0, (short) 0, (short) 0, (short) 0);
                player.sendPacket(spawnPacket);

                PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(entity);
                player.sendPacket(metaPacket);
            }
        }
        List<Integer> ids = new ArrayList<>();
        for (Entity entity : entities) {
            if (!entitiesInRange.contains(entity)) {
                ids.add(entity.getEntityId());
            }
        }
        for (int id : ids) {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
            player.sendPacket(destroyPacket);
        }
        this.entities = entitiesInRange;
        int playerChunkX = (int) location.getX() >> 4;
        int playerChunkZ = (int) location.getZ() >> 4;
        World world = location.getWorld();
        Map<ChunkPosition, Chunk> chunksInRange = new HashMap<>();
        for (int x = playerChunkX - viewDistanceChunks; x < playerChunkX + viewDistanceChunks; x++) {
            for (int z = playerChunkZ - viewDistanceChunks; z < playerChunkZ + viewDistanceChunks; z++) {
                Chunk chunk= world.getChunkAt(x, z);
                if (chunk != null) chunksInRange.put(new ChunkPosition(world, x, z), chunk);
                else chunksInRange.put(new ChunkPosition(world, x, z), World.EMPTY_CHUNK);
            }
        }

        for (Map.Entry<ChunkPosition, Chunk> entry : currentViewing.entrySet()) {
            ChunkPosition chunkPosition = entry.getKey();
            if (!chunksInRange.containsKey(chunkPosition)) {
                PacketPlayOutUnloadChunk unloadChunkPacket = new PacketPlayOutUnloadChunk(chunkPosition.getChunkX(), chunkPosition.getChunkZ());
                player.sendPacket(unloadChunkPacket);
            }
        }

        for (Map.Entry<ChunkPosition, Chunk> entry : chunksInRange.entrySet()) {
            ChunkPosition chunkPosition = entry.getKey();
            if (!currentViewing.containsKey(chunkPosition)) {
                Chunk chunk = chunkPosition.getWorld().getChunkAt(chunkPosition.getChunkX(), chunkPosition.getChunkZ());
                if (chunk == null) {
                    ClientboundLevelChunkWithLightPacket chunkData = new ClientboundLevelChunkWithLightPacket(chunkPosition.getChunkX(), chunkPosition.getChunkZ(), entry.getValue(), world.getEnvironment(), true, new ArrayList<>(), new ArrayList<>());
                    player.sendPacket(chunkData);
                } else {
                    List<Byte[]> blockChunk = world.getLightEngineBlock().getBlockLightBitMask(chunkPosition.getChunkX(), chunkPosition.getChunkZ());
                    if (blockChunk == null) blockChunk = new ArrayList<>();
                    List<Byte[]> skyChunk = null;
                    if (world.hasSkyLight()) skyChunk = world.getLightEngineSky().getSkyLightBitMask(chunkPosition.getChunkX(), chunkPosition.getChunkZ());
                    if (skyChunk == null) skyChunk = new ArrayList<>();
                    ClientboundLevelChunkWithLightPacket chunkData = new ClientboundLevelChunkWithLightPacket(chunkPosition.getChunkX(), chunkPosition.getChunkZ(), chunk, world.getEnvironment(), true, skyChunk, blockChunk);
                    player.sendPacket(chunkData);
                }
            }
        }
        this.currentViewing = chunksInRange;
    }
}
