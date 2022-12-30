package com.uroria.kebab.plugins;

import com.uroria.kebab.file.FileConfiguration;

public final class PluginInfo {
    private final String name;
    private final String description;
    private final String author;
    private final String version;
    private final String main;

    public PluginInfo(FileConfiguration file) {
        this.name = file.get("name", String.class);
        this.description = file.get("description", String.class) == null ? "" : file.get("description", String.class);
        this.author = file.get("author", String.class);
        this.version = file.get("version", String.class);
        this.main = file.get("main", String.class);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getMain() {
        return main;
    }
}
