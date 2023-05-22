package com.example.expensetracker.expenses;

import static com.example.expensetracker.Constants.KEY_TRIP_ID;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_AMOUNT_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_COMMENTS_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_DATE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_ID_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_TYPE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TBL_EXPENSES;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_ID_COLUMN;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.database.Expense;
import com.example.expensetracker.databinding.ActivityAllExpensesBinding;
import com.example.expensetracker.trips.AddTripActivity;
import com.example.expensetracker.trips.AllTripsActivity;

import java.util.ArrayList;
import java.util.List;

public class AllExpensesActivity extends AppCompatActivity {

    private ActivityAllExpensesBinding binding;
    private int tripId = 0;
    private DatabaseHelper dbHelper;
    private ExpensesAdapter adapter;
    private List<Expense> expenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("All Expenses");
        tripId = getIntent().getIntExtra(KEY_TRIP_ID, 0);
        if (tripId < 1) return;

        dbHelper = new DatabaseHelper(AllExpensesActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        initRecyclerView();

        binding.btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(AllExpensesActivity.this, AddExpenseActivity.class);
            intent.putExtra(KEY_TRIP_ID, tripId);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchExpenses();
    }

    private void initRecyclerView() {
        adapter = new ExpensesAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchExpenses() {
        expenses.clear();

        // Create and/or open a database to read from it
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                EXPENSE_ID_COLUMN,
                EXPENSE_TYPE_COLUMN,
                EXPENSE_AMOUNT_COLUMN,
                EXPENSE_DATE_COLUMN,
                EXPENSE_COMMENTS_COLUMN,
                TRIP_ID_COLUMN
        };

        String selection = TRIP_ID_COLUMN + " = ?";
        String[] selectionArgs = {String.valueOf(tripId)};

        // Perform a query on the contacts table
        Cursor cursor = db.query(
                TBL_EXPENSES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {
            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Get the values from the cursor
                int expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_ID_COLUMN));
                String expenseType = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_TYPE_COLUMN));
                int expenseAmount = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_AMOUNT_COLUMN));
                String expenseDate = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DATE_COLUMN));
                String expenseComments = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_COMMENTS_COLUMN));
                int tripId = cursor.getInt(cursor.getColumnIndexOrThrow(TRIP_ID_COLUMN));

                // Do something with the values
                Expense expense = new Expense(expenseType, expenseAmount, expenseDate, expenseComments, tripId);
                expenses.add(expense);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its resources and makes it invalid.
            cursor.close();

            if (expenses.isEmpty()) {
                binding.textViewNoTrips.setVisibility(View.VISIBLE);
            }
            adapter.submitList(expenses);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_expenses_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            openAllTripsActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAllTripsActivity() {
        Intent intent = new Intent(AllExpensesActivity.this, AllTripsActivity.class);
        startActivity(intent);
        finish();
    }
}