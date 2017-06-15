package com.example.dayomi.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dayomi on 2017-05-14.
 */
public class MessageDBManager {
    static final String DB_MESSAGE = "MessageManagement.db";
    static final String TABLE_MESSAGE = "Management";
    static final int DB_VERSION = 1;

    Context mContext = null;
    private static MessageDBManager mDBManager = null;
    private SQLiteDatabase mdatabase = null;

    public MessageDBManager(Context context) {
        mContext = context;
        mdatabase = context.openOrCreateDatabase(DB_MESSAGE, Context.MODE_PRIVATE, null);

        mdatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_MESSAGE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " size INTEGER);");
    }

    public static MessageDBManager getInstance(Context context){
        if(mDBManager == null){
            mDBManager = new MessageDBManager(context);
        }
        return mDBManager;
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE;
        Cursor cursor = mdatabase.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void insert(int value){
        mdatabase.execSQL("INSERT INTO Management VALUES(null, '" + value + "');");
    }

    public void cleanDB(){
        mdatabase.execSQL("DELETE FROM Management");
    }

    public void update(int value){
        System.out.println("value is " + value);
        mdatabase.execSQL("UPDATE Management SET _id='0', size='1' WHERE _id='0';") ;

        String[] columns = new String[] {"_id", "size"};
        Cursor c = mDBManager.query(columns, null, null, null, null, null);
        if(c!=null){
            while(c.moveToNext()){
                int id = c.getInt(0);
                int size = c.getInt(1);
                System.out.println("id = " + id + " / size = "+ size);
            }
            c.close();
        }
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        return mdatabase.query("Management", columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int getSize(){
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE;
        Cursor cursor = mdatabase.rawQuery(countQuery, null);
        cursor.moveToLast();
        return cursor.getInt(1);
    }

    public int getRowSize(){
        String countQuery = "SELECT * FROM Management";
        Cursor c  = mdatabase.rawQuery(countQuery, null);
        c.moveToFirst();
        int total = c.getCount();
        c.close();
        return total;
    }
}

