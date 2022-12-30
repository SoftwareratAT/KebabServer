package com.uroria.kebab;

import com.uroria.kebab.events.EventManager;
import com.uroria.kebab.events.KebabEventManager;
import com.uroria.kebab.file.FileConfiguration;
import com.uroria.kebab.logger.ConsoleLogger;
import com.uroria.kebab.logger.Logger;
import com.uroria.kebab.network.ServerConnection;
import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.plugins.KebabPlugin;
import com.uroria.kebab.plugins.KebabPluginManager;
import com.uroria.kebab.plugins.PluginManager;
import com.uroria.kebab.scheduling.KebabScheduler;
import com.uroria.kebab.scheduling.Tick;
import com.uroria.kebab.world.World;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class KebabServer {
    private static KebabServer INSTANCE;

    public static void main(final String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        for (final String arg: args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println("Documentation will come soon!");
                System.exit(0);
                return;
            }
        }
        System.out.println("Initializing...");
        INSTANCE = new KebabServer();
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
    private final Collection<KebabPlayer> players;
    private final Collection<World> worlds;

    public KebabServer() throws IOException, NumberFormatException, ClassNotFoundException, InterruptedException {
        System.out.println("Running Kebab server " + SERVER_VERSION + ", Protocol " + SERVER_PROTOCOL);
        this.running = new AtomicBoolean(true);
        this.consoleLogger = new ConsoleLogger();
        getLogger().info("Booting up...");
        this.scheduler = new KebabScheduler();
        this.serverConfig = new FileConfiguration(new File("server.yml"));
        int tps = this.serverConfig.get("ticks-per-second", Integer.class);
        if (tps == 0) {
            getLogger().error("TPS null! Using a tps of 10 now!");
            tps = 10;
        }
        this.pluginFolder = new File("plugins");
        this.internalDataFolder = new File("internal_data");;
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
        this.server = new ServerConnection(this.serverConfig.get("server-ip", String.class), this.serverConfig.get("server-port", Integer.class));
        this.players = new ArrayList<>();
        this.worlds = new ArrayList<>();
        getLogger().info("Server online!");
        getLogger().info("Running on " + this.serverConfig.get("server-ip", String.class) + " on port " + this.serverConfig.get("server-port", Integer.class));
    }

    public void stopServer() {
        running.set(false);
        getLogger().info("Stopping server...");
        for (KebabPlugin plugin : this.pluginManager.getPlugins()) {
            getLogger().info("Disabling plugin " + plugin.getName() + " " + plugin.getPluginInfo().getVersion());
            plugin.onDisable();
        }
        System.exit(0);
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

    public Collection<KebabPlayer> getPlayers() {
        return this.players;
    }

    public Collection<World> getWorlds() {
        return this.worlds;
    }
}
