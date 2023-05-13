package com.tiptop.dotsandboxes.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GameScoreDao {

    @Query("SELECT * FROM gamescore ORDER BY date desc")
    List<GameScore> getAll();

    @Query("SELECT count(*) FROM gamescore")
    long getTotalGamesPlayed();


    @Query("SELECT COUNT(*) FROM gamescore WHERE result is 'Won'")
    long getWinMatches();

    @Query("SELECT COUNT(*) FROM gamescore WHERE result is 'Tie'")
    long getTieMatches();

    @Query("SELECT COUNT(*) FROM gamescore WHERE result is 'Lost'")
    long getLostMatches();

    @Insert
    void  insertGameScore(GameScore gameScore);


}
