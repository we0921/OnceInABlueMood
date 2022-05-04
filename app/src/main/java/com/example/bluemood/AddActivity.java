package com.example.bluemood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.bluemood.db.Entry;
import com.example.bluemood.db.EntryDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.OffsetTime;
import java.util.Calendar;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private int entry_id;
    private int mood;
    private int month;
    private int day;
    private int year;
    private String time;
    private String location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("theme", false)) {
            setTheme(R.style.Theme_BlueMood_Dark_NoBar);
        } else {
            setTheme(R.style.Theme_BlueMood_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setSupportActionBar(findViewById(R.id.toolbar));

        entry_id = getIntent().getIntExtra("entry_id", -1);

        // Location stuff
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location lastLoc = locationResult.getLastLocation();
                String locString = lastLoc.getLatitude() + "," + lastLoc.getLongitude();
                location = locString;
                ((Button) findViewById(R.id.location_button)).setText(locString);
            }
        };

        findViewById(R.id.location_button).setOnClickListener(view -> {
            getLastLocation();
        });

        findViewById(R.id.date_picker_button).setOnClickListener((view -> {
            DatePicker datePicker = new DatePicker();
            datePicker.show(getSupportFragmentManager(), "Date Picker");
        }));

        findViewById(R.id.time_picker_button).setOnClickListener((view -> {
            com.example.bluemood.TimePicker timePicker = new com.example.bluemood.TimePicker();
            timePicker.show(getSupportFragmentManager(), "Time Picker");
        }));

        if (savedInstanceState == null && entry_id != -1) {
            EntryDatabase.getEntry(entry_id, entry -> {

                if (entry != null) {
                    // Set the mood button according to the retrieved entry
                    mood = entry.mood;
                    Log.d("Mood switch:", "" + mood);
                    switch (entry.mood) {
                        case 0:
                            ((RadioButton) findViewById(R.id.mood_0_button)).setChecked(true);
                            break;
                        case 1:
                            ((RadioButton) findViewById(R.id.mood_1_button)).setChecked(true);
                            break;
                        case 2:
                            ((RadioButton) findViewById(R.id.mood_2_button)).setChecked(true);
                            break;
                        case 3:
                            ((RadioButton) findViewById(R.id.mood_3_button)).setChecked(true);
                            break;
                        case 4:
                            ((RadioButton) findViewById(R.id.mood_4_button)).setChecked(true);
                            break;
                    }

                    // Set the description
                    ((EditText) findViewById(R.id.txtEditDesc)).setText(entry.description);

                    // Set the date
                    month = entry.month;
                    day = entry.day;
                    year = entry.year;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    String dateString = "";
                    dateString = dateString + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en")) + " ";
                    dateString = dateString + entry.day + ", ";
                    dateString = dateString + entry.year;

                    ((Button) findViewById(R.id.date_picker_button)).setText(dateString);

                    // Set the time
                    time = entry.time;
                    ((Button) findViewById(R.id.time_picker_button)).setText(entry.time);

                    // Set the location
                    location = entry.location;
                    if (location != null) {
                        ((Button) findViewById(R.id.location_button)).setText(entry.location);
                    }
                } else {
                    // Set the mood - default is neutral
                    mood = 2;

                    // Set the location - default is blank
                    location = "";

                    // Set the date - default is current date
                    Calendar calendar = Calendar.getInstance();

                    String dateString = "";
                    dateString = dateString + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en")) + " ";
                    dateString = dateString + calendar.get(Calendar.DATE) + ", ";
                    dateString = dateString + calendar.get(Calendar.YEAR);
                    ((Button) findViewById(R.id.date_picker_button)).setText(dateString);

                    // Set the time - default is the current time
                    ((Button) findViewById(R.id.time_picker_button)).setText((OffsetTime.now().toLocalTime()).toString());
                }
            });
        } else if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("time")) {
                time = savedInstanceState.getString("time");
                ((Button) findViewById(R.id.time_picker_button)).setText(time);
            }
            if (savedInstanceState.containsKey("location")) {
                location = savedInstanceState.getString("location");
                ((Button) findViewById(R.id.location_button)).setText(location);
            }

            Calendar calendar = Calendar.getInstance();

            if (savedInstanceState.containsKey("month")) {
                month = savedInstanceState.getInt("month");
            } else {
                month = calendar.get(Calendar.MONTH);
            }
            if (savedInstanceState.containsKey("day")) {
                day = savedInstanceState.getInt("day");
            } else {
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }

            if (savedInstanceState.containsKey("year")) {
                year = savedInstanceState.getInt("year");
            } else {
                year = calendar.get(Calendar.YEAR);
            }

            calendar.set(year, month, day);
            String dateString = "";
            dateString = dateString + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en")) + " ";
            dateString = dateString + calendar.get(Calendar.DATE) + ", ";
            dateString = dateString + calendar.get(Calendar.YEAR);
            ((Button) findViewById(R.id.date_picker_button)).setText(dateString);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("year", year);
        outState.putInt("month", month);
        outState.putInt("day", day);

        if (!(((Button)findViewById(R.id.location_button)).getText().toString().equals("Location"))) {
            outState.putString("location", ((Button)findViewById(R.id.location_button)).getText().toString());
        }
        if (time != null) {
            outState.putString("time", time);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (!checked) {
            mood = -1;
        } else {
            // Check which was clicked
            switch (view.getId()) {
                case R.id.mood_0_button:
                    mood = 0;
                    break;
                case R.id.mood_1_button:
                    mood = 1;
                    break;
                case R.id.mood_2_button:
                    mood = 2;
                    break;
                case R.id.mood_3_button:
                    mood = 3;
                    break;
                case R.id.mood_4_button:
                    mood = 4;
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add, menu);
        if (entry_id == -1) {
            menu.getItem(1).setIcon(R.drawable.ic_cancel);
            menu.getItem(1).setTitle(R.string.menu_cancel);
            setTitle("Add entry");
        } else {
            setTitle("Edit entry");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                updateDatabase();
                return true;
            case R.id.menu_delete:
                if (entry_id != -1) {
                    ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog();
                    confirmDialog.show(getSupportFragmentManager(), "deletionConfirmation");
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDatabase() {
        String locationString = ((Button) findViewById(R.id.location_button)).getText().toString();

        Entry entry = new Entry(
                entry_id == -1 ? 0 : entry_id,
                mood,
                month,
                day,
                year,
                time,
                ((EditText) findViewById(R.id.txtEditDesc)).getText().toString(),
                "",
                locationString.equals("Location") ? "" : locationString);
        Log.d("UpdateDatabase(): ", String.valueOf(entry));
        if (entry_id == -1) {
            EntryDatabase.insert(entry);
        } else {
            EntryDatabase.update(entry);
        }
        finish(); // Quit activity
    }

    public void deleteRecord() {
        EntryDatabase.delete(entry_id);
    }

    public static class ConfirmDeleteDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Delete this entry?")
                    .setMessage("Deletions can't be undone!")
                    .setPositiveButton("Delete",
                            (dialog, id) -> {
                                ((AddActivity) getActivity()).deleteRecord();
                                getActivity().finish();
                            })
                    .setNegativeButton("Return to entries",
                            (dialog, id) -> getActivity().finish());
            return builder.create();
        }
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Save the values in the instance variables
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        Log.d("Date set:", year + " " + month + " " + dayOfMonth);

        // Create a calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Convert to string
        String dateString = "";
        dateString = dateString + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en")) + " ";
        dateString = dateString + calendar.get(Calendar.DATE) + ", ";
        dateString = dateString + calendar.get(Calendar.YEAR);
        ((Button) findViewById(R.id.date_picker_button)).setText(dateString);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        this.time = hourOfDay + ":" + ((minute < 10) ? "0" + minute : minute);
        ((Button) findViewById(R.id.time_picker_button)).setText(time);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String locString = location.getLatitude() + "," + location.getLongitude();
                            ((Button) findViewById(R.id.location_button)).setText(locString);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        com.google.android.gms.location.LocationRequest locationRequest;
        locationRequest = new com.google.android.gms.location.LocationRequest();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}
