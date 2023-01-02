package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Cancellable;
import com.uroria.kebab.events.Event;
import com.uroria.kebab.player.Player;
import com.uroria.kebab.player.KebabPlayer;

public class PlayerJoinEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;

    public PlayerJoinEvent(KebabPlayer player) {
        super(player);
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
