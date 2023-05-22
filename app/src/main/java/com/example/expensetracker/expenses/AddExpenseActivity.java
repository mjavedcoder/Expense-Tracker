package com.example.expensetracker.expenses;

import static com.example.expensetracker.Constants.KEY_TRIP_ID;
import static com.example.expensetracker.Utils.getCurrentDate;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_AMOUNT_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_COMMENTS_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_DATE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.EXPENSE_TYPE_COLUMN;
import static com.example.expensetracker.database.DatabaseHelper.TBL_EXPENSES;
import static com.example.expensetracker.database.DatabaseHelper.TRIP_ID_COLUMN;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.database.Expense;
import com.example.expensetracker.databinding.ActivityAddExpenseBinding;

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private int tripId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Add Expense");

        tripId = getIntent().getIntExtra(KEY_TRIP_ID, 0);
        if (tripId < 1) return;

        binding.editTextDate.setText(getCurrentDate());
        binding.btnAddExpense.setOnClickListener(v -> {
            Expense expense = getExpenseFromUserInput();
            if (expense != null) {
                insertExpense(expense);
            }
        });
    }

    private void insertExpense(Expense expense) {
        DatabaseHelper dbHelper = new DatabaseHelper(AddExpenseActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EXPENSE_TYPE_COLUMN, expense.getType());
        values.put(EXPENSE_AMOUNT_COLUMN, expense.getAmount());
        values.put(EXPENSE_DATE_COLUMN, expense.getDate());
        values.put(EXPENSE_COMMENTS_COLUMN, expense.getComments());
        values.put(TRIP_ID_COLUMN, tripId);

        // Insert a new row for expense in the database, returning the ID of that new row.
        long newRowId = db.insert(TBL_EXPENSES, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving expense", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Expense added successfully! ID: " + newRowId, Toast.LENGTH_SHORT).show();
            openAllExpensesActivity();
        }
    }

    private void openAllExpensesActivity() {
        Intent intent = new Intent(AddExpenseActivity.this, AllExpensesActivity.class);
        intent.putExtra(KEY_TRIP_ID, tripId);
        startActivity(intent);
        finish();
    }

    private Expense getExpenseFromUserInput() {
        String type = binding.editTextType.getText().toString();
        String amountInput = binding.editTextAmount.getText().toString();
        String date = binding.editTextDate.getText().toString();
        String comments = binding.editTextComments.getText().toString();

        if (type.isEmpty() || amountInput.isEmpty() || date.isEmpty() || comments.isEmpty()) {
            Toast.makeText(this, "Please fill all data first", Toast.LENGTH_SHORT).show();
            return null;
        }
        return new Expense(type, getAmount(), date, comments, tripId);
    }

    private int getAmount() {
        String amountInput = binding.editTextAmount.getText().toString();
        int amount;
        try {
            amount = Integer.parseInt(amountInput);
        } catch (NumberFormatException e) {
            amount = 1;
        }
        return amount;
    }
}