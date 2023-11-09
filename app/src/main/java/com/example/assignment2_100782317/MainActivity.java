package com.example.assignment2_100782317;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
       Button add;
       LinearLayout list;
       MyDatabaseHelper dbhelper;
       SQLiteDatabase database;
       TextView search;
        double latitude, longitude;
        String address2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.listOfViews);
        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        dbhelper = new MyDatabaseHelper(this);
        database = dbhelper.getWritableDatabase();
        InputStream inputStream = getResources().openRawResource(R.raw.geocoding);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(dbhelper.getCount()) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] coordinates = line.split(",");
                    if (coordinates.length == 2) {
                        latitude = Double.parseDouble(coordinates[0]);
                        longitude = Double.parseDouble(coordinates[1]);
                        address2 = getCompleteAddress(latitude, longitude);
                        ContentValues values = new ContentValues();
                        values.put(MyDatabaseHelper.COLUMN_ADDRESS, address2);
                        values.put(MyDatabaseHelper.COLUMN_LONG, longitude);
                        values.put(MyDatabaseHelper.COLUMN_LAT, latitude);

                        database.insert(MyDatabaseHelper.TABLE_ADDRESS, null, values);
                    } else {
                        Toast.makeText(this, "invalid", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error reading file", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        displayNotes();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditCoords.class);
                startActivity(intent);
            }
        });
    }
    private void performSearch(String query) {
        list.removeAllViews(); // Clear previous notes

        String[] projection = {
                MyDatabaseHelper.COLUMN_ID,
                MyDatabaseHelper.COLUMN_ADDRESS,
                MyDatabaseHelper.COLUMN_LONG,
                MyDatabaseHelper.COLUMN_LAT
        };

        String selection = MyDatabaseHelper.COLUMN_ADDRESS + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};

        Cursor cursor = database.query(
                MyDatabaseHelper.TABLE_ADDRESS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
            @SuppressLint("Range") String longi = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LONG));
            @SuppressLint("Range") String lati = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LAT));

            // Create a TextView to display the note
            TextView noteTextView = new TextView(this);
            noteTextView.setTextSize(20);
            noteTextView.setGravity(Gravity.CENTER);

            // Use line breaks to display title and description on separate lines
            noteTextView.setText("\nAddress: " + address + "\nLongitude: " + longi+"\nLatitude: "+lati+"\n");

            noteTextView.setBackgroundColor(0xFF7cd8fc);
            noteTextView.setTextColor(Color.BLACK);
            list.addView(noteTextView);
            TextView empty = new TextView(this);
            empty.setText("");
            list.addView(empty);

            noteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, EditCoords.class);
                    intent.putExtra("NOTE_ID", id);
                    startActivity(intent);
                }
            });

        }
        cursor.close();
    }

    private void displayNotes() {
        list.removeAllViews(); // Clear previous notes
        String[] projection = {
                MyDatabaseHelper.COLUMN_ID,
                MyDatabaseHelper.COLUMN_ADDRESS,
                MyDatabaseHelper.COLUMN_LONG,
                MyDatabaseHelper.COLUMN_LAT
        };
        Cursor cursor = database.query(
                MyDatabaseHelper.TABLE_ADDRESS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
            @SuppressLint("Range") String longi = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LONG));
            @SuppressLint("Range") String lati = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_LAT));
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
            // Create a TextView to display the note
            TextView noteTextView = new TextView(this);
            noteTextView.setTextSize(20);
            noteTextView.setGravity(Gravity.CENTER);

            // Use line breaks to display title and description on separate lines
            noteTextView.setText("\nAddress: " + address + "\nLongitude: " + longi+"\nLatitude: "+lati+"\n");

            noteTextView.setBackgroundColor(0xFF7cd8fc);
            noteTextView.setTextColor(Color.BLACK);
            list.addView(noteTextView);
            TextView empty = new TextView(this);
            empty.setText("");
            list.addView(empty);
            noteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, EditCoords.class);
                    intent.putExtra("NOTE_ID", id);
                    startActivity(intent);
                }
            });
        }
        cursor.close();
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