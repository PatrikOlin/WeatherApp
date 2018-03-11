package se.iftac.patrikolin.weatherapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vegeterran on 2018-03-06.
 */

public class HttpHandler {

    String LOG_TAG ="HttpHandler";

    public InputStream callServer(String remoteURL) {
        InputStream inStm = null;

        try {
            URL url = new URL(remoteURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            int response = conn.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                inStm = conn.getInputStream();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in CallServer", e);
        }
        return inStm;
    }

    public String streamToString(InputStream stream) {
        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) !=null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in streamToString", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in streamToString", e);
        }
        return response.toString();
    }

}
