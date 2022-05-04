package com.example.bluemood.db;

import androidx.room.TypeConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

// DEPRECATED
public class Converters {

    // Convert from String to OffsetDateTime
    @TypeConverter
    public static OffsetDateTime toOffsetDateTime (String dateTime) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(dateTime, OffsetDateTime::from);
    }

    // Convert from OffsetDateTime to String
    @TypeConverter
    public static String fromOffsetDateTime (OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}