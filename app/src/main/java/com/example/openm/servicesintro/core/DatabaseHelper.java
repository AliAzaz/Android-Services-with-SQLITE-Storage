package com.example.openm.servicesintro.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.openm.servicesintro.core.DataContract.singleData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by openm on 29-Dec-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String SQL_CREATE_DATA = "CREATE TABLE " + singleData.TABLE_NAME + "("
            + singleData.COLUMN_ID + " TEXT,"
            + singleData.COLUMN_USER_ID + " TEXT,"
            + singleData.ROW_TITLE + " TEXT,"
            + singleData.ROW_BODY + " TEXT );";

    private static final String SQL_DELETE_DATA =
            "DROP TABLE IF EXISTS " + singleData.TABLE_NAME;

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATA);

        onCreate(sqLiteDatabase);
    }

    public Collection<DataContract> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                singleData.COLUMN_ID,
                singleData.COLUMN_USER_ID,
                singleData.ROW_TITLE,
                singleData.ROW_BODY
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy =
                singleData.COLUMN_ID + " ASC";

        Collection<DataContract> allDT = new ArrayList<>();
        try {
            c = db.query(
                    singleData.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                DataContract dc = new DataContract();
                allDT.add(dc.Hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allDT;
    }

    public Long insertData(DataContract dc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(singleData.COLUMN_ID, dc.getId());
        values.put(singleData.COLUMN_USER_ID, dc.getUserId());
        values.put(singleData.ROW_TITLE, dc.getTitle());
        values.put(singleData.ROW_BODY, dc.getBody());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                singleData.TABLE_NAME,
                null,
                values);
        db.close();
        return newRowId;
    }

    public void insertData(JSONArray json) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(singleData.TABLE_NAME, null, null);

        try {
            JSONArray jsonArray = json;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectCC = jsonArray.getJSONObject(i);

                DataContract dc = new DataContract().ConvertJSONtoObject(jsonObjectCC);

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(singleData.COLUMN_ID, dc.getId());
                values.put(singleData.COLUMN_USER_ID, dc.getUserId());
                values.put(singleData.ROW_TITLE, dc.getTitle());
                values.put(singleData.ROW_BODY, dc.getBody());

                // Insert the new row, returning the primary key value of the new row
                db.insert(
                        singleData.TABLE_NAME,
                        null,
                        values);

            }


        } catch (Exception e) {
        } finally {
            db.close();
        }
    }
}
