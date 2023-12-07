package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton createReminder;
    String dbname = "ReminderDB";
    ScrollView scrollView;
    LinearLayout lv;
    SQLiteDatabase db;
    Cursor cursor;
    int id, hour, min, Alarmid;
    String title, desc, Alarmname, Alarmdesc;
    Boolean tableExist;
    Calendar calendar;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createReminder = findViewById(R.id.create_reminder);
        scrollView = findViewById(R.id.sc);
        lv = findViewById(R.id.lv);
        addOnclick();
        checkDb();

    }

    void checkDb() {
        db = openOrCreateDatabase(dbname, Context.MODE_PRIVATE, null);
        cursor = db.rawQuery("Select * from sqlite_master WHERE name='reminders' and type='table'", null);

        if (cursor.getCount() > 0) {
            cursor = db.rawQuery("Select * from reminders ORDER BY id DESC", null);
            if (cursor.getCount() != 0) {
                doQuery();
            }
        } else {
            TextView tv = new TextView(this );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 500, 10 , 20);
            tv.setText("Add reminder");
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(30, 20, 30, 20);
            tv.setTextSize(26);
            tv.setLayoutParams(params);
            //adding the textview to the scroll view
            lv.addView(tv);

        }
    }

    public void addOnclick(){
        createReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
                startActivity(intent);
            }
        });
    }
    void doQuery() {
        while(cursor.moveToNext()){
            id = cursor.getInt(0);
            title = cursor.getString(1);
            desc = cursor.getString(2);
            String[] timeval =cursor.getString(3).split(":");
            int[] time =new int[timeval.length];

            for (int a = 0; a < timeval.length; a++){
                time[a] = Integer.parseInt(timeval[a]);
            }
            min = time[1];
            TextView tv = new TextView(this );
            if(time[0] >= 12){
                if (time[0] > 12){
                    hour = time[0] - 12;
                }else{
                    hour = time[0];
                }
                tv.setText(title  + "-" + hour + ":" + String.format("%02d", min) + "PM"+"\n" +desc);
            } else {
                hour = time[0];
                tv.setText(title  + "-" + hour + ":" + String.format("%02d", min) + "AM"+"\n" +desc);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.HORIZONTAL
            );
            params.setMargins(10, 20, 10 , 20);
            tv.setId((int) id);
            tv.setPadding(30, 20, 30, 20);
            tv.setTextSize(26);
            tv.setLayoutParams(params);
            tv.setBackgroundColor(Color.parseColor("#E4D1FF")); //DFDFDF // DAC1FF
            //adding the textview to the scroll view
            lv.addView(tv);


            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EditReminder.class);
                    intent.putExtra("id",v.getId());
                    startActivity(intent);
                }
            });
            createNotif();
            setAlarm();
        }
    }
    private void setAlarm(){
        Alarmid = cursor.getInt(0);
        Alarmname = cursor.getString(1);
        Alarmdesc = cursor.getString(2);
        String[] Alarmtimeval = cursor.getString(3).split(":");
        int[] time = new int[Alarmtimeval.length];

        for (int j = 0; j < Alarmtimeval.length ; j++) {
            time[j] = Integer.parseInt(Alarmtimeval[j]);
        }
        hour = time[0];
        min = time[1];

        calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra("title", this.Alarmname);
        i.putExtra("desc", Alarmtimeval[0] + ":" + Alarmtimeval[1] + " " + this.Alarmdesc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Alarmid, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (currentTime > calendar.getTimeInMillis()) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
    private void createNotif() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notif";
            String descrip = "This Notif";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Notif", name, importance);
            channel.setDescription(descrip);

            NotificationManager notifman = getSystemService(NotificationManager.class);
            notifman.createNotificationChannel(channel);
        }
    }

}