package com.tiptop.dotsandboxes.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GameScore {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "opponent")
    private String opponent;

    @ColumnInfo(name = "mode")
    private String mode;

    @ColumnInfo(name = "date")
    private Long date;

    @ColumnInfo(name = "score")
    private String score;

    @ColumnInfo(name = "result")
    private String result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "GameScore{" +
                "id=" + id +
                ", opponent='" + opponent + '\'' +
                ", mode='" + mode + '\'' +
                ", date=" + date +
                ", score='" + score + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
