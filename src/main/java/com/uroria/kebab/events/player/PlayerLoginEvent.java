package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Cancellable;
import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.events.Event;
import com.uroria.kebab.player.Player;
import net.kyori.adventure.text.Component;

public class PlayerLoginEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;
    private Component reason;

    public PlayerLoginEvent(KebabPlayer player, boolean cancelled, Component reason) {
        super(player);
        this.cancelled = cancelled;
        this.reason = reason;
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
