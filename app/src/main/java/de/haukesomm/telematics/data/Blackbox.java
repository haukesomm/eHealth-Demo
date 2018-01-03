/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
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
import android.support.annotation.Nullable;
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
 * <p>
 * This class stores the blackbox-data from the space detection vehicle.
 * <br>
 * Since there is no real vehicle the database populates itself with mockup-data provided in form of
 * csv-files on first access.
 *
 * @author Hauke Sommerfeld
 */
public class Blackbox extends SQLiteOpenHelper {

    /*
     * Name of the database file.
     */
    private static final String FILENAME = "blackbox.db";


    /*
     * Database version. This is important in case the database implementation changes.
     */
    private static final int VERSION = 1;



    /*
     * Name of the directory in which the mockup-data csv-files are stored.
     * It is located within the Android 'assets' resource-directory.
     */
    private static final String MOCKUP_DATA_DIR = "mockup-data";



    /**
     * Prefix of all database tables containing mockup-data.
     * This might become important in case the app switches to real-world-data at some point.
     */
    public static final String DATA_TABLE_PREFIX = "data_";


    /**
     * Format of the table name's date (e.g. 'data_[DATE]' -> 'data_20170603').
     */
    public static final String DATA_TABLE_DATE_FORMAT = "yyyyMMdd";


    /**
     * This String is used to identify the id of a specific data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_ID = "id";


    /**
     * This String is used to identify the timestamp of a specific data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_TIME = "time";


    /**
     * This String is used to identify the 'space lenght'-data in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_SPACE_LENGHT = "spaceLenght";


    /**
     * This String is used to identify the 'space depth'-data in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_SPACE_DEPTH = "spaceDepth";


    /**
     * This String is used to identify the location-data's latitude in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_LATITUDE = "latitude";


    /**
     * This String is used to identify the location-data's longitude in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_LONGITUDE = "longitude";


    /**
     * This String is used to identify the vehicle's direction in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_DIRECTION = "direction";


    /**
     * This String is used to identify the vehicle's speed in a data-set.
     * It is used for both the database's tables and JSON-objects which are used to transfer data.
     */
    public static final String DATA_SPEED = "speed";



    /**
     * Default speed unit.
     */
    public static final Data.Unit UNIT_SPEED = Data.SpeedUnit.MILES_PER_HOUR;



    /**
     * @param context The app's context to use for the database connection.
     */
    public Blackbox(@NonNull Context context) {
        super(context, FILENAME, null, VERSION);
        mContext = context;
    }



    /*
     * Reference to the app's context since SQLiteOpenHelper does not have a getContext()
     * method.
     */
    private final Context mContext;



    /**
     * This method is called when the Blackbox is accessed for the first time and populates it with
     * mockup-data from {@link #MOCKUP_DATA_DIR}.<br>
     * A new table is created for each csv-file. Each file contains data from one day.
     *
     * @param database The database which has been created.
     */
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
            String tableName = DATA_TABLE_PREFIX + assetName.split("\\.")[0];

