package com.tiptop.dotsandboxes.event_bus.events;


import com.tiptop.dotsandboxes.game.models.Edge;



public class PlayerMoveEvent {

    public final Edge playerMove;

    public PlayerMoveEvent(Edge playerMove) {
        this.playerMove = playerMove;
    }

    @Override
    public String toString() {
        return "PlayerMoveEvent{" +
                "playerMove=" + playerMove +
                '}';
    }
}
