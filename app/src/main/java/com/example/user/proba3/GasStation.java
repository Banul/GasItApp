package com.example.user.proba3;


import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public class GasStation {
    private String stationName;
    private double latitide;
    private double longitude;
    private ArrayList<Gas> listaPaliw = new ArrayList<Gas>(); //dodane ostatnio


    public GasStation(String stationName, double latitude, double longitude, Gas gas) {
        this.stationName = stationName;
        this.latitide = latitude;
        this.longitude = longitude;
        this.listaPaliw.add(gas);
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

    //TODO: dodałem w klasie listę paliw, trzeba tu coś zmienieć pewnie
    public static GasStation parseJSON(JSONObject jsonObject) throws JSONException {
        Gas paliwo = Gas.parseJSON(jsonObject);
        String stationName = jsonObject.getString("station-name");

        JSONObject locationJSON = jsonObject.getJSONObject("location");
        double latitude = locationJSON.getDouble("latitude");
        double longitude = locationJSON.getDouble("longitude");
        return new GasStation(stationName, latitude, longitude, paliwo);
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
