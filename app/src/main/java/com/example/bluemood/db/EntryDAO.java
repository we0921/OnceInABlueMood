package com.example.bluemood.db;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface EntryDAO {
    // Get entries for a specific year
    @Query("SELECT * FROM entries WHERE year = :year ORDER BY month desc, day desc, time desc")
    LiveData<List<Entry>> getYear(int year);

    // Get entries for a specific day? Not sure when this would be used
    @Query("SELECT * FROM entries WHERE year = :y AND month = :m AND day = :d")
    LiveData<List<Entry>> getDay(int m, int d, int y);

    // Get one specific entry
    @Query("SELECT * FROM entries WHERE rowid = :entryID")
    Entry getById(int entryID);

    // [DEBUG] Get all
    @Query("SELECT * FROM entries")
    LiveData<List<Entry>> getAll();

    @Insert
    void insert(Entry... entries);

    @Update
    void update(Entry... entries);

    @Delete
    void delete(Entry... entries);

    @Query("DELETE FROM entries WHERE rowid = :entryID")
    void delete(int entryID);
}
