/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Mukisa Kibirige.
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.opencsv.CSVReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created on 04.11.17
 *
 * @author Hauke Sommerfeld
 */
public class Blackbox extends SQLiteOpenHelper {

    private static final String FILENAME = "blackbox.db";


    private static final int VERSION = 1;



    private static final String MOCKUP_DATA_DIR = "mockup-data";


    private static final String MOCKUP_TABLE_PREFIX = "MOCKUP_";



    public static final String DATA_ID = "id";


    public static final String DATA_TIME = "time";


    public static final String DATA_SPACE_LENGHT = "spaceLenght";


    public static final String DATA_LATITUDE = "latitude";


    public static final String DATA_LONGITUDE = "longitude";


    public static final String DATA_DIRECTION = "direction";


    public static final String DATA_SPEED = "speed";



    public Blackbox(@NonNull Context context) {
        super(context, FILENAME, null, VERSION);
        mContext = context;
    }



    private final Context mContext;



    @Override
    public void onCreate(SQLiteDatabase database) {
        AssetManager assetManager = mContext.getAssets();

        String[] mockupAssets;
        try {
            mockupAssets = assetManager.list(MOCKUP_DATA_DIR);
        } catch (IOException io) {
            // This should never occur if the apk itself is not damaged!
            io.printStackTrace();
            return;
        }

        for (String assetName : mockupAssets) {
            String tableName = MOCKUP_TABLE_PREFIX + assetName.split("\\.")[0];

            database.execSQL("CREATE TABLE " + tableName + " ("
                    + DATA_ID               + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + DATA_TIME             + " TEXT NOT NULL, "
                    + DATA_SPACE_LENGHT     + " REAL NOT NULL, "
                    + DATA_LATITUDE         + " REAL NOT NULL, "
                    + DATA_LONGITUDE        + " REAL NOT NULL, "
                    + DATA_DIRECTION        + " REAL NOT NULL, "
                    + DATA_SPEED            + " REAL NOT NULL);"
            );

            for (JSONObject data : readMockupData(assetManager, assetName)) {
                add(database, tableName, data);
            }
        }
    }


    private ArrayList<JSONObject> readMockupData(AssetManager assetManager, String assetName) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        try {
            InputStream dataStream = assetManager.open(MOCKUP_DATA_DIR + "/" + assetName);
            InputStreamReader dataStreamReader = new InputStreamReader(dataStream);
            CSVReader csvReader = new CSVReader(dataStreamReader);

            String[] csvColumns;

            // Skip first line
            csvReader.readNext();

            while ((csvColumns = csvReader.readNext()) != null) {
                JSONObject json = new JSONObject();
                json.put(DATA_TIME,           generateTime(csvColumns[0]));
                json.put(DATA_SPACE_LENGHT,   Double.valueOf(csvColumns[1]));
                json.put(DATA_LATITUDE,       Double.valueOf(csvColumns[2]));
                json.put(DATA_LONGITUDE,      Double.valueOf(csvColumns[3]));
                json.put(DATA_DIRECTION,      Double.valueOf(csvColumns[4]));
                json.put(DATA_SPEED,          Double.valueOf(csvColumns[5]));

                jsonObjects.add(json);
            }
        } catch (IOException i) {
            Log.w("Blackbox", "Unable to parse  asset " + assetName + ":\n" + i.getClass() + " ("
                    + i.getMessage() + ")");
        } catch (ArrayIndexOutOfBoundsException | JSONException e) {
            Log.w("Blackbox", "Asset " + assetName + " contains invalid data:\n" + e.getClass()
                    + " (" + e.getMessage() + ")");
        }

        return jsonObjects;
    }


    private String generateTime(String timestamp) {
        String[] timeParts = timestamp.split("T");
        return timeParts[timeParts.length - 1].split("\\+")[0];
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is nothing to upgrade
    }



    private SQLiteDatabase mDatabase;


    public void open() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = getWritableDatabase();
        }
    }


    @Override
    public void close() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }



    /* This method is never used and only here for demo purposes.
     * Data would be added to the blackbox trough this method if there was no mockup data. */
    @SuppressWarnings("unused")
    public void add(@NonNull String table, @NonNull JSONObject data) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        add(mDatabase, table, data);
    }


    private void add(SQLiteDatabase database, String table, JSONObject data) {
        try {
            ContentValues columns = new ContentValues();
            columns.put(DATA_TIME,          data.getString(DATA_TIME));
            columns.put(DATA_SPACE_LENGHT,  data.getDouble(DATA_SPACE_LENGHT));
            columns.put(DATA_LATITUDE,      data.getDouble(DATA_LATITUDE));
            columns.put(DATA_LONGITUDE,     data.getDouble(DATA_LONGITUDE));
            columns.put(DATA_DIRECTION,     data.getDouble(DATA_DIRECTION));
            columns.put(DATA_SPEED,         data.getDouble(DATA_SPEED));

            database.insert(table, null, columns);
        } catch (JSONException e) {
            Log.e("Blackbox", "Cannot write to table '" + table + "': " + "Malformed JSON ("
                    + e.getMessage() + ")");
        }
    }



    public ArrayList<JSONObject> getEntireTable(@NonNull String table) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        Cursor cursor = mDatabase.query(table, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                JSONObject json = new JSONObject();
                json.put(DATA_ID,               cursor.getInt(0));
                json.put(DATA_TIME,             cursor.getString(1));
                json.put(DATA_SPACE_LENGHT,     cursor.getDouble(2));
                json.put(DATA_LATITUDE,         cursor.getDouble(3));
                json.put(DATA_LONGITUDE,        cursor.getDouble(4));
                json.put(DATA_DIRECTION,        cursor.getDouble(5));
                json.put(DATA_SPEED,            cursor.getDouble(6));

                jsonObjects.add(json);
            } catch (JSONException e) {
                Log.w("Blackbox", "Unable to get entry '" + cursor.getPosition() + "' from table '"
                        + table + "': " + e.getMessage());
            }
        }
        cursor.close();

        return jsonObjects;
    }
}
