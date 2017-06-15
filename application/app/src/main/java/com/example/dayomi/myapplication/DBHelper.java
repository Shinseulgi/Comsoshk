package com.example.dayomi.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dayomi on 2017-04-23.
 */
public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS MESSAGEMANAGEMENT ( " +
                "size INTEGER );");
    }

    public void updateSize(int size){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE MESSAGEMANAGEMENT SET MESSAGEMANAGEMENT.size='" + size + "' " + "WHERE MESSAGEMANAGEMENT.entry='0';");
    }

    public int getSize(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM MESSAGEMANAGEMENT", null);
        int size = c.getInt(0);
        return size;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
