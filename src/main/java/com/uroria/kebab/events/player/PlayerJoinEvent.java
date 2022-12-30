package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Cancellable;
import com.uroria.kebab.events.Event;
import com.uroria.kebab.player.Player;
import com.uroria.kebab.player.KebabPlayer;

public class PlayerJoinEvent extends Event implements Cancellable {
    private final Player player;
    private boolean cancelled;

    public PlayerJoinEvent(KebabPlayer player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
