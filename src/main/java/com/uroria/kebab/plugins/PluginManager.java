package com.uroria.kebab.plugins;

import com.uroria.kebab.commands.KebabCommand;

import java.io.File;
import java.util.Collection;

public interface PluginManager {
    File getPluginFolder();

    void registerCommand(KebabPlugin plugin, KebabCommand command);

    void unregisterAllCommands(KebabPlugin plugin);

    KebabPlugin getPlugin(String pluginName);

    void disablePlugin(String pluginName);

    void enablePlugin(String pluginName);

    Collection<KebabPlugin> getPlugins();
}
