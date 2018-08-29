/*
 * This file is part of the "eHealth-Demo" project, formerly known as
 * "Telematics App Mockup".
 * Copyright 2017-2018, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *
 * Licensed under the MIT license.
 *
 * For more information and/or a copy of the license visit the following
 * GitHub repository: https://github.com/haukesomm/eHealth-Demo
 */

package de.haukesomm.healthdemo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 23.08.18
 * <p>
 * This class manages the SQlite database containing all recorded fitness {@link Session}s.
 * It includes methods to add, remove or list Sessions.<br>
 * By default it is populated with mockup data provided by the {@link SessionAssetReader} class.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class SessionDatabase extends SQLiteOpenHelper implements AutoCloseable {

    private static final String FILE = "sessions.db";

    private static final int VERSION = 1;


    private static final String TABLE_SESSIONS = "sessions";

    private static final String TABLE_SESSIONS_ID = "session_id";

    private static final String TABLE_SESSIONS_TYPE = "session_type";


    private static final String ID = "id";

    private static final String TIMESTAMP = "timestamp";

    private static final String LATITUDE = "latitude";

    private static final String LONGITUDE = "longitude";

    private static final String HEARTRATE = "heartrate";


    private final Context mContext;

    private SQLiteDatabase mDatabase;


    /**
     * Creates a new SessionDatabase object which can be used to manipulate the database.
     *
     * @param context   Application context.
     */
    public SessionDatabase(Context context) {
        super(context, FILE, null, VERSION);
        mContext = context;
        open();
    }


    // No Javadoc
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SESSIONS + "("
                + TABLE_SESSIONS_ID + " INTEGER NOT NULL PRIMARY KEY,"
                + TABLE_SESSIONS_TYPE + " INTEGER NOT NULL);"
        );

        initMockupData(db);
    }

    // No Javadoc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }


    /**
     * Call this method to manually open the database connection. By default this is not required
     * since the constructor already opens a connection.
     *
     * @see #close()
     */
    @SuppressWarnings("WeakerAccess")
    public void open() {
        mDatabase = getWritableDatabase();
    }

    /**
     * This method closes the existing database connection. All method reading from / modifying the
     * database will stop working until you manually re-establish the connection by invoking
     * {@link #open()} ()}.<br>
     * Call this method when you are finished working with the database or use a try-with-resources
     * block!
     */
    @Override
    public void close() {
        if (mDatabase == null) return;
        mDatabase.close();
        mDatabase = null;
    }

    private void validateConnection() throws IllegalStateException {
        if (mDatabase == null) {
            throw new IllegalStateException(
                    "SessionDatabase has no active connection. Call open() first!");
        }
    }


    private void initMockupData(SQLiteDatabase db) {
        SessionAssetReader reader = new SessionAssetReader(mContext);
        try {
            for (Session session : reader.readMockupSessions()) {
                add(db, session);
            }
        } catch (IOException e) {
            Log.e("SessionDatabase", "Unable to read mockup data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static String convertToTableName(int id) {
        return "data_" + String.valueOf(id);
    }


    /**
     * Returns a List of all available sessions in form of {@link SessionDescription}s.
     *
     * @return                          List of SessionDescriptions
     * @throws IllegalStateException    If there is no active database connection
     */
    public List<SessionDescription> listSessions() throws IllegalStateException {
        validateConnection();


        List<SessionDescription> sessions = new LinkedList<>();

        try (Cursor sessionCursor = mDatabase.query(TABLE_SESSIONS, null, null, null, null, null, null)) {
            if (sessionCursor.moveToFirst()) {
                while (!sessionCursor.isAfterLast()) {
                    sessions.add(new SessionDescription(
                            sessionCursor.getInt(0),
                            SessionType.get(sessionCursor.getString(1))));
                    sessionCursor.moveToNext();
                }
            }
        }

        return sessions;
    }

    /**
     * This method adds a Session to the database.
     *
     * @param session                   Session object
     * @throws IllegalStateException    If there is no active database connection
     */
    public void add(Session session) throws IllegalStateException{
        validateConnection();
        add(mDatabase, session);
    }

    private void add(SQLiteDatabase db, Session session) {
        final String table = convertToTableName(session.id);

        // Create the actual data-table
        db.execSQL("CREATE TABLE " + table + " ("
                + ID        + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + TIMESTAMP + " TEXT NOT NULL, "
                + LATITUDE  + " REAL NOT NULL, "
                + LONGITUDE + " REAL NOT NULL, "
                + HEARTRATE + " INTEGER NOT NULL);");

        // Insert measurement data into the table
        for (Measurement measurement : session.getMeasurements()) {
            ContentValues values = new ContentValues();
            values.put(TIMESTAMP, measurement.timestamp);
            values.put(LATITUDE, measurement.latitude);
            values.put(LONGITUDE, measurement.longitude);
            values.put(HEARTRATE, measurement.heartrate);
            db.insert(table, null, values);
        }

        // Create entry in the session-table (old id will be overridden!)
        ContentValues sessionInfo = new ContentValues();
        sessionInfo.put(TABLE_SESSIONS_ID, session.id);
        sessionInfo.put(TABLE_SESSIONS_TYPE, session.type.alias);
        db.insert(TABLE_SESSIONS, null, sessionInfo);
    }

    /**
     * This method returns a specific Session that matches the specified ID.
     *
     * @param id                        ID of the Session to return
     * @return                          Session object
     * @throws IllegalStateException    If there is no active database connection
     */
    public Session get(int id) throws IllegalStateException {
        validateConnection();


        try (Cursor sessionCursor = mDatabase.query(TABLE_SESSIONS, null,
                TABLE_SESSIONS_ID + " = " + id, null, null, null, null)) {

            sessionCursor.moveToFirst();

            Session session = new Session(id, SessionType.get(sessionCursor.getString(1)));

            try (Cursor measurementCursor = mDatabase.query(convertToTableName(id), null, null, null,
                    null, null, null)) {

                if (measurementCursor.moveToFirst()) {
                    while (!measurementCursor.isAfterLast()) {
                        session.add(new Measurement(
                                // 0 is reserved for the ID!
                                measurementCursor.getString(1),
                                measurementCursor.getDouble(2),
                                measurementCursor.getDouble(3),
                                measurementCursor.getInt(4)
                        ));
                        measurementCursor.moveToNext();
                    }
                }
            }

            return session;
        }
    }
}
