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
import java.util.List;

import de.haukesomm.telematics.R;

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
    public static final String MOCKUP_TABLE_PREFIX = "MOCKUP_";



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



    /*
     * This String is used to identify the 'cache'-table of the Blackbox.
     */
    private static final String CACHE_TABLE_NAME = "cache";


    /**
     * This String is used to identify the name of a table in a cached data-set.
     * It is used for both the database's cache table and JSON-objects which are used to transfer
     * data.
     */
    public static final String CACHE_TABLE = "data_table";


    /**
     * This String is used to identify the starting location in a cached data-set.
     * It is used for both the database's cache table and JSON-objects which are used to transfer
     * data.
     */
    public static final String CACHE_LOCATION_START = "start";


    /**
     * This String is used to identify the destination location in a cached data-set.
     * It is used for both the database's cache table and JSON-objects which are used to transfer
     * data.
     */
    public static final String CACHE_LOCATION_DESTINATION = "destination";


    /**
     * This String is used to identify the average speed in a cached data-set.
     * It is used for both the database's cache table and JSON-objects which are used to transfer
     * data.
     */
    public static final String CACHE_AVERAGE_SPEED = "averageSpeed";



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
            String tableName = MOCKUP_TABLE_PREFIX + assetName.split("\\.")[0];

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

        rebuildCache(database);
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

        return getTables(mDatabase);
    }


    private ArrayList<String> getTables(SQLiteDatabase database) {
        ArrayList<String> tables = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name LIKE 'MOCKUP_%' order by name", null);
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
     * @see #MOCKUP_TABLE_PREFIX
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
     * @param table The table the data should be retrieved from. Format: [PREFIX][DATE(yyyymmdd)]
     * @return      Returns a list of all data-sets in a table of in form of {@link JSONObject}s.
     */
    public ArrayList<JSONObject> getEntireTable(@NonNull String table) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        return getEntireTable(mDatabase, table);
    }


    private ArrayList<JSONObject> getEntireTable(SQLiteDatabase database, String table) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        Cursor cursor = database.query(table, null, null, null, null, null, null);
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
     * This method generates commonly requested data such as average speed or start- and destination-
     * positions from all tables of the Blackbox and stores them in a separate 'cache'-table.
     * This is useful in some cases only a preview of a table is needed and this way there is no
     * need to read and process the data of an entire table.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @see #getEntireTable(String)
     */
    @SuppressWarnings("unused")
    public void rebuildCache() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        rebuildCache(mDatabase);
    }


    private void rebuildCache(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + CACHE_TABLE_NAME);
        database.execSQL("CREATE TABLE " + CACHE_TABLE_NAME + " ("
                + CACHE_TABLE                   + " TEXT NOT NULL PRIMARY KEY, "
                + CACHE_LOCATION_START          + " TEXT NOT NULL, "
                + CACHE_LOCATION_DESTINATION    + " TEXT NOT NULL, "
                + CACHE_AVERAGE_SPEED           + " REAL NOT NULL);"
        );

        for (String table : getTables(database)) {
            List<JSONObject> data = getEntireTable(database, table);

            /* BEGIN:
             * The following code is not final. The locations can be generated from the given GPS
             * coordinates once the Google Maps SDK is implemented.
             * The average speed may be calculated in a separate method in the future. */
            double averageSpeed = 0.0d;
            for (JSONObject o : data) {
                try {
                    double speed = o.getDouble(Blackbox.DATA_SPEED);
                    averageSpeed += speed;
                } catch (JSONException e) {
                    // Warning
                }
            }
            averageSpeed /= data.size();


            ContentValues columns = new ContentValues();
            columns.put(CACHE_TABLE, table);
            columns.put(CACHE_LOCATION_START, mContext.getString(R.string.unknown));
            columns.put(CACHE_LOCATION_DESTINATION, mContext.getString(R.string.unknown));
            columns.put(CACHE_AVERAGE_SPEED, averageSpeed);
            /* END */

            database.insert(CACHE_TABLE_NAME, null, columns);
        }
    }



    /**
     * This method return the cached data from a specific 'cache'-table in form of a JSONObject.
     * <p>
     * An {@link IllegalStateException} will be thrown if the Blackbox is not open.
     *
     * @param table The table which data should be returned
     * @return      A JSONObject containing the cached values
     */
    public JSONObject getCachedValues(@NonNull String table) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            throw new IllegalStateException("Call open() before accessing the database!");
        }

        Cursor cursor = mDatabase.query(CACHE_TABLE_NAME, null, CACHE_TABLE + " = '" + table + "'", null, null, null, null);
        cursor.moveToFirst();

        JSONObject json = new JSONObject();
        try {
            json.put(CACHE_TABLE,                   cursor.getString(0));
            json.put(CACHE_LOCATION_START,          cursor.getString(1));
            json.put(CACHE_LOCATION_DESTINATION,    cursor.getString(2));
            json.put(CACHE_AVERAGE_SPEED,           cursor.getDouble(3));
        } catch (JSONException e) {
            Log.w("Blackbox", "Unable to get entry '" + cursor.getPosition() + "' from table '"
                    + table + "': " + e.getMessage());
        }
        cursor.close();

        return json;
    }
}
