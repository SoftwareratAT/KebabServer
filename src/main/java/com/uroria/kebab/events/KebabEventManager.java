package com.uroria.kebab.events;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.plugins.KebabPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KebabEventManager implements EventManager {
    private final List<ListenerPair> listeners;
    private final Map<Listener, RegisteredCachedListener> cachedListeners;

    public KebabEventManager() {
        this.listeners = new ArrayList<>();
        this.cachedListeners = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        for (EventOrder order : EventOrder.getOrdersInOrder()) {
            for (Map.Entry<Listener, RegisteredCachedListener> entry : cachedListeners.entrySet()) {
                for (Method method : entry.getValue().getListeners(event.getClass(), order)) {
                    try {
                        method.invoke(entry.getKey(), event);
                    } catch (Exception exception) {
                        KebabServer.getInstance().getLogger().error("Error while passing " + event.getClass().getCanonicalName() + " to plugin " + entry.getValue().getPlugin().getName(), exception);
                    }
                }
            }
        }
        return event;
    }

    @Override
    public void registerListener(KebabPlugin plugin, Listener listener) {
        this.listeners.add(new ListenerPair(plugin, listener));
        this.cachedListeners.put(listener, new RegisteredCachedListener(plugin, listener));
    }

    @Override
    public void unregisterAllListeners(KebabPlugin kebabPlugin) {
        this.listeners.removeIf(listenerPair -> {
            if (listenerPair.plugin.equals(kebabPlugin)) {
                cachedListeners.remove(listenerPair.listener);
                return true;
            }
            return false;
        });
    }


    protected static class ListenerPair {
        public final KebabPlugin plugin;
        public final Listener listener;

        public ListenerPair(KebabPlugin plugin, Listener listener) {
            this.plugin = plugin;
            this.listener = listener;
        }
    }
}
