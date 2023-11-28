package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class EditReminder extends AppCompatActivity {
    int getId, queId, min, hour;
    String id, queTitle, queDesc;
    EditText title, desc, etime;
    Button back, save, delete,picktime;
    SQLiteDatabase db;
    String dbname = "ReminderDB";
    Cursor cursor;
    Toast toast;
    SQLiteDatabase remDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
        Dbhandler dbhandler = new Dbhandler(this);
        remDB = dbhandler.getWritableDatabase();
        title = findViewById(R.id.etRemTitle);
        desc = findViewById(R.id.etRemDesc);
        etime = findViewById(R.id.editTextTime);
        etime.setEnabled(false);
        back = findViewById(R.id.btnBack);
        save = findViewById(R.id.btnSave);
        delete = findViewById(R.id.btnDelete);
        picktime = findViewById(R.id.pickTimebtn);
        clockbtn();

        Intent intent = getIntent();
        getId = intent.getIntExtra("id", 0);
        id = String.valueOf(getId);

        db = openOrCreateDatabase(dbname, Context.MODE_PRIVATE, null);
        cursor = db.rawQuery("Select * from reminders where id=? ", new String[]{id});
        if(cursor.getCount() != 0){
            doquery();
        }
        saveReminder();
        deleteReminder();
        backonClick();
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

    void clockbtn(){
        picktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditReminder  .this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etime.setText(hourOfDay + ":" + String.format("%02d", minute));
                    }
                } , hour, minute, false);
                timePickerDialog.show();
            }
        });
    }

    void doquery() {
        while (cursor.moveToNext()) {
            queId = cursor.getInt(0);
            queTitle = cursor.getString(1);
            queDesc = cursor.getString(2);
            String[] timeval = cursor.getString(3).split(":");
            int[] time = new int[timeval.length];

            for (int a = 0; a < timeval.length; a++) {
                time[a] = Integer.parseInt(timeval[a]);
            }
            min = time[1];
            hour = time[0];
            etime.setText(hour + ":" + String.format("%02d", min));
            title.setText(queTitle);
            desc.setText(queDesc);
        }
    }
    void saveReminder(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().trim().length() == 0){
                    toast = Toast.makeText(getApplicationContext(), "Provide reminder name!", Toast.LENGTH_SHORT);
                    toast.show();
                } else if(etime.getText().toString().trim().length() == 0){
                    toast = Toast.makeText(getApplicationContext(), "Provide reminder time!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    ContentValues val = new ContentValues();
                    val.put("name", title.getText().toString());
                    val.put("description", desc.getText().toString());
                    val.put("time", etime.getText().toString());
                    remDB.update("reminders", val, "id = ?", new String[]{id});
                    toast = Toast.makeText(getApplicationContext(), "Reminder Set!", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    void deleteReminder(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remDB.delete("reminders","id = ?", new String[]{id});
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}