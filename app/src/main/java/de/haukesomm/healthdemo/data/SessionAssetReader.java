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

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 26.08.18
 * <p>
 * This class parses the mockup session assets (JSON) in '/assets/sessions/ and converts them into
 * {@link Session} objects.<br>
 * It uses the {@link JsonReader} class provided by the Android SDK.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class SessionAssetReader {

    private static final String DIRECTORY = "sessions";

    private static final String EXTENSION = ".json";


    private final Context mContext;


    /**
     * Creates a new SessionAssetReader.
     *
     * @param context Application Context
     */
    SessionAssetReader(Context context) {
        mContext = context;
    }


    // TODO Store ID in JSON-Asset independently from the filename
    private int fileNameToId(String name) {
        String tmp = name
                .replace(DIRECTORY + "/", "")
                .replace(EXTENSION, "");
        return Integer.parseInt(tmp);
    }


    /**
     * This method reads all mockup Session assets and returns them as a List of Session objects.
     *
     * @return              List of Session objects
     * @throws IOException  In case the assets are not accessible or contain malformed JSON-data.
     */
    public List<Session> readMockupSessions() throws IOException {
        AssetManager assetManager = mContext.getAssets();
        String[] assets = assetManager.list(DIRECTORY);

        List<Session> sessions = new LinkedList<>();
        for (String asset : assets) {
            sessions.add(readSession(assetManager, DIRECTORY + "/" + asset));
        }
        return sessions;
    }

    private Session readSession(AssetManager manager, String name) throws IOException {
        int id = fileNameToId(name);
        Session session = new Session(id, SessionType.DEFAULT);

        JsonReader reader = new JsonReader(new InputStreamReader(manager.open(name), "UTF-8"));
        try {
            session.addAll(readMeasurementArray(reader));
        } catch (IOException e) {
            reader.close();
        }

        return session;
    }

    private List<Measurement> readMeasurementArray(JsonReader reader) throws IOException {
        List<Measurement> measurements = new LinkedList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            measurements.add(readMeasurement(reader));
        }
        reader.endArray();

        return measurements;
    }

    private Measurement readMeasurement(JsonReader reader) throws IOException {
        String timestamp = null;
        double latitide = 0d;
        double longitude = 0d;
        int heartrate = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "timestamp":
                    timestamp = reader.nextString();
                    break;
                case "latitude":
                    latitide = reader.nextDouble();
                    break;
                case "longitude":
                    longitude = reader.nextDouble();
                    break;
                case "heartrate":
                    heartrate = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Measurement(timestamp, latitide, longitude, heartrate);
    }
}
