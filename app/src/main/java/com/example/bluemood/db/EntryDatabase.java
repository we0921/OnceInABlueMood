package com.example.bluemood.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Entry.class}, version = 1, exportSchema = false)
//@TypeConverters({Converters.class})
public abstract class EntryDatabase extends RoomDatabase {
    public interface EntryListener {
        void onEntryReturned(Entry entry);
    }

    public abstract EntryDAO entryDAO();

    private static EntryDatabase INSTANCE;

    // Ensure there is only one database instance
    public static EntryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EntryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EntryDatabase.class, "entry_database")
                            .addCallback(createEntryDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static RoomDatabase.Callback createEntryDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static void getEntry(int id, EntryListener listener) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onEntryReturned((Entry) msg.obj);
            }
        };

        (new Thread(() -> {
                Message msg = handler.obtainMessage();
                msg.obj = INSTANCE.entryDAO().getById(id);
                handler.sendMessage(msg);
            })).start();
    }

    public static void insert(Entry entry) {
        (new Thread(()-> INSTANCE.entryDAO().insert(entry))).start();
    }

    public static void delete(int entryId) {
        (new Thread(() -> INSTANCE.entryDAO().delete(entryId))).start();
    }

    public static void update(Entry entry) {
        (new Thread(() -> INSTANCE.entryDAO().update(entry))).start();
    }
}