package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;


public class MyReceiver extends AppCompatActivity {

    Button pickTimebtn, save, back;
    TextView timeview;
    Dbhandler dbhandler;
    SQLiteDatabase remDB;
    EditText title, desc;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receiver);
        Dbhandler dbhandler = new Dbhandler(this);
        remDB = dbhandler.getWritableDatabase();
        title = findViewById(R.id.etRemTitle);
        desc = findViewById(R.id.etRemDesc);
        pickTimebtn = findViewById(R.id.pickTimebtn);
        timeview = findViewById(R.id.editTextTime);
        timeview.setEnabled(false);
        save = findViewById(R.id.btnSave);
        back = findViewById(R.id.btnBack);

        pickTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MyReceiver.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeview.setText(hourOfDay + ":" + String.format("%02d", minute));
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        backonClick();
        saveReminder();
    }
    public void backonClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    void saveReminder() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().trim().length() == 0) {
                    toast = Toast.makeText(getApplicationContext(), "Provide reminder name!", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (timeview.getText().toString().trim().length() == 0) {
                    toast = Toast.makeText(getApplicationContext(), "Provide reminder time!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    ContentValues val = new ContentValues();
                    val.put("name", title.getText().toString());
                    val.put("description", desc.getText().toString());
                    val.put("time", timeview.getText().toString());
                    //finally inserting data from edit text box to sqlite
                    remDB.insert("reminders", null, val);

                    toast = Toast.makeText(getApplicationContext(), "Reminder Set!", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}