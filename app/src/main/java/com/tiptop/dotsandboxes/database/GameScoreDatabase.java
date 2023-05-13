package com.tiptop.dotsandboxes.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {GameScore.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class GameScoreDatabase extends RoomDatabase {
    public abstract GameScoreDao gameScoreDao();

}
