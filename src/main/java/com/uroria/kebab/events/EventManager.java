package com.uroria.kebab.events;

import com.uroria.kebab.plugins.KebabPlugin;

public interface EventManager {
    void registerListener(KebabPlugin plugin, Listener listener);

    <T extends Event> T callEvent(T event);

    void unregisterAllListeners(KebabPlugin plugin);
}
