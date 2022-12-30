package com.uroria.kebab.events;

public interface Cancellable {
    void setCancelled(boolean value);
    boolean isCancelled();
}
