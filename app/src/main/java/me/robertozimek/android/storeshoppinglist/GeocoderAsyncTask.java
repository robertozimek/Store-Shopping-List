package me.robertozimek.android.storeshoppinglist;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * GeocoderAsyncTask
 *  retrieves latitude and longitude of an address
 * Created by robertozimek on 7/15/16.
 */
public class GeocoderAsyncTask extends AsyncTask<String, Void, LatLng> {
    @Override
    protected LatLng doInBackground(String... params) {
        String street = params[0];
        String city = params[1];
        String state = params[2];

        return retrieveCoordinatesFromAddress(street, city, state);
    }

    public static LatLng retrieveCoordinatesFromAddress(String street, String city, String state) {
        JSONArray jsonArray = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        // URL encode address
        try {
            street = URLEncoder.encode(street, "UTF-8");
            city = URLEncoder.encode(city, "UTF-8");
            state = URLEncoder.encode(state, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // retrieve jsonarray from nominatim openstreetmap
        try {
            URL url = new URL("http://nominatim.openstreetmap.org/search?street=" + street + "&city=" + city + "&state=" + state + "&format=json");

            connection = (HttpURLConnection) url.openConnection();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }

            // convert stringbuffer to jsonarray
            try {
                jsonArray = new JSONArray(stringBuffer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getLatLng(jsonArray);
    }

    // obtain LatLng object with coordinate in jsonarray
    private static LatLng getLatLng(JSONArray jsonArray) {
        LatLng latLng = null;
        try {
            Double lng = jsonArray.getJSONObject(0).getDouble("lon");
            Double lat = jsonArray.getJSONObject(0).getDouble("lat");
            latLng = new LatLng(lat, lng);
            return latLng;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLng;
    }
}

