package com.example.valentin.bugsbanny;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    final String LOG_TAG = "myLogs";
    SQLiteDatabase mdb;
    private static final String DB_TABLE = "t_category";
    public static final String COLUMN_TXT = "category_name";
    public static final String COLUMN_COLOR = "category_color";
    //*********
    private static final String DB_TABLE_T = "t_category_data"; // ид категории
    public static final String DB_TABLE_T_ID = "_id_t"; // ид категории
    public static final String DB_TABLE_DATE = "date_t"; // дата создания записи
    public static final String COLUMN_SUMM = "summ_t"; // сумма затрат

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
        try {
            mdb = getWritableDatabase();
        } catch (Exception e) {
            Log.d(LOG_TAG, "--- DBHelper mdb error ---");
            mdb = getReadableDatabase();
        }
        Log.d(LOG_TAG, "--- DBHelper ()---");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate DBHelper ---");
        mdb = db;
        db.execSQL("create table if not exists " + DB_TABLE + " ("
                + "_id integer primary key autoincrement, "
                + COLUMN_TXT + " text, "
                + COLUMN_COLOR + " integer" +
                ");");

        String sql = "create table if not exists " + DB_TABLE_T
                + " (_id integer primary key autoincrement, " +
                DB_TABLE_T_ID + " integer not null, " + //
                DB_TABLE_DATE + " text default CURRENT_TIMESTAMP, " +
                COLUMN_SUMM + " real default 0);";
//        Log.d(LOG_TAG, sql);
        db.execSQL(sql);
    }

    // закрыть подключение
    public void close() {
        if (mdb != null) mdb.close();
        Log.d(LOG_TAG, "--- close() DBHelper---");
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        try {
            return mdb.query(DB_TABLE, null, null, null, null, null, null);
        } catch (Exception e) {
            Log.d(LOG_TAG, "--- getAllData() DBHelper--- " + e.toString());
            return null;
        }
    }

        public Cursor getAllDataDetail(long id, String date_b, String date_e) {
        String where = DB_TABLE_T_ID + " = ? and " +
                DB_TABLE_DATE + " between ? and ? ";
        String[] selectionArgs = new String[]{id + "", date_b, date_e};
        String order_by = DB_TABLE_DATE;
        return mdb.query(DB_TABLE_T, null, where, selectionArgs, null, null, order_by);
    }

    public Cursor getAllDataDetailParent(String date_b, String date_e) {
        String sqlQuery = "SELECT " +
                "distinct tc._id, tc.category_name, tcd._id_t, " +//
                "sum(tcd.summ_t) as summ_t " +
                "FROM " + DB_TABLE_T + " tcd " +
                "inner join " + DB_TABLE + " tc " +
                "on tc._id = tcd._id_t " +
                "where tcd.date_t between ? and ? " +
                "group by tc.category_name " +
                "order by tc._id";
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }

    public Cursor getAllDataDetailChild(int id, String date_b, String date_e) {
        String sqlQuery = "SELECT " +
                "tcd.date_t, tcd.summ_t, tcd._id " +
                "FROM " + DB_TABLE_T + " tcd " +
                "where tcd.date_t between ? and ? and tcd._id_t = " + id;
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }


    public Cursor getAllDataGraph(String date_b, String date_e, String order) {
        String sqlQuery = "SELECT " +
                "sum(summ_t) as summ, tc.category_name, tc.category_color " +
                "FROM " + DB_TABLE_T + " tcd " +
                "inner join " + DB_TABLE + " tc " +
                "on tc._id = tcd._id_t " +
                "where tcd.date_t between ? and ? " +
                "group by tc._id " +
                "order by " + order;
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }

//    public Cursor getAllDataDetail(String date_b, String date_e) {
//        String sqlQuery = "SELECT " +
//                "tcd.date_t, tc.category_name, tcd.summ_t, tcd._id, tcd._id_t " +
//                "FROM " + DB_TABLE_T + " tcd " +
//                "inner join " + DB_TABLE + " tc " +
//                "on tc._id = tcd._id_t " +
//                "where tcd.date_t between ? and ? ";
//        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
//    }

    public Cursor getTotalAllDataDetail(String date_b, String date_e) {
        String sqlQuery = "SELECT " +
                "sum(tcd.summ_t) as total_sum " +
                "FROM " + DB_TABLE_T + " tcd " +
                "inner join " + DB_TABLE + " tc " +
                "on tc._id = tcd._id_t " +
                "where tcd.date_t between ? and ? ";
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }

    public Cursor getAllDataCalculator(String date_b, String date_e) {
        String sqlQuery = "SELECT " +
                "sum(summ_t) as summ, tc.category_name, tc._id " +
                "FROM " + DB_TABLE_T + " tcd " +
                "inner join " + DB_TABLE + " tc " +
                "on tc._id = tcd._id_t " +
                "where tcd.date_t between ? and ? " +
                "group by tc._id " +
                "order by tc._id";
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }

    public Cursor getAllDataCalculatorTotal(String date_b, String date_e) {
        String sqlQuery = "SELECT " +
                "sum(summ_t) as summ " +
                "FROM " + DB_TABLE_T + " tcd " +
                "where tcd.date_t between ? and ? ";
        return mdb.rawQuery(sqlQuery, new String[]{date_b, date_e});
    }

    // добавить запись в DB_TABLE
    public void addRec(String txt, int color) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_COLOR, color);
        mdb.insert(DB_TABLE, null, cv);
    }

    public long addRecExp(int id_t, float summ, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DB_TABLE_T_ID, id_t);
        cv.put(COLUMN_SUMM, summ);
        if (!date.trim().equals(""))
            cv.put(DB_TABLE_DATE, date);

        long count = mdb.insert(DB_TABLE_T, null, cv);
        return count;
    }

    public int delete_Summ(long id) {
        return mdb.delete(DB_TABLE_T, "_id = " + id, null);

    }

    public int update_Summ(long id, float summ) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SUMM, summ);
        String _id = id + "";
        return mdb.update(DB_TABLE_T, cv, "_id = ?", new String[]{_id});
    }

    public int delete_Category(long id) {
        return mdb.delete(DB_TABLE, "_id = " + id, null);
    }

    public int update_Category_Name(long id, String new_name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, new_name);
        String _id = id + "";
        return mdb.update(DB_TABLE, cv, "_id = ?", new String[]{_id});
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "--- onUpgrade() DBHelper---");
    }


}
