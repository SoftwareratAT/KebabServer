package com.uroria.kebab;

import com.google.common.reflect.ClassPath;
import com.uroria.kebab.events.EventManager;
import com.uroria.kebab.events.KebabEventManager;
import com.uroria.kebab.file.FileConfiguration;
import com.uroria.kebab.logger.ConsoleLogger;
import com.uroria.kebab.logger.Logger;
import com.uroria.kebab.network.ServerConnection;
import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.player.Player;
import com.uroria.kebab.plugins.KebabPlugin;
import com.uroria.kebab.plugins.KebabPluginManager;
import com.uroria.kebab.plugins.PluginManager;
import com.uroria.kebab.scheduling.KebabScheduler;
import com.uroria.kebab.scheduling.Tick;
import com.uroria.kebab.world.Environment;
import com.uroria.kebab.world.Schematic;
import com.uroria.kebab.world.World;
import net.kyori.adventure.text.Component;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class KebabServer {
    private static KebabServer INSTANCE;

    public static void main(final String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String address = null;
        int port = -1;
        for (final String arg: args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println(" ");
                System.out.println("-h --help : Show this");
                System.out.println("-a:<address> --address:<address> : Override the address of the server");
                System.out.println("-p:<port> --port:<port> : Override the port of the server");
                System.out.println("-s --start : Start the server");
                return;
            }
            if (arg.contains("--port:") || arg.contains("-p:")) {
                port = Integer.parseInt(arg.replace("--port:", "").replace("-p:", ""));
                continue;
            }
            if (arg.contains("--address:") || arg.contains("-a:")) {
                address = arg.replace("--address:", "").replace("-a:", "");
                continue;
            }
            if (arg.equals("--start") || arg.equals("-s")) {
                System.out.println("Initializing...");
                INSTANCE = new KebabServer(address, port);
                return;
            }
        }
        System.out.println(" ");
        System.out.println("Please provide arguments! [--h; --help for help]");
    }

    public static KebabServer getInstance() {
        return INSTANCE;
    }

    //=====================
    // Engine starts here!
    //=====================

    // Minecraft version and protocol version
    public static String SERVER_VERSION = "1.19.3";
    public static int SERVER_PROTOCOL = 761;
    private final int viewDistance;

    private final UnsafeServer unsafe;
    private final AtomicBoolean running;
    private final ServerConnection server;
    private final ConsoleLogger consoleLogger;
    private final FileConfiguration serverConfig;
    private final KebabScheduler scheduler;
    private final File pluginFolder;
    private final File internalDataFolder;
    private final Tick tick;
    private final KebabPluginManager pluginManager;
    private final KebabEventManager eventManager;
    private final AtomicInteger entityIdCount = new AtomicInteger();
    final Map<UUID, KebabPlayer> players;
    private final Collection<World> worlds;

    public KebabServer(String serverAddress, int port) throws IOException, NumberFormatException, ClassNotFoundException, InterruptedException {
        this.running = new AtomicBoolean(true);
        this.consoleLogger = new ConsoleLogger();
        getLogger().info("Running Kebab server " + SERVER_VERSION + ", Protocol " + SERVER_PROTOCOL);
        getLogger().info("Booting up...");
        this.players = new HashMap<>();
        this.worlds = new ArrayList<>();
        this.unsafe = new UnsafeServer(this);
        this.scheduler = new KebabScheduler();
        File config = new File("server.yml");
        if (!config.exists()) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("server.yml");
            if (inputStream == null) {
                getLogger().error("Config not found in ClassLoader!");
                System.exit(4);
            }
            Files.copy(inputStream, config.toPath());
            config = new File("server.yml");
        }
        this.serverConfig = new FileConfiguration(config);
        int tps = this.serverConfig.getInt("ticks-per-second");
        if (tps == 0) {
            getLogger().error("TPS null! Using a tps of 10 now!");
            tps = 10;
        }
        int viewDistance = this.serverConfig.getInt("view-distance");
        if (viewDistance == 0) {
            getLogger().error("ViewDistance null! Using a ViewDistance value of 6 now!");
            this.viewDistance = 6;
        } else this.viewDistance = viewDistance;
        loadAllPackets();
        this.pluginFolder = new File("plugins");
        if (!this.pluginFolder.exists()) {
            if (this.pluginFolder.mkdir()) getLogger().info("Created empty plugin folder.");
        }
        this.internalDataFolder = new File("internal_data");;
        if (!this.internalDataFolder.exists()) {
            if (this.internalDataFolder.mkdir()) getLogger().info("Created internal_data folder.");
        }
        this.tick = new Tick(this, tps);
        this.pluginManager = new KebabPluginManager(this.pluginFolder);
        try {
            Method loadPluginsMethod = KebabPluginManager.class.getDeclaredMethod("loadPlugins");
            loadPluginsMethod.setAccessible(true);
            loadPluginsMethod.invoke(this.pluginManager);
            loadPluginsMethod.setAccessible(false);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            getLogger().error("Error while trying to load plugins", exception);
        }
        this.eventManager = new KebabEventManager();
        if (serverAddress == null) serverAddress = this.serverConfig.get("server-ip", String.class);
        if (port == -1) port = this.serverConfig.getInt("server-port");
        this.server = new ServerConnection(serverAddress, port);

        Runtime.getRuntime().addShutdownHook(new Thread(this::terminate));

        getLogger().info("Server online!");
    }

    private void loadAllPackets() throws IOException, ClassNotFoundException {
        getLogger().info("Loading all packets...");
        Collection<String> packages = new ArrayList<>();
        final String prefix = "com.uroria.kebab.network.protocol.minecraft";
        packages.add(prefix + ".handshake.in");
        packages.add(prefix + ".login.in");
        packages.add(prefix + ".login.out");
        packages.add(prefix + ".play.in");
        packages.add(prefix + ".play.out");
        for (String pack : packages) {
            ClassPath pathToClass = ClassPath.from(this.getClass().getClassLoader());
            for (ClassPath.ClassInfo classInfo : pathToClass.getTopLevelClasses(pack)) {
                Class<?> clazz = Class.forName(classInfo.getName(), true, getClass().getClassLoader());
                getLogger().info("Loaded packet " + clazz.getSimpleName());
            }
        }
        getLogger().info("Finished loading packets!");
    }

    public void stopServer() {
        System.exit(0);
    }

    private void terminate() {
        running.set(false);
        getLogger().info("Stopping server...");
        for (KebabPlugin plugin : this.pluginManager.getPlugins()) {
            getLogger().info("Disabling plugin " + plugin.getName() + " " + plugin.getPluginInfo().getVersion());
            plugin.onDisable();
        }
        getPlayers().forEach(player -> player.disconnect(Component.newline().content("§cServer stopped")));
        while (!getPlayers().isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        getLogger().info("Server stopped!");
    }


    public Tick getTick() {
        return this.tick;
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public File getInternalDataFolder() {
        return this.internalDataFolder;
    }

    public File getPluginFolder() {
        return this.pluginFolder;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public Logger getLogger() {
        return this.consoleLogger;
    }

    public KebabScheduler getScheduler() {
        return this.scheduler;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getOnlinePlayerCount() {
        return this.players.size();
    }

    public Collection<Player> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public Player getPlayer(UUID playerUniqueId) {
        return this.players.get(playerUniqueId);
    }

    public Collection<World> getWorlds() {
        return new ArrayList<>(this.worlds);
    }

    public World getWorld(String name) {
        for (World world : getWorlds()) {
            if (world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        return null;
    }

    public int getNextEntityId() {
        return this.entityIdCount.getAndUpdate(i -> i == Integer.MAX_VALUE ? 0 : i++);
    }

    public CompletableFuture<Void> registerWorld(World world) {
        return CompletableFuture.runAsync(() -> {
            //TODO world register event
            if (!worlds.contains(world)) worlds.add(world);
            else throw new RuntimeException("World already registered");
        });
    }

    public CompletableFuture<Void> unregisterWorld(World world) {
        return CompletableFuture.runAsync(() -> {
            if (!worlds.contains(world)) throw new RuntimeException("World not registered");
            else {
                //TODO World remove event
                //TODO Somehow get rid of all players inside the world
                worlds.remove(world);
            }
        });
    }

    public World getDefaultWorld() throws IOException {
        getLogger().info("Loading default world from schematic file...");
        File schematicFile = new File("default.schem");
        if (!schematicFile.exists()) {
            getLogger().info("Can't find default world schematic file!");
            getLogger().info("Creating default world...");
            try (InputStream input = KebabServer.class.getClassLoader().getResourceAsStream("default.schem")) {
                Files.copy(input, schematicFile.toPath());
            } catch (IOException exception) {
                getLogger().error("Error while creating default world from ClassLoader", exception);
            }
        }

        try {
            World world = Schematic.toWorld("default", Environment.NORMAL, (CompoundTag) NBTUtil.read(schematicFile).getTag());
            getLogger().info("Successfully loaded default world from schematic file!");
            return world;
        } catch (Exception exception) {
            getLogger().error("Unable to load default world from schematic file!", exception);
            getLogger().info("System is shutting down...");
            System.exit(1);
            return null;
        }
    }

    public ServerConnection getServerConnection() {
        return this.server;
    }

    public FileConfiguration getServerConfig() {
        return this.serverConfig;
    }

    public UnsafeServer getUnsafe() {
        return unsafe;
    }
}
