package com.uroria.kebab.world;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.entity.DataWatcher;
import com.uroria.kebab.entity.Entity;
import com.uroria.kebab.entity.EntityType;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.network.protocol.minecraft.play.out.PacketPlayOutEntityDestroy;
import com.uroria.kebab.network.protocol.minecraft.play.out.PacketPlayOutEntityMetadata;
import com.uroria.kebab.player.Player;
import com.uroria.kebab.utils.minecraft.BlockPosition;
import com.uroria.kebab.utils.minecraft.BlockState;
import com.uroria.kebab.utils.minecraft.SchematicConversion;
import net.querz.mca.Chunk;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class World {
    public static final CompoundTag HEIGHT_MAP = new CompoundTag();
    public static final Chunk EMPTY_CHUNK = Chunk.newChunk();

    static {
        HEIGHT_MAP.putLongArray("MOTION_BLOCKING",
                new long[] {1371773531765642314L, 1389823183635651148L, 1371738278539598925L,
                        1389823183635388492L, 1353688558756731469L, 1389823114781694027L, 1317765589597723213L,
                        1371773531899860042L, 1389823183635651149L, 1371773462911685197L, 1389823183635650636L,
                        1353688626805119565L, 1371773531900123211L, 1335639250618849869L, 1371738278674077258L,
                        1389823114781694028L, 1353723811310638154L, 1371738278674077259L, 1335674228429068364L,
                        1335674228429067338L, 1335674228698027594L, 1317624576693539402L, 1335709481520370249L,
                        1299610178184057417L, 1335638906349064264L, 1299574993811968586L, 1299574924958011464L,
                        1299610178184056904L, 1299574924958011464L, 1299610109330100296L, 1299574924958011464L,
                        1299574924823793736L, 1299574924958011465L, 1281525273222484040L, 1299574924958011464L,
                        1281525273222484040L, 9548107335L});

        EMPTY_CHUNK.cleanupPalettesAndBlockStates();
        EMPTY_CHUNK.setHeightMaps(HEIGHT_MAP.clone());
        EMPTY_CHUNK.setBiomes(new int[256]);
        EMPTY_CHUNK.setTileEntities(new ListTag<CompoundTag>(CompoundTag.class));
    }

    private String name;
    private Environment environment;
    private Chunk[][] chunks;
    private int width;
    private int length;
    private LightEngineBlock lightEngineBlock;
    private LightEngineSky lightEngineSky;
    private Map<Entity, DataWatcher> entities;

    public World(String name, int width, int length, Environment environment) {
        this.name = name;
        this.environment = environment;
        this.chunks = new Chunk[(width >> 4) + 1][(length >> 4) + 1];
        this.width = width;
        this.length = length;

        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                chunks[x][z] = Chunk.newChunk();
                Chunk chunk = chunks[x][z];
                chunk.cleanupPalettesAndBlockStates();
                chunk.setHeightMaps(HEIGHT_MAP.clone());
                chunk.setBiomes(new int[256]);
                chunk.setTileEntities(new ListTag<>(CompoundTag.class));
            }
        }
        this.lightEngineBlock = new LightEngineBlock(this);
        if (environment.hasSkyLight()) this.lightEngineSky = new LightEngineSky(this);
        this.entities = new LinkedHashMap<>();
    }

    public LightEngineSky getLightEngineSky() {
        return this.lightEngineSky;
    }

    public LightEngineBlock getLightEngineBlock() {
        return this.lightEngineBlock;
    }

    public boolean hasSkyLight() {
        return this.lightEngineSky != null;
    }

    protected void setBlock(int x, int y, int z, String blockData) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(z >> 4)] = chunk;
        }
        CompoundTag block = SchematicConversion.toBlockTag(blockData);
        chunk.setBlockStateAt(x, y, z, block, false);
    }

    public BlockState getBlock(int x, int y, int z) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(x >> 4)] = chunk;
        }

        CompoundTag tag = chunk.getBlockStateAt(x, y, z);
        if (tag == null) {
            tag = new CompoundTag();
            tag.putString("Name", "minecraft:air");
        }
        return new BlockState(tag);
    }

    public BlockState getBlock(BlockPosition blockPosition) {
        return getBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    public void setBlock(int x, int y, int z, BlockState blockState) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(z >> 4)] = chunk;
        }
        chunk.setBlockStateAt(x % 16, y % 16, z % 16, blockState.toCompoundTag(), false);
    }

    public void setBlock(BlockPosition blockPosition, BlockState blockState) {
        setBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), blockState);
    }

    public Chunk[][] getChunks() {
        return this.chunks;
    }

    public Chunk getChunkAtWorldPos(int x, int z) {
        return this.chunks[(x >> 4)][(z >> 4)];
    }

    public Chunk getChunkAt(int x, int z) {
        if (x < 0 || z < 0 || x >= chunks.length || z >= chunks[x].length) {
            return null;
        }
        return this.chunks[x][z];
    }

    public int getChunkX(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return x;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    public int getChunkZ(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return z;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    public int[] getChunkXZ(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return new int[] {x, z};
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getChunkWidth() {
        return (width >> 4) + 1;
    }

    public int getChunkLength() {
        return (length >> 4) + 1;
    }

    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entities.keySet());
    }

    public Entity spawnEntity(EntityType type, Location location) {
        if (!location.getWorld().equals(this)) {
            throw new IllegalArgumentException("Location not in world.");
        }
        Entity entity;
        switch (type) {
            case ARMOR_STAND:
                entity = new ArmorStand(location);
                break;
            default:
                throw new UnsupportedOperationException("This EntityType cannot be summoned.");
        }
        entities.put(entity, new DataWatcher(entity));
        return entity;
    }

    public Entity addEntity(Entity entity) {
        if (entity.getWorld().equals(this)) {
            entities.put(entity, new DataWatcher(entity));
        } else {
            throw new IllegalArgumentException("Location not in world.");
        }
        return entity;
    }

    public List<Player> getPlayers() {
        return KebabServer.getInstance().getPlayers().stream().filter(player -> player.getWorld().equals(this)).collect(Collectors.toList());
    }

    protected void removeEntity(Entity entity) {
        entities.remove(entity);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
        for (Player player : getPlayers()) {
            try {
                player.sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected DataWatcher getDataWatcher(Entity entity) {
        return entities.get(entity);
    }

    public void update() throws IllegalArgumentException, IllegalAccessException {
        for (DataWatcher watcher : entities.values()) {
            if (watcher.getEntity().getWorld().equals(this)) {
                Map<Field, DataWatcher.WatchableObject> updated = watcher.update();
                PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata(watcher.getEntity(), false, updated.keySet().toArray(new Field[0]));
                for (Player player : getPlayers()) {
                    try {
                        player.sendPacket(entityMetadataPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                PacketPlayOutEntityDestroy entityDestroyPacket = new PacketPlayOutEntityDestroy(watcher.getEntity().getEntityId());
                for (Player player : getPlayers()) {
                    try {
                        player.sendPacket(entityDestroyPacket);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                entities.remove(watcher.getEntity());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        World world = (World) o;
        return Objects.equals(name, world.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
