package com.example.expensetracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getRequiresAssessmentStatus(int requiresAssessmentFlag) {
        if (requiresAssessmentFlag == 0) {
            return "No";
        } else {
            return "Yes";
        }
    }

    public static String getFormattedAmount(int amount) {
        return amount + "£";
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

}
