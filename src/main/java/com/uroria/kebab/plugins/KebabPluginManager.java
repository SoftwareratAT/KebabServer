package com.uroria.kebab.plugins;

import com.google.common.collect.ImmutableList;
import com.uroria.kebab.KebabServer;
import com.uroria.kebab.commands.CommandSource;
import com.uroria.kebab.commands.KebabCommand;
import com.uroria.kebab.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KebabPluginManager implements PluginManager {
    private final Map<String, KebabPlugin> plugins;
    private final Collection<Command> commands;
    private final File pluginFolder;

    public KebabPluginManager(File pluginFolder) {
        this.pluginFolder = pluginFolder;
        this.commands = new ArrayList<>();
        this.plugins = new LinkedHashMap<>();
    }

    protected void loadPlugins() {
        for (File file : pluginFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                boolean found = false;
                try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
                    while (true) {
                        ZipEntry entry = zip.getNextEntry();
                        if (entry == null) {
                            break;
                        }
                        String name = entry.getName();
                        if (name.endsWith("plugin.yml") || name.endsWith("limbo.yml")) {
                            found = true;
                            FileConfiguration pluginYaml = new FileConfiguration(zip);
                            String main = pluginYaml.get("main", String.class);
                            String pluginName = pluginYaml.get("name", String.class);

                            if (plugins.containsKey(pluginName)) {
                                KebabServer.getInstance().getLogger().error("Ambiguous plugin name in " + file.getName() + " with the plugin " + plugins.get(pluginName).getClass().getName() + "!");
                                break;
                            }
                            URLClassLoader child = new URLClassLoader(new URL[] {file.toURI().toURL()}, KebabServer.getInstance().getClass().getClassLoader());
                            Class<?> clazz = Class.forName(main, true, child);
                            KebabPlugin plugin = (KebabPlugin) clazz.getDeclaredConstructor().newInstance();
                            plugin.setInfo(pluginYaml, file);
                            plugins.put(plugin.getName(), plugin);
                            plugin.onLoad();
                            KebabServer.getInstance().getLogger().info("Loading plugin " + file.getName() + " " + plugin.getPluginInfo().getVersion() + " by " + plugin.getPluginInfo().getAuthor());
                            break;
                        }
                    }
                } catch (Exception exception) {
                    KebabServer.getInstance().getLogger().error("Unable to load plugin " + file.getName() + "!", exception);
                }
                if (!found) {
                    KebabServer.getInstance().getLogger().info("Jar file " + file.getName() + " has no plugin.yml! Ignoring it.");
                }
            }
        }
    }

    @Override
    public Collection<KebabPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    @Override
    public void enablePlugin(String pluginName) {
        getPlugin(pluginName).onEnable();
    }

    @Override
    public void disablePlugin(String pluginName) {
        getPlugin(pluginName).onDisable();
    }

    @Override
    public KebabPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public CompletableFuture<Void> fireExecutors(CommandSource source, String[] args) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String commandName = args[0];
            if (commandName == null || commandName.isEmpty()) return;
            for (Command command : commands) {
                if (command.commandName.equalsIgnoreCase(commandName)) {
                    command.command.execute(source, Arrays.copyOfRange(args, 1, args.length));
                    break;
                }
            }
        });
    }

    public CompletableFuture<List<String>> getTabOptions(CommandSource source, String[] args) {
        return CompletableFuture.supplyAsync(() -> {
            String commandName = args[0];
            if (commandName == null) return ImmutableList.of();
            List<String> options = new ArrayList<>();
            for (Command command : commands) {
                if (!command.commandName.contains(commandName)) continue;
                options.addAll(command.command.getTabComplete(source, Arrays.copyOfRange(args, 1, args.length)));
            }
            return options;
        });
    }

    @Override
    public void registerCommand(KebabPlugin plugin, KebabCommand command) {
        commands.add(new Command(plugin, command, command.getCommandName()));
    }

    @Override
    public void unregisterAllCommands(KebabPlugin plugin) {
        commands.removeIf(each -> each.plugin.equals(plugin));
    }

    @Override
    public File getPluginFolder() {
        return new File(pluginFolder.getAbsolutePath());
    }

    protected static final class Command {
        public final String commandName;
        public final KebabPlugin plugin;
        public final KebabCommand command;

        public Command(KebabPlugin plugin, KebabCommand command, String commandName) {
            this.plugin = plugin;
            this.command = command;
            this.commandName = commandName;
        }
    }
}
