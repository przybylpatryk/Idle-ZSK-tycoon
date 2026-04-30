package edu.zsk.zsktycoon.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GameStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GameStateEntity state);

    @Update
    void update(GameStateEntity state);

    @Query("SELECT * FROM game_state WHERE id = 1")
    GameStateEntity getState();

}