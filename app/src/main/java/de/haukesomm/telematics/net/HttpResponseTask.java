/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.net;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created on 03.01.18
 * <p>
 * This class can send basic HTTP requests and retrieve the result in String form. Multiple URLs at
 * once are supported.
 *
 * @author Hauke Sommerfeld
 */
public class HttpResponseTask extends AsyncTask<URL, Void, ArrayList<String>> {

    /**
     * This interface is called when the HttpResponseTasks receives a HTTP-result.
     *
     * @see #setResponseListener(ResponseListener)
     */
    public interface ResponseListener {
        /**
         * This interface is called when the HttpResponseTasks receives a HTTP-result.
         *
         * @param responses List of responses
         */
        void onResponse(ArrayList<String> responses);
    }


    private ResponseListener mListener;


    /**
     * Use this method to specify a {@link ResponseListener} to call once the task receives a result.
     *
     * @param listener  Listener
     */
    public void setResponseListener(ResponseListener listener) {
        mListener = listener;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected ArrayList<String> doInBackground(URL... urls) {
        ArrayList<String> responses = new ArrayList<>();

        for (URL url : urls) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder response = new StringBuilder();
                String responseLine = "";
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine).append("\n");
                }
                responses.add(response.toString());

                reader.close();
                stream.close();
                connection.disconnect();
            } catch (IOException e) {
                Log.w("HttpResponseTask",
                        "Unable to get response (" + url.getPath() + "): " + e.getMessage());
            }
        }

        return responses;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostExecute(ArrayList<String> responses) {
        if (mListener != null) {
            mListener.onResponse(responses);
        } else {
            Log.w("HttpResponseTask", "No ResponseListener specified!");
        }
    }
}
