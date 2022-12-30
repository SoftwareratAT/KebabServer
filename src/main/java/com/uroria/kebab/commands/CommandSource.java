package com.uroria.kebab.commands;

import net.kyori.adventure.text.Component;

public interface CommandSource {
    boolean hasPermission(String permission);

    void sendMessage(Component component);

    void sendMessage(String message);
}
