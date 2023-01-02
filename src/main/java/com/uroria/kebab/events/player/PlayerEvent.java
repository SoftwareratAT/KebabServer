package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Event;
import com.uroria.kebab.player.Player;

public abstract class PlayerEvent extends Event {
    private final Player player;
    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
