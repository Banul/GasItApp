package com.example.user.proba3;


import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public class GasStation {
    private String stationName;
    private double latitide;
    private double longitude;


    public GasStation(String stationName, double latitude, double longitude) {
        this.stationName = stationName;
        this.latitide = latitude;
        this.longitude = longitude;
    }

    public String getStationName() {
        return stationName;
    }

    public double getLatitide() {
        return latitide;
    }

    public double getLongitude() {
        return longitude;
    }

    public static GasStation parseJSON(JSONObject jsonObject) throws JSONException {
        String stationName = jsonObject.getString("station-name");

        JSONObject locationJSON = jsonObject.getJSONObject("location");
        double latitude = locationJSON.getDouble("latitude");
        double longitude = locationJSON.getDouble("longitude");
        return new GasStation(stationName, latitude, longitude);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();

        toReturn.put("station-name", stationName);
        JSONObject locationJSON = new JSONObject();
        locationJSON.put("latitude", latitide);
        locationJSON.put("longitude", longitude);
        toReturn.put("location", locationJSON);

        return toReturn;
    }

}
