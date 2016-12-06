package com.example.qonita.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Qonita on 12/6/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Map.db";
    public static final String CONTACTS_TABLE_NAME = "marker";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_LAT = "latitude";
    public static final String CONTACTS_COLUMN_LNG = "longitude";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table marker " +
                "(id integer primary key, latitude text, longitude text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS marker");
        onCreate(db);
    }

    public boolean insertMarker(String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        db.insert("marker", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from marker where id="+id+"", null );
        return res;
    }

    public Integer getMaxId(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String MY_QUERY = "SELECT MAX(id) AS _id FROM marker";
        Cursor res =  db.rawQuery( MY_QUERY, null );
        res.moveToFirst();
        int id = res.getInt(0);
        return id;
    }

    public ArrayList<String> getAllMarkers()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from marker", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }

    public Integer deleteMarker (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("marker",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public boolean updateMarker (Integer id, String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);

        db.update("marker", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    /*public ArrayList<String> findData(String search){
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where name like '%"+search+"%' or phone like '%"+search+"%' or birth like '%"+search+"%' or address like '%"+search+"%' ", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }*/

}

