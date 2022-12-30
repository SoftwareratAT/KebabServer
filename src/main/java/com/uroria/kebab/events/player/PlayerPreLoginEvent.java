package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Cancellable;
import com.uroria.kebab.events.Event;
import com.uroria.kebab.network.ClientConnection;
import net.kyori.adventure.text.Component;

public class PlayerPreLoginEvent extends Event implements Cancellable {
    private final ClientConnection clientConnection;
    private boolean cancelled;
    private Component reason;

    public PlayerPreLoginEvent(ClientConnection clientConnection, boolean cancelled, Component reason) {
        this.clientConnection = clientConnection;
        this.cancelled = cancelled;
        this.reason = reason;
    }

    public ClientConnection getConnection() {
        return clientConnection;
    }

    public Component getReason() {
        return reason;
    }

    public void setReason(Component reason) {
        this.reason = reason;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
