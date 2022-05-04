package com.example.bluemood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluemood.db.Entry;
import com.example.bluemood.db.EntryViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EntryViewModel entryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("theme", false)) {
            setTheme(R.style.Theme_BlueMood_Dark_NoBar);
        } else {
            setTheme(R.style.Theme_BlueMood_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);


        // Set the action bar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.lstEntries);
        EntryListAdapter adapter = new EntryListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);
        entryViewModel.getEntries().observe(this, adapter::setEntries);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, AddActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // TODO - Create AddActivity class
    public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryViewHolder> {

        class EntryViewHolder extends RecyclerView.ViewHolder {
            private final TextView entryTimeDate;
            private final TextView entryDesc;
            private final ImageView entryIcon;
            private Entry entry;

            private EntryViewHolder(View itemView) {
                super(itemView);
                entryTimeDate = itemView.findViewById(R.id.entryTimeDate);
                entryDesc = itemView.findViewById(R.id.entryDesc);
                entryIcon = itemView.findViewById(R.id.entryIcon);

                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    intent.putExtra("entry_id", entry.id);
                    startActivity(intent);
                });
            }
        }

        private final LayoutInflater layoutInflater;
        private List<Entry> entries;    // Cached copy of entries

        EntryListAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            Log.d("onCreateViewHolder", "Attempting to create view holder");
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new EntryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EntryViewHolder holder, int position) {
            if (entries != null) {
                Entry current = entries.get(position);
                holder.entry = current;
                Log.d("onBindViewHolder", "Setting holder for entry id " + holder.entry.id);

                // Parse date & time
                Calendar calendar = Calendar.getInstance();
                calendar.set(current.year,current.month,current.day);
                String dateString = "";
                dateString = dateString + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en")) + " ";
                dateString = dateString + current.day + ", ";
                dateString = dateString + current.year;

                // Display date & time
                holder.entryTimeDate.setText(dateString + " - " + current.time);
                // Display description
                holder.entryDesc.setText(current.description);

                Drawable moodIcon;

                // TODO - Account for theme in getDrawable
                // Android Studio didn't like using Entry Mood ENUM instead of int literals?
                Log.d("Mood: ", "" + current.mood);
                switch(current.mood) {
                    case 0:
                        moodIcon = getResources().getDrawable(R.drawable.ic_sentiment_very_dissatisfied_fill);
                        break;
                    case 1:
                        moodIcon = getResources().getDrawable(R.drawable.ic_sentiment_dissatisfied_fill);
                        break;
                    case 2:
                        moodIcon = getResources().getDrawable(R.drawable.ic_sentiment_neutral_fill);
                        break;
                    case 3:
                        moodIcon = getResources().getDrawable(R.drawable.ic_sentiment_satisfied_fill);
                        break;
                    case 4:
                        moodIcon = getResources().getDrawable(R.drawable.ic_sentiment_very_satisfied_fill);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + current.mood);
                }

                // Set the mood icon
                holder.entryIcon.setImageDrawable(moodIcon);
            } else {
                Log.d("onBindViewHolder", "entries = null");
                holder.entryTimeDate.setText("...");
                holder.entryDesc.setText("Loading content...");
                holder.entryIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_neutral_fill));
            }

        }

        void setEntries(List<Entry> entries) {
            this.entries = entries;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (entries != null) {
                Log.d("getItemCount():", String.valueOf(entries.size()));
                return entries.size();
            } else return 0;
        }


    }

}
