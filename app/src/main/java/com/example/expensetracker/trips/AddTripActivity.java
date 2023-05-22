package com.example.expensetracker.trips;

import static com.example.expensetracker.Constants.IS_ADD_TRIP_FLOW;
import static com.example.expensetracker.Constants.KEY_TRIP;
import static com.example.expensetracker.Utils.getRequiresAssessmentStatus;
import static com.example.expensetracker.database.DatabaseHelper.TBL_TRIPS;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DATE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DAYS_SPENT_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DESCRIPTION_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_DESTINATION_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_ID_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_NAME_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_REQUIRES_ASSESSMENT_COLUMN;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.database.Trip;
import com.example.expensetracker.databinding.ActivityAddTripBinding;

public class AddTripActivity extends AppCompatActivity {

    private ActivityAddTripBinding binding;
    private boolean isAddTripFlow = true;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isAddTripFlow = getIntent().getBooleanExtra(IS_ADD_TRIP_FLOW, true);
        trip = (Trip) getIntent().getSerializableExtra(KEY_TRIP);

        if (isAddTripFlow) {
            binding.btnAddTrip.setVisibility(View.VISIBLE);
        } else {
            binding.btnUpdateTripDetails.setVisibility(View.VISIBLE);
        }
        updateTitle();

        if (trip != null) {
            updateUI(trip);
        }

        binding.btnAddTrip.setOnClickListener(v -> {
            Trip trip = getTripFromUserInput();
            if (trip != null) {
                showConfirmationDialog(trip);
            }
        });

        binding.btnUpdateTripDetails.setOnClickListener(v -> {
            Trip trip = getTripFromUserInput();
            if (trip != null) {
                showConfirmationDialog(trip);
            }
        });
    }

    private void updateTitle() {
        String title;
        if (isAddTripFlow) {
            title = "Add Trip";
        } else {
            title = "Update Trip";
        }
        setTitle(title);
    }

    private void showConfirmationDialog(Trip trip) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_confirm_trip_dialog);

        int requiresAssessmentFlag = trip.getRequiresAssessment();
        String requiresAssessmentStatus = getRequiresAssessmentStatus(requiresAssessmentFlag);
        String description = trip.getDescription();

        TextView textViewName = (TextView) dialog.findViewById(R.id.textViewName);
        TextView textViewDestination = (TextView) dialog.findViewById(R.id.textViewDestination);
        TextView textViewDate = (TextView) dialog.findViewById(R.id.textViewDate);
        TextView textViewDaysSpent = (TextView) dialog.findViewById(R.id.textViewDaysSpent);
        TextView titleRequiresAssessment = (TextView) dialog.findViewById(R.id.titleRequiresAssessment);
        TextView textViewDescription = (TextView) dialog.findViewById(R.id.textViewDescription);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        Button btnEditDetails = (Button) dialog.findViewById(R.id.btnEditDetails);

        textViewName.setText("Name: " + trip.getName());
        textViewDestination.setText("Destination: " + trip.getDestination());
        textViewDate.setText("Date: " + trip.getDate());
        titleRequiresAssessment.setText("Requires Assessment: " + requiresAssessmentStatus);
        textViewDaysSpent.setText("Days Spent: " + trip.getDaysSpent());

        if (!description.isBlank()) {
            textViewDescription.setVisibility(View.VISIBLE);
            textViewDescription.setText("Description: " + trip.getDescription());
        }

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (isAddTripFlow) {
                insertTrip(trip);
            } else {
                updateTrip(trip);
            }
        });
        btnEditDetails.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private Trip getTripFromUserInput() {
        String name = binding.editTextName.getText().toString();
        String destination = binding.editTextDestination.getText().toString();
        String date = binding.editTextDate.getText().toString();
        String description = binding.editTextDescription.getText().toString();

        if (name.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all data first", Toast.LENGTH_SHORT).show();
            return null;
        }
        return new Trip(name, destination, date, getRiskAssessmentStatus(), description, getDaysSpent());
    }

    private void updateUI(Trip trip) {
        int requiresAssessmentFlag = trip.getRequiresAssessment();

        binding.title.setText("Update Trip Details");
        binding.editTextName.setText("" + trip.getName());
        binding.editTextDestination.setText("" + trip.getDestination());
        binding.editTextDate.setText("" + trip.getDate());
        binding.editTextDescription.setText("" + trip.getDescription());
        binding.editTextDaysSpent.setText("" + trip.getDaysSpent());

        if (requiresAssessmentFlag == 1) {
            binding.rdbYes.setChecked(true);
        } else {
            binding.rdbNo.setChecked(true);
        }
    }

    private void insertTrip(Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(AddTripActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIP_NAME_COLUMN, trip.getName());
        values.put(TRIP_DESTINATION_COLUMN, trip.getDestination());
        values.put(TRIP_DATE_COLUMN, trip.getDate());
        values.put(TRIP_REQUIRES_ASSESSMENT_COLUMN, trip.getRequiresAssessment());
        values.put(TRIP_DESCRIPTION_COLUMN, trip.getDescription());
        values.put(TRIP_DAYS_SPENT_COLUMN, trip.getDaysSpent());

        // Insert a new row for trip in the database, returning the ID of that new row.
        long newRowId = db.insert(TBL_TRIPS, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving trip", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Trip saved successfully! ID: " + newRowId, Toast.LENGTH_SHORT).show();
            openAllTripsActivity();
        }
    }

    private void updateTrip(Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(AddTripActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIP_NAME_COLUMN, trip.getName());
        values.put(TRIP_DESTINATION_COLUMN, trip.getDestination());
        values.put(TRIP_DATE_COLUMN, trip.getDate());
        values.put(TRIP_REQUIRES_ASSESSMENT_COLUMN, trip.getRequiresAssessment());
        values.put(TRIP_DESCRIPTION_COLUMN, trip.getDescription());
        values.put(TRIP_DAYS_SPENT_COLUMN, trip.getDaysSpent());

        // Updating row
        int count = db.update(
                TBL_TRIPS,
                values,
                TRIP_ID_COLUMN + " = ?",
                new String[]{String.valueOf(this.trip.getId())}
        );
        db.close();

        // Check the result
        if (count > 0) {
            // Update was successful
            Toast.makeText(this, "Trip updated successfully!", Toast.LENGTH_SHORT).show();
            openAllTripsActivity();
        } else {
            // No rows were updated
            Toast.makeText(this, "Error with updating trip", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAllTripsActivity() {
        Intent intent = new Intent(AddTripActivity.this, AllTripsActivity.class);
        startActivity(intent);
        finish();
    }

    private int getDaysSpent() {
        String daysSpentInput = binding.editTextDaysSpent.getText().toString();
        int daysSpent;
        try {
            daysSpent = Integer.parseInt(daysSpentInput);
        } catch (NumberFormatException e) {
            daysSpent = 1;
        }
        if (daysSpent < 1) { // Set the number of days spent to 1 even if user updates it to 0
            return 1;
        } else {
            return daysSpent;
        }
    }

    private int getRiskAssessmentStatus() {
        if (binding.rdbYes.isChecked()) {
            return 1;
        } else {
            return 0;
        }
    }
}