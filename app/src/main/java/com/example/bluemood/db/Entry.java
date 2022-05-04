package com.example.bluemood.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;

@Entity(tableName="entries")
public class Entry {

    public enum Moods {
        AWFUL,
        BAD,
        OKAY,
        GOOD,
        GREAT
    }

    public Entry(int id, int mood, int month, int day, int year, String time, String description, String tags, String location) {
        this.id = id;
        this.mood = mood;

        this.month = month;
        this.day = day;
        this.year = year;

        this.time = time;

        this.description = description;
        this.tags = tags;
        this.location = location;
    }

    // Assign 0 to id to have it be auto generated
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    public int id;

    // Integer value 0-4
    @ColumnInfo(name = "mood")
    public int mood;

    @ColumnInfo(name = "month")
    public int month;

    @ColumnInfo(name = "day")
    public int day;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "desc")
    public String description;

    @ColumnInfo(name = "tags")
    public String tags;

    @ColumnInfo(name = "location")
    public String location;
}
