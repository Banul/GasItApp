package com.example.user.proba3.dataModel;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public class GasStation {
    private String uuid;
    private String owner;
    // Lokalizacja
    private double latitide;
    private double longitude;

    // Lista paliw oferowanych na stacji
    ArrayList<Gas> gases;

    public GasStation()
    {

    }

    public GasStation(String uuid, String owner, double latitude, double longitude, ArrayList<Gas> gases) {
        this.uuid = uuid;
        this.owner = owner;
        this.latitide = latitude;
        this.longitude = longitude;
        this.gases = gases;
    }

    public String getOwner() {
        return owner;
    }

    public double getLatitiude() {
        return latitide;
    }

    public double getLongitude() {
        return longitude;
    }
//    public void addGas(Gas gas) {
//        this.gases.add(gas);
//    }

    public static GasStation parseJSON(JSONObject jsonObject) throws JSONException {
        JSONArray entity = jsonObject.getJSONArray("entities");
        String ownerName = entity.getJSONObject(0).getString("owner");
        String uuid = entity.getJSONObject(0).getString("uuid");
        ArrayList<Gas> gases = new ArrayList<>();
        JSONArray gasesArray = entity.getJSONObject(0).getJSONArray("gases");
        for(int i = 0; i<gasesArray.length(); i++) {
            gases.add(Gas.parseJSON(gasesArray.getJSONObject(i)));
        }
        JSONObject locationJSON = entity.getJSONObject(0).getJSONObject("location");
        double latitude = locationJSON.getDouble("latitude");
        double longitude = locationJSON.getDouble("longitude");
        return new GasStation(uuid, ownerName, latitude, longitude, gases);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();
        JSONObject locationJSON = new JSONObject();
        locationJSON.put("latitude", latitide);
        locationJSON.put("longitude", longitude);
        toReturn.put("location", locationJSON);

        return toReturn;
    }

}
