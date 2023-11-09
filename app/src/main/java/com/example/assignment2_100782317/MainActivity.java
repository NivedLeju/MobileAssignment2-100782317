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
       Button b1, add;
       LinearLayout list;
       MyDatabaseHelper dbhelper;
       SQLiteDatabase database;
       TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.listOfViews);
        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        dbhelper = new MyDatabaseHelper(this);
        database = dbhelper.getWritableDatabase();
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

}