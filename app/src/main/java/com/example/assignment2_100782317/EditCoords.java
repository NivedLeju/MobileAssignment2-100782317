package com.example.assignment2_100782317;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

public class EditCoords extends AppCompatActivity {
    private int noteId;
    MyDatabaseHelper dbhelper;
    SQLiteDatabase db;
    EditText lat,lon;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coords);
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        Intent intent = getIntent();
        noteId = intent.getIntExtra("NOTE_ID", -1);
        dbhelper = new MyDatabaseHelper(this);
        db = dbhelper.getWritableDatabase();
        if (noteId != -1) {
            // Load note details from the database based on the noteId
            Cursor cursor = getNoteDetails(noteId);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
                @SuppressLint("Range") String longi = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LONG));
                @SuppressLint("Range") String lati = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LAT));


                // Populate UI with note details
                lat.setText(lati);
                lon.setText(longi);

            }
            cursor.close();
        }

        save = findViewById(R.id.saveCoords);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });
        Button discard = findViewById(R.id.discard);
        discard.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (noteId != -1) {
                    // If noteId is valid, delete the note from the database
                    dbhelper.deleteNoteById(noteId);

                    // Return to the main activity after deletion
                    Intent intent = new Intent(EditCoords.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(EditCoords.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveDetails(){
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        String latitudeText = lat.getText().toString();
        String longitudeText = lon.getText().toString();
        double la = Double.parseDouble(latitudeText);
        double lo = Double.parseDouble(longitudeText);
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_ADDRESS, getCompleteAddress(la,lo));
        values.put(MyDatabaseHelper.COLUMN_LONG, longitudeText);
        values.put(MyDatabaseHelper.COLUMN_LAT, latitudeText);


        if (noteId != -1) {
            // If noteId is provided, update the existing note
            String whereClause = MyDatabaseHelper.COLUMN_ID + " = ?";
            String[] whereArgs = {String.valueOf(noteId)};
            db.update(MyDatabaseHelper.TABLE_ADDRESS, values, whereClause, whereArgs);
        } else {
            // If noteId is not provided, insert a new note
            db.insert(MyDatabaseHelper.TABLE_ADDRESS, null, values);
        }

        // Return to the main activity
        Intent intent = new Intent(EditCoords.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private Cursor getNoteDetails(int noteId) {
        String[] projection = {
                MyDatabaseHelper.COLUMN_ID,
                MyDatabaseHelper.COLUMN_ADDRESS,
                MyDatabaseHelper.COLUMN_LONG,
                MyDatabaseHelper.COLUMN_LAT
        };

        String selection = MyDatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(noteId)};

        return db.query(
                MyDatabaseHelper.TABLE_ADDRESS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private String getCompleteAddress(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strToReturn = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strToReturn.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strToReturn.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;

    }
}

