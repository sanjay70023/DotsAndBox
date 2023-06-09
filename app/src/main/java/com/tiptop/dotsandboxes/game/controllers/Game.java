
package com.tiptop.dotsandboxes.game.controllers;


import com.tiptop.dotsandboxes.event_bus.RxBus;
import com.tiptop.dotsandboxes.event_bus.events.GameEndEvent;
import com.tiptop.dotsandboxes.event_bus.events.ScoreMadeEvent;
import com.tiptop.dotsandboxes.event_bus.events.TurnChangeEvent;
import com.tiptop.dotsandboxes.game.models.Board;
import com.tiptop.dotsandboxes.game.models.Edge;
import com.tiptop.dotsandboxes.game.models.Graph;

/**
 * Game class object responsible for handling the game as it develops.
 * It interfaces the game state and keeps track of the board, notifies the game listeners
 * and indicated when a turn has changed.
 */
public class Game {
    /**
     *  Reactive Bus subscription
     */
    private final RxBus rxBus;

    private Board board;
    private Graph gameTree;

    // pre-calculated field for keeping track of the current MAX Score
    private int maxScore;

    public void setNextPlayer(Player nextPlayer) {
        switch (nextPlayer) {
            case PLAYER1:
                gameState = State.PLAYER1_TURN;
                break;
            case PLAYER2:
                gameState = State.PLAYER2_TURN;
                break;
            case NONE:
                gameState = State.PLAYER1_TURN;
                break;
        }
    }

    public State getState() {
        return gameState;
    }

    public Graph getGameTree() {
        return gameTree;
    }

    /**
     * Enumeration for the game state. Could be any one of the described below.
     */
    public enum State {
        PLAYER1_TURN, PLAYER2_TURN, END
    }

    private State gameState = State.PLAYER1_TURN;
    /**
     * Enumeration of the players, who own a square. Also used for turn based decisions.
     */
    public enum Player {
        PLAYER1, PLAYER2, NONE

    }
    /**
     * Enumeration of the Mode which the user has selected to play as.
     */
    public enum Mode {
        PLAYER, CPU

    }

    /**
     * Basic default constructor setting initial values to zero
     * and placing PLAYER1 as the next to move.
     *
     * @param rows    the rows of the board
     * @param columns the columns of the board
     */
    public Game(int rows, int columns) {
        this.maxScore = rows * columns;
        this.board = new Board(rows, columns);
        this.gameTree = new Graph(rows, columns);
        this.gameState = State.PLAYER1_TURN;
        this.rxBus = RxBus.getInstance();
    }

    /**
     * The getter for the board object
     *
     * @return the current board on which the game is played
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Connects two dots on the board, which counts as a move.
     * This method keeps track of the current state of the game.
     *
     * @param dotStart starting point
     * @param dotEnd   ending point
     * @return how many boxes were completed. -1 if the move is invalid.
     */
    public int makeAMove(int dotStart, int dotEnd) {
        if (gameTree.hasEdge(dotStart, dotEnd))
            return -1;

        gameTree.addEdge(new Edge(dotStart, dotEnd));

        switch (gameState) {
            case PLAYER1_TURN:
                return takeTurnPlayer1(dotStart, dotEnd);

            case PLAYER2_TURN:
                return takeTurnPlayer2(dotStart, dotEnd);

            case END:

                return 0;
        }

        return 0;
    }

    /**
     * A move will be executed with Player1 being active
     */
    private int takeTurnPlayer1(int dotStart, int dotEnd) {
        // register the move on the board for the first mService
        int boxesCompleted = board.setLineForDots(dotStart, dotEnd, Player.PLAYER1);

        if (boxesCompleted > 0) {
            // calculate the score
            int player1Score = board.getScore(Player.PLAYER1);
            int player2Score = board.getScore(Player.PLAYER2);
            notifyScoreChange(Player.PLAYER1, player1Score);

            // if the board is filled
            if (player1Score + player2Score == maxScore) {
                if (player1Score == player2Score) {
                    notifyGameEnd(Player.NONE);
                }
                else if (player1Score > player2Score) {
                    notifyGameEnd(Player.PLAYER1);
                }
                else {
                    notifyGameEnd(Player.PLAYER2);
                }

                gameState = State.END;
            }
        }
        else {
            gameState = State.PLAYER2_TURN;
            notifyTurnChange(Player.PLAYER2);
        }

        return boxesCompleted;
    }

    /**
     * A move will be executed with Player2 being active
     */
    private int takeTurnPlayer2(int dotStart, int dotEnd) {
        // register the move on the board for the first mService
        int boxesCompleted = board.setLineForDots(dotStart, dotEnd, Player.PLAYER2);

        if (boxesCompleted > 0) {
            // calculate the score
            int player1Score = board.getScore(Player.PLAYER1);
            int player2Score = board.getScore(Player.PLAYER2);
            notifyScoreChange(Player.PLAYER2, player2Score);

            // if the board is filled
            if (player1Score + player2Score == maxScore) {
                if (player1Score == player2Score) {
                    notifyGameEnd(Player.NONE);
                } else if (player1Score > player2Score) {
                    notifyGameEnd(Player.PLAYER1);
                } else {
                    notifyGameEnd(Player.PLAYER2);
                }


                gameState = State.END;
            }
        }
        else {
            gameState = State.PLAYER1_TURN;
            notifyTurnChange(Player.PLAYER1);
        }

        return boxesCompleted;
    }

    /**
     * Notifies the listeners for a turn change
     *
     * @param nextPlayer the mService who is going the take the next turn
     */
    private void notifyTurnChange(Player nextPlayer) {
        rxBus.send(new TurnChangeEvent(nextPlayer));
    }

    /**
     * Notifies the listeners for a score change
     *
     * @param player the mService who's score is changing
     * @param score  the mService's score
     */
    private void notifyScoreChange(Player player, int score) {
        rxBus.send(new ScoreMadeEvent(player, score));
    }

    /**
     * Notify the end of the game
     *
     * @param winner the winner of the game
     */
    private void notifyGameEnd(Player winner) {
        rxBus.send(new GameEndEvent(winner));
    }
}
