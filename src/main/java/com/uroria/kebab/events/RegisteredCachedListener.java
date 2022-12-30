package com.uroria.kebab.events;

import com.uroria.kebab.plugins.KebabPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisteredCachedListener {
        private KebabPlugin plugin;
        private Map<Class<? extends Event>, Map<EventOrder, List<Method>>> listeners;

        @SuppressWarnings("unchecked")
        public RegisteredCachedListener(KebabPlugin plugin, Listener listener) {
            this.plugin = plugin;
            this.listeners = new ConcurrentHashMap<>();
            for (Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                    listeners.putIfAbsent(eventClass, new ConcurrentHashMap<>());
                    Map<EventOrder, List<Method>> mapping = listeners.get(eventClass);
                    EventOrder eventOrder = method.getAnnotation(EventHandler.class).order();
                    mapping.putIfAbsent(eventOrder, new ArrayList<>());
                    List<Method> list = mapping.get(eventOrder);
                    list.add(method);
                }
            }
        }

        public KebabPlugin getPlugin() {
            return plugin;
        }

        public List<Method> getListeners(Class<? extends Event> eventClass, EventOrder eventOrder) {
            Map<EventOrder, List<Method>> mapping = listeners.get(eventClass);
            if (mapping == null) {
                return Collections.emptyList();
            }
            List<Method> list = mapping.get(eventOrder);
            return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        }
}
