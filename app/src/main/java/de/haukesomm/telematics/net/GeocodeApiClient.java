/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.net;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created on 03.01.18
 * <p>
 * This class functions as a client for the Google Maps Geocoding API.
 *
 * @author Hauke Sommerfeld
 */
public class GeocodeApiClient {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/";


    private static final String API_KEY = "AIzaSyCwlaYg4uyd3jYQB97jS9A_dk3aldjisd4";



    private static final String JSON_RESULTS = "results";


    private static final String JSON_FORMATTED_ADDRESS = "formatted_address";


    private static final String JSON_ADDRESS_COMPONENTS = "address_components";


    private static final String JSON_ADDRESS_COMPONENTS_TYPES = "types";


    private static final String JSON_ADDRESS_COMPONENTS_TYPES_LOCALITY = "locality";


    private static final String JSON_ADDRESS_COMPONENTS_SHORT = "short_name";


    private static final String JSON_ADDRESS_COMPONENTS_LONG = "long_name";



    /**
     * This listener class is used to deliver responses from the API in JSON form as the HTTP
     * requests are sent asynchronously.
     */
    public static abstract class ResponseListener {

        void onRawResponse(String response) {
            try {
                onResponse(new JSONObject(response));
            } catch (JSONException e) {
                Log.w("GeocodeApiClient", "Unable to convert response to JSON: " + e.getMessage());
            }
        }



        /**
         * This method is called when the response was successfully received.
         *
         * @param response  Response in form of a {@link JSONObject}
         */
        public abstract void onResponse(JSONObject response);
    }



    /**
     * Use this method to request an address based on its coordinates from the API.<br>
     * An asynchronous HTTP-request will be sent so the use of a listener is required.
     *
     * @param latitude  Latitude of the address
     * @param longitude Longitude of the address
     * @param listener  {@link ResponseListener}
     *
     * @see HttpResponseTask
     */
    public void requestAddress(double latitude, double longitude,
            @NonNull final ResponseListener listener) {

        try {
            URL url = new URL(API_URL + "json?latlng=" + latitude + "," + longitude + "&key=" + API_KEY);

            HttpResponseTask httpTask = new HttpResponseTask();
            httpTask.setResponseListener(new HttpResponseTask.ResponseListener() {
                @Override
                public void onResponse(ArrayList<String> responses) {
                    listener.onRawResponse(responses.get(0));
                }
            });
            httpTask.execute(url);
        } catch (IOException e) {
            Log.w("GeocodeApiClient", "Unable to request address: " + e.getMessage());
        }
    }



    /**
     * Use this method to get the formatted address from a repsonse.
     * <p>
     * A {@link JSONException} will be thrown if an invalid response is passed to this method.
     *
     * @param response          Response JSONObject
     * @return                  Formatted address in String from
     * @throws JSONException    In case of an invalid response
     */
    public String decodeFormattedAddress(@NonNull JSONObject response) throws JSONException {
        return response
                .getJSONArray(JSON_RESULTS)
                .getJSONObject(0)
                .getString(JSON_FORMATTED_ADDRESS);
    }


    /**
     * Use this method to get the city (locality) from a response.
     * <p>
     * A {@link JSONException} will be thrown if an invalid response is passed to this method.
     *
     * @param response          Response JSONObject
     * @return                  City in String from
     * @throws JSONException    In case of an invalid response
     */
    public String decodeCity(@NonNull JSONObject response) throws JSONException {
        JSONArray addressComponents = response
                .getJSONArray(JSON_RESULTS)
                .getJSONObject(0)
                .getJSONArray(JSON_ADDRESS_COMPONENTS);

        for (int i = 0; i < addressComponents.length(); i++) {
            JSONObject component = addressComponents.getJSONObject(i);

            JSONArray types = component.getJSONArray(JSON_ADDRESS_COMPONENTS_TYPES);
            if (types.getString(0).equals(JSON_ADDRESS_COMPONENTS_TYPES_LOCALITY)) {

                return component.getString(JSON_ADDRESS_COMPONENTS_LONG);
            }
        }

        return null;
    }
}