            database.execSQL("CREATE TABLE " + tableName + " ("
                    + DATA_ID               + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + DATA_TIME             + " TEXT NOT NULL, "
                    + DATA_SPACE_LENGHT     + " REAL NOT NULL, "
                    + DATA_SPACE_DEPTH      + " REAL NOT NULL, "
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


    /* This method reads the data from a given csv-file asset and returns all data-sets as a list of
     * JSONObjects.
     * The timestamp gets extracted from the longer csv-timestamp. */
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
                json.put(DATA_TIME,             generateTime(csvColumns[0]));
                json.put(DATA_SPACE_LENGHT,     Double.valueOf(csvColumns[1]));
                json.put(DATA_SPACE_DEPTH,      Double.valueOf(csvColumns[2]));
                json.put(DATA_LATITUDE,         Double.valueOf(csvColumns[3]));
                json.put(DATA_LONGITUDE,        Double.valueOf(csvColumns[4]));
                json.put(DATA_DIRECTION,        Double.valueOf(csvColumns[5]));
                json.put(DATA_SPEED,            Double.valueOf(csvColumns[6]));

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


    /**
     * This method gets called when the Blackbox is beeing accessed for the first time after
     * {@link #VERSION} changed.
     *
     * @param db            The database which has been updated.
     * @param oldVersion    Old version of the database.
     * @param newVersion    New version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is nothing to upgrade
    }



    /*
     * Class variable holding the actual SQLite database when the Blackbox is open.
     */
    private SQLiteDatabase mDatabase;


    /**
     * Listener interface used as a callback if the Blackbox is opened asynchronously. This may be
     * the case on first access when the database needs to be created.
     *
     * @see #open()
     * @see #open(OpenListener)
     */
    public interface OpenListener {
        void onBlackboxOpened();
    }


    /**
     * This method opens a connection to the database and must be called before the Blackbox can be
     * accessed.<p>
     * A {@link OpenListener} can be passed as an argument so it will be called once the Blackbox is
     * open.<br>
     * In most cases this method should be called from an asynchronous context.
     *
     * @param listener  The listener to call
     */
    public void open(@Nullable OpenListener listener) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = getWritableDatabase();

            if (listener != null) {
                listener.onBlackboxOpened();
            }
        }
    }


    /**
     * This method opens a connection to the database and must be called before the Blackbox can be
     * accessed.
     */
    public void open() {
        open(null);
    }


    /**
     * This method closes the connection to the database and should be called once the Blackbox will
     * no longer be accessed.
     */
    @Override
    public void close() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }



    /**
     * This method returns a list of all tables of the Blackbox.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @return ArrayList of all tables
     */
    public ArrayList<String> getTables() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }


        ArrayList<String> tables = new ArrayList<>();

        Cursor cursor = mDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name LIKE '" + Blackbox.DATA_TABLE_PREFIX + "%' order by name", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                tables.add(cursor.getString( cursor.getColumnIndex("name")));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return tables;
    }



    /**
     * This method can be used to pass data in form of a {@link JSONObject} to the Blackbox.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @param table The table the data should be added to. Format: [PREFIX][DATE(yyyymmdd)]
     * @param data  The vehicle data in form of a JSONObject which should be added to the Blackbox.
     *
     * @see #DATA_TABLE_PREFIX
     */
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
            columns.put(DATA_SPACE_DEPTH,   data.getDouble(DATA_SPACE_DEPTH));
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



    /**
     * This method can be used to obtain a list of all data-sets in a specific table of the
     * BlackBox in form of {@link JSONObject}s.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @param index The indey of the table the data should be retrieved from.
     * @return      Returns a list of all data-sets in a table of in form of {@link JSONObject}s.
     */
    public ArrayList<JSONObject> getEntireTable(int index) {
        return getEntireTable(getTables().get(index));
    }


    /**
     * This method can be used to obtain a list of all data-sets in a specific table of the
     * BlackBox in form of {@link JSONObject}s.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @param table The table the data should be retrieved from. Format: [PREFIX][DATE(yyyymmdd)]
     * @return      Returns a list of all data-sets in a table of in form of {@link JSONObject}s.
     */
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
                json.put(DATA_SPACE_DEPTH,      cursor.getDouble(3));
                json.put(DATA_LATITUDE,         cursor.getDouble(4));
                json.put(DATA_LONGITUDE,        cursor.getDouble(5));
                json.put(DATA_DIRECTION,        cursor.getDouble(6));
                json.put(DATA_SPEED,            cursor.getDouble(7));

                jsonObjects.add(json);
            } catch (JSONException e) {
                Log.w("Blackbox", "Unable to get entry '" + cursor.getPosition() + "' from table '"
                        + table + "': " + e.getMessage());
            }
        }
        cursor.close();

        return jsonObjects;
    }



    /**
     * This method returns the number of tables of the Blackbox.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @return  Number of tables
     */
    public int getCount() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        return getTables().size();
    }
}
