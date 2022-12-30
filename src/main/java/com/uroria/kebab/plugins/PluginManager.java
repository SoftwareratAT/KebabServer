package com.uroria.kebab.plugins;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
public class PluginManager {
    private Map<String, KebabPlugin> plugins;
    private List<Executor> executors;
    private File pluginFolder;

    public PluginManager(File pluginFolder) {
        this.pluginFolder = pluginFolder;
        this.executors = new ArrayList<>();
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
                                System.err.println("Ambiguous plugin name in " + file.getName() + " with the plugin \"" + plugins.get(pluginName).getClass().getName() + "\"");
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
                } catch (Exception e) {
                    System.err.println("Unable to load plugin \"" + file.getName() + "\"");
                    e.printStackTrace();
                }
                if (!found) {
                    System.err.println("Jar file " + file.getName() + " has no plugin.yml!");
                }
            }
        }
    }

    public List<KebabPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    public KebabPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public void fireExecutors(CommandSender sender, String[] args) throws Exception {
        KebabServer.getInstance().getLogger().info(sender.getName() + " executed server command: /" + String.join(" ", args));
        try {
            defaultExecutor.execute(sender, args);
        } catch (Exception e) {
            System.err.println("Error while running default command \"" + args[0] + "\"");
            e.printStackTrace();
        }
        for (Executor entry : executors) {
            try {
                entry.executor.execute(sender, args);
            } catch (Exception e) {
                System.err.println("Error while passing command \"" + args[0] + "\" to the plugin \"" + entry.plugin.getName() + "\"");
                e.printStackTrace();
            }
        }
    }

    public List<String> getTabOptions(CommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();
        try {
            options.addAll(defaultExecutor.tabComplete(sender, args));
        } catch (Exception e) {
            System.err.println("Error while getting default command tab completions");
            e.printStackTrace();
        }
        for (Executor entry : executors) {
            if (entry.tab.isPresent()) {
                try {
                    options.addAll(entry.tab.get().tabComplete(sender, args));
                } catch (Exception e) {
                    System.err.println("Error while getting tab completions to the plugin \"" + entry.plugin.name) + "\"");
                    e.printStackTrace();
                }
            }
        }
        return options;
    }

    public void registerCommands(KebabPlugin plugin, CommandExecutor executor) {
        executors.add(new Executor(plugin, executor));
    }

    public void unregisterAllCommands(KebabPlugin plugin) {
        executors.removeIf(each -> each.plugin.equals(plugin));
    }

    public File getPluginFolder() {
        return new File(pluginFolder.getAbsolutePath());
    }

    protected static class Executor {
        public KebabPlugin plugin;
        public CommandExecutor executor;
        public Optional<TabCompletor> tab;

        public Executor(KebabPlugin plugin, CommandExecutor executor) {
            this.plugin = plugin;
            this.executor = executor;
            if (executor instanceof TabCompletor) {
                this.tab = Optional.of((TabCompletor) executor);
            } else {
                this.tab = Optional.empty();
            }
        }
    }
}
