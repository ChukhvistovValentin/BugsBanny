package com.example.valentin.bugsbanny;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import java.util.Calendar;

public class Constant {
    //    private int month_index = -1;
    private static Current_Month_Index cmi = null;
    public static DBHelper dbHelper;
    final static String LOG_TAG = "myLogs";

    private static Integer[] pic_category_default = {R.drawable.doroga,
            R.drawable.food, R.drawable.prodtovar, R.drawable.mobile,
            R.drawable.home_pay, R.drawable.komunalka,
            R.drawable.pay, R.drawable.inshe, R.drawable.other};

    private static int[] color = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.GRAY, Color.WHITE, Color.MAGENTA, Color.CYAN};


    public static Integer get_Picture_Index_Default(int index) {
        int pic_length = pic_category_default.length;

        if ((index > -1) && (index <= (pic_length - 1))) {
            return pic_category_default[index - 1];
        } else return pic_category_default[pic_length - 1];
    }

    public static void createDBHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
            Log.d(LOG_TAG, "--- createDBHelper == null ConstantAll---");
            insertDefaultCategory(context);
        }
        Log.d(LOG_TAG, "--- createDBHelper != null ConstantAll---");
    }

    private static void insertDefaultCategory(Context context) {
        Log.d(LOG_TAG, "--- insertDefaultCategory ConstantAll---");
        Cursor c = dbHelper.getAllData();
        if ((c != null) && (c.getCount() == 0)) {
            String date[] = {context.getString(R.string.d1), context.getString(R.string.d2),
                    context.getString(R.string.d3), context.getString(R.string.d4),
                    context.getString(R.string.d5), context.getString(R.string.d6),
                    context.getString(R.string.d7), context.getString(R.string.d8)};
            if (!c.moveToFirst()) {
                for (int i = 0; i < date.length; i++) {
                    dbHelper.addRec(date[i], color[i]);
                }
            }
        }
        if (c != null) c.close();
    }

    public static int getCurrentMonth() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public static void setMonth_index(int month_index_) {
        if (cmi == null)
            cmi = new Current_Month_Index(month_index_);
        else
            cmi.setMonth_index(month_index_);
    }

    public static int getMonth_index() {
        if (cmi == null)
            return -1;
        else
            return cmi.getMonth_index();
    }


    private static class Current_Month_Index {
        private int month_index = -1;

        public Current_Month_Index(int month_index) {
            this.month_index = month_index;
        }

        public int getMonth_index() {
            return month_index;
        }

        public void setMonth_index(int month_index) {
            this.month_index = month_index;
        }
    }
}
