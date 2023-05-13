package com.tiptop.dotsandboxes.event_bus.events;


import com.tiptop.dotsandboxes.game.models.Edge;



public class BotComputeEvent {
    public final Edge botMove;

    public BotComputeEvent(Edge botMove) {
        this.botMove = botMove;
    }

    @Override
    public String toString() {
        return "BotComputeEvent{" +
                "botMove=" + botMove +
                '}';
    }
}
