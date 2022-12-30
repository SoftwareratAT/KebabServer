package com.uroria.kebab.plugins;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.file.FileConfiguration;

import java.io.File;

public class KebabPlugin {
    private PluginInfo pluginInfo;
    private String name;
    private File dataFolder;
    private File pluginJar;

    protected final void setInfo(FileConfiguration file, File pluginJar) {
        this.pluginInfo = new PluginInfo(file);
        this.name = this.pluginInfo.getName();
        this.dataFolder = new File("", this.name);
        this.pluginJar = pluginJar;
    }

    protected final File getPluginJar() {
        return this.pluginJar;
    }

    public void onLoad() {}
    public void onEnable() {}
    public void onDisable() {}

    public final String getName() {
        return this.name;
    }

    public final File getDataFolder() {
        return this.dataFolder;
    }

    public final PluginInfo getPluginInfo() {
        return this.pluginInfo;
    }

    public final KebabServer getServer() {
        return KebabServer.getInstance();
    }
}
