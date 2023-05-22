package com.example.expensetracker.trips;

import static com.example.expensetracker.Constants.IS_ADD_TRIP_FLOW;
import static com.example.expensetracker.Constants.KEY_TRIP;
import static com.example.expensetracker.Constants.KEY_TRIP_ID;
import static com.example.expensetracker.Utils.getRequiresAssessmentStatus;
import static com.example.expensetracker.database.DatabaseHelper.TBL_EXPENSES;
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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.database.Trip;
import com.example.expensetracker.databinding.ActivityTripDetailsBinding;
import com.example.expensetracker.expenses.AllExpensesActivity;

public class TripDetailsActivity extends AppCompatActivity {

    private ActivityTripDetailsBinding binding;
    private DatabaseHelper dbHelper;
    private int tripId;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Trip Details");

        dbHelper = new DatabaseHelper(TripDetailsActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        trip = (Trip) getIntent().getSerializableExtra(KEY_TRIP);

        if (trip != null) {
            updateUI(trip);
        }

        binding.btnEditTrip.setOnClickListener(v -> openEditTripDetailsActivity());
        binding.btnDeleteTrip.setOnClickListener(v -> deleteTrip());
        binding.btnAllExpenses.setOnClickListener(v -> openAllExpensesActivity());
    }

    private void openEditTripDetailsActivity() {
        if (trip == null) return;
        Intent intent = new Intent(TripDetailsActivity.this, AddTripActivity.class);
        intent.putExtra(IS_ADD_TRIP_FLOW, false);
        intent.putExtra(KEY_TRIP, trip);
        startActivity(intent);
        finish();
    }

    private void openAllExpensesActivity() {
        if (trip == null) return;
        Intent intent = new Intent(TripDetailsActivity.this, AllExpensesActivity.class);
        intent.putExtra(KEY_TRIP_ID, trip.getId());
        startActivity(intent);
        finish();
    }

    private Trip fetchTripDetails() {
        Trip trip = new Trip();

        // Open the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] projection = {
                TRIP_ID_COLUMN,
                TRIP_NAME_COLUMN,
                TRIP_DESTINATION_COLUMN,
                TRIP_REQUIRES_ASSESSMENT_COLUMN,
                TRIP_DATE_COLUMN,
                TRIP_DESCRIPTION_COLUMN,
                TRIP_DAYS_SPENT_COLUMN
        };

        // Define the selection criteria
        String selection = "tripId = ?";
        String[] selectionArgs = {String.valueOf(tripId)};

        // Execute the query and get the results
        Cursor cursor = db.query(
                TBL_TRIPS,   // The table to query
                projection,    // The columns to retrieve
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null,          // Don't group the results
                null,          // Don't filter by row groups
                null           // The sort order
        );

        // Loop through the results and process each row
        if (cursor.moveToFirst()) {

            // Get the values from the cursor
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_ID_COLUMN));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_NAME_COLUMN));
            String destination = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DESTINATION_COLUMN));
            int requiresAssessment = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_REQUIRES_ASSESSMENT_COLUMN));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DATE_COLUMN));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TRIP_DESCRIPTION_COLUMN));
            int days = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_DAYS_SPENT_COLUMN));

            // Do something with the values
            trip.setId(id);
            trip.setName(name);
            trip.setDestination(destination);
            trip.setRequiresAssessment(requiresAssessment);
            trip.setDate(date);
            trip.setDescription(description);
            trip.setDaysSpent(days);
        }

        // Close the cursor and the database
        cursor.close();
        db.close();

        if (!trip.getName().isEmpty()) {
            return trip;
        } else {
            return null;
        }
    }

    public void deleteTrip() {
        if (trip == null) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TRIP_ID_COLUMN + " = ?";
        String[] selectionArgs = {String.valueOf(trip.getId())};

        int deletedExpenseRowsCount = db.delete(TBL_EXPENSES, selection, selectionArgs);
        int deletedTripRowsCount = db.delete(TBL_TRIPS, selection, selectionArgs);
        db.close();

        if (deletedTripRowsCount > 0 && deletedExpenseRowsCount > 0) {
            Toast.makeText(TripDetailsActivity.this, "Record deleted successfully!", Toast.LENGTH_SHORT).show();
            openAllTripsActivity();
        }
    }

    private void openAllTripsActivity() {
        Intent intent = new Intent(TripDetailsActivity.this, AllTripsActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(Trip trip) {
        int requiresAssessmentFlag = trip.getRequiresAssessment();
        String requiresAssessmentStatus = getRequiresAssessmentStatus(requiresAssessmentFlag);
        String description = trip.getDescription();

        binding.textViewName.setText("Name: " + trip.getName());
        binding.textViewDestination.setText("Destination: " + trip.getDestination());
        binding.textViewDate.setText("Date: " + trip.getDate());
        binding.titleRequiresAssessment.setText("Requires Assessment: " + requiresAssessmentStatus);
        binding.textViewDaysSpent.setText("Days Spent: " + trip.getDaysSpent());

        if (!description.isBlank()) {
            binding.textViewDescription.setVisibility(View.VISIBLE);
            binding.textViewDescription.setText("Description: " + trip.getDescription());
        }
    }
}