package com.tiptop.dotsandboxes.event_bus.events;


import com.tiptop.dotsandboxes.game.controllers.Game;



public class GameEndEvent {
    private final Game.Player winner;

    public GameEndEvent(Game.Player winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "GameEndEvent{" +
                "winner=" + winner +
                '}';
    }
}
