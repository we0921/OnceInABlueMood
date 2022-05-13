package com.example.bluemood.db;

import android.app.Application;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class EntryViewModel extends AndroidViewModel {
    private LiveData<List<Entry>> entries;

    public EntryViewModel(Application application) {
        super(application);
        // Get the current year in string form
        int year = Calendar.getInstance().get(Calendar.YEAR);
        // Query the database using the year
        entries = EntryDatabase.getDatabase(getApplication()).entryDAO().getYear(year);
//        entries = EntryDatabase.getDatabase(getApplication()).entryDAO().getAll();
    }

    public LiveData<List<Entry>> getEntries() {
        return entries;
    }
}
