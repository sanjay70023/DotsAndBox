package com.tiptop.dotsandboxes.event_bus.events;


import com.tiptop.dotsandboxes.game.controllers.Game;



public class ScoreMadeEvent {
    public final Game.Player scoringPlayer;
    public final int score;

    public ScoreMadeEvent(Game.Player scoringPlayer, int score) {
        this.scoringPlayer = scoringPlayer;
        this.score = score;
    }

    @Override
    public String toString() {
        return "ScoreMadeEvent{" +
                "scoringPlayer=" + scoringPlayer +
                ", score=" + score +
                '}';
    }
}
