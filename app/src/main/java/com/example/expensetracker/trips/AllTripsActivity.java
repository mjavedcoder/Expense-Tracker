package com.example.expensetracker.trips;

import static com.example.expensetracker.Constants.KEY_TRIP;
import static com.example.expensetracker.database.DatabaseHelper.TBL_TRIPS;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DATE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DAYS_SPENT_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DESCRIPTION_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DESTINATION_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_ID_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_NAME_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_REQUIRES_ASSESSMENT_COLUMN;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.database.Trip;
import com.example.expensetracker.databinding.ActivityAllTripsBinding;

import java.util.ArrayList;
import java.util.List;

public class AllTripsActivity extends AppCompatActivity implements TripsAdapter.OnTripItemClickListener {

    private ActivityAllTripsBinding binding;
    private DatabaseHelper dbHelper;
    private TripsAdapter adapter;
    private List<Trip> trips = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("All Trips");
        dbHelper = new DatabaseHelper(AllTripsActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        initViews();
        initRecyclerView();
    }

    private void initViews() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        binding.btnAddTrip.setOnClickListener(v -> {
            Intent intent = new Intent(AllTripsActivity.this, AddTripActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTrips();
    }

    private void initRecyclerView() {
        adapter = new TripsAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(this);
    }

    private void filter(String text) {
        List<Trip> filteredList = new ArrayList<>();
        for (Trip trip : trips) {
            if (trip.getName().toLowerCase().contains(text.toLowerCase()) ||
                    trip.getDestination().toLowerCase().contains(text.toLowerCase()) ||
                    trip.getDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(trip);
            }
        }
        adapter.submitList(filteredList);

        if (filteredList.isEmpty()) {
            binding.textViewNoTrips.setVisibility(View.VISIBLE);
        } else {
            binding.textViewNoTrips.setVisibility(View.INVISIBLE);
        }
    }

    private void fetchTrips() {
        trips.clear();

        // Create and/or open a database to read from it
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                TRIP_ID_COLUMN,
                TRIP_NAME_COLUMN,
                TRIP_DESTINATION_COLUMN,
                TRIP_REQUIRES_ASSESSMENT_COLUMN,
                TRIP_DATE_COLUMN,
                TRIP_DESCRIPTION_COLUMN,
                TRIP_DAYS_SPENT_COLUMN
        };

        // Perform a query on the contacts table
        Cursor cursor = db.query(
                TBL_TRIPS,             // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null                   // The sort order
        );

        try {
            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Get the values from the cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_ID_COLUMN));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_NAME_COLUMN));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DESTINATION_COLUMN));
                int requiresAssessment = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_REQUIRES_ASSESSMENT_COLUMN));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DATE_COLUMN));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DESCRIPTION_COLUMN));
                int days = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_DAYS_SPENT_COLUMN));

                // Do something with the values
                Trip trip = new Trip();
                trip.setId(id);
                trip.setName(name);
                trip.setDestination(destination);
                trip.setRequiresAssessment(requiresAssessment);
                trip.setDate(date);
                trip.setDescription(description);
                trip.setDaysSpent(days);
                trips.add(trip);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its resources and makes it invalid.
            cursor.close();

            if (trips.isEmpty()) {
                binding.textViewNoTrips.setVisibility(View.VISIBLE);
            }
            adapter.submitList(trips);
        }
    }

    @Override
    public void onTripItemClick(Trip trip) {
        Intent intent = new Intent(AllTripsActivity.this, TripDetailsActivity.class);
        intent.putExtra(KEY_TRIP, trip);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_trips_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clearDatabase) {
            showConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Database?")
                .setMessage("Do you really want to clear entire database?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.clearDatabase(db);
                    adapter.submitList(new ArrayList<>());
                    binding.textViewNoTrips.setVisibility(View.VISIBLE);
                })
                .setNegativeButton(android.R.string.no, (dialog, whichButton) -> {
                    dialog.dismiss();
                })
                .show();
    }
}