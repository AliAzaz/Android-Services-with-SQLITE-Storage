package com.example.openm.servicesintro.core;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by openm on 29-Dec-17.
 */

public class DataContract {
    int id,userId;
    String title, body;

    public DataContract() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public DataContract Hydrate(Cursor c) {
        this.userId = c.getInt(c.getColumnIndex(singleData.COLUMN_USER_ID));
        this.id = c.getInt(c.getColumnIndex(singleData.COLUMN_ID));
        this.title = c.getString(c.getColumnIndex(singleData.ROW_TITLE));
        this.body = c.getString(c.getColumnIndex(singleData.ROW_BODY));

        return this;
    }

    public DataContract ConvertJSONtoObject(JSONObject jsonObject) throws JSONException {
        this.userId = jsonObject.getInt(singleData.COLUMN_USER_ID);
        this.id = jsonObject.getInt(singleData.COLUMN_ID);
        this.title = jsonObject.getString(singleData.ROW_TITLE);
        this.body = jsonObject.getString(singleData.ROW_BODY);

        return this;

    }

    public static abstract class singleData{

        public static final String TABLE_NAME = "data";
        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_ID = "id";
        public static final String ROW_TITLE = "title";
        public static final String ROW_BODY = "body";
    }
}
