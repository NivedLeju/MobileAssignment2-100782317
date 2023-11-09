package com.example.assignment2_100782317;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Geocoder";
    private static final int DATABASE_VERSION = 14;

    public static final String TABLE_ADDRESS = "addresses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "title";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_LAT = "latitude";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_ADDRESS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_LONG + " TEXT, " +
                    COLUMN_LAT + " TEXT); ";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE);
        } catch (SQLException e) {
            // Handle the exception (e.g., log it or show an error message)
            e.printStackTrace();
        }
    }

    public void deleteNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(noteId)};
        db.delete(TABLE_ADDRESS, whereClause, whereArgs);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        onCreate(db);
    }

    public boolean getCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ADDRESS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

}
