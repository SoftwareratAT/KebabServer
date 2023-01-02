package com.uroria.kebab.events.player;

import com.uroria.kebab.player.Player;

public class PlayerQuitEvent extends PlayerEvent {
    public PlayerQuitEvent(Player player) {
        super(player);
    }
}
