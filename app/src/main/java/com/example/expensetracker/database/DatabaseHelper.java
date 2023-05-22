package com.example.expensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db_expense_tracker";

    public static final String TBL_TRIPS = "tbl_trips";
    public static final String TRIP_ID_COLUMN = "tripId";
    public static final String TRIP_NAME_COLUMN = "name";
    public static final String TRIP_DESTINATION_COLUMN = "destination";
    public static final String TRIP_DATE_COLUMN = "date";
    public static final String TRIP_REQUIRES_ASSESSMENT_COLUMN = "requiresAssessment";
    public static final String TRIP_DESCRIPTION_COLUMN = "description";
    public static final String TRIP_DAYS_SPENT_COLUMN = "days";

    public static final String CREATE_TABLE_TRIPS = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER DEFAULT 0, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER DEFAULT 1)",
            TBL_TRIPS, TRIP_ID_COLUMN, TRIP_NAME_COLUMN, TRIP_DESTINATION_COLUMN, TRIP_REQUIRES_ASSESSMENT_COLUMN, TRIP_DATE_COLUMN, TRIP_DESCRIPTION_COLUMN, TRIP_DAYS_SPENT_COLUMN
    );

    public static final String TBL_EXPENSES = "tbl_expenses";
    public static final String EXPENSE_ID_COLUMN = "expenseId";
    public static final String EXPENSE_TYPE_COLUMN = "type";
    public static final String EXPENSE_AMOUNT_COLUMN = "amount";
    public static final String EXPENSE_DATE_COLUMN = "date";
    public static final String EXPENSE_COMMENTS_COLUMN = "comments";

    public static final String CREATE_TABLE_EXPENSES = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s INTEGER DEFAULT 0, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER, " +
                    "FOREIGN KEY (tripId) REFERENCES tbl_trips(tripId))",
            TBL_EXPENSES, EXPENSE_ID_COLUMN, EXPENSE_TYPE_COLUMN, EXPENSE_AMOUNT_COLUMN, EXPENSE_DATE_COLUMN, EXPENSE_COMMENTS_COLUMN, TRIP_ID_COLUMN
    );

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_EXPENSES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TRIPS + ";");
        onCreate(db);
    }
}
