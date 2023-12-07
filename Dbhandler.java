package com.example.notification;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbhandler extends SQLiteOpenHelper {
    public static final String dbname = "ReminderDB";
    public static final int dbver = 1;

    public Dbhandler (Context context) { super(context,dbname,null,dbver);}

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS reminders (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, time TEXT);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion){
        //this method checks if the table already exist
        db.execSQL("DROP TABLE IF EXISTS employee");
        onCreate(db);
    }
}