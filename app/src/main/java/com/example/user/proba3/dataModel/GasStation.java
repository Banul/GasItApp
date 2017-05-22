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
    ArrayList<Gas> gasesArray;

    public GasStation(String owner, double latitude, double longitude, ArrayList<Gas> gases)
    {
        this.owner = owner;
        this.latitide = latitude;
        this.longitude = longitude;
        this.gasesArray = gases;
    }


    public GasStation(String uuid, String owner, double latitude, double longitude, ArrayList<Gas> gasesArray) {
        this.uuid = uuid;
        this.owner = owner;
        this.latitide = latitude;
        this.longitude = longitude;
        this.gasesArray = gasesArray;
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

    public String getUuid() {return uuid;}

    public ArrayList zwrocListeGazow ()
    {
        return gasesArray;
    }



    public static GasStation parseJSON(JSONObject jsonObject) throws JSONException {

        String ownerName = jsonObject.getString("owner");
        String uuid = jsonObject.getString("uuid");
        ArrayList<Gas> gases = new ArrayList<>();
        JSONArray gasesArray = jsonObject.getJSONArray("gases");
        for(int i = 0; i<gasesArray.length(); i++) {
            gases.add(Gas.parseJSON(gasesArray.getJSONObject(i)));
        }
        JSONObject locationJSON = jsonObject.getJSONObject("location");
        double latitude = locationJSON.getDouble("latitude");
        double longitude = locationJSON.getDouble("longitude");
        return new GasStation(uuid, ownerName, latitude, longitude, gases);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();

        JSONObject location = new JSONObject();
        location.put("latitude", latitide);
        location.put("longitude", longitude);
        toReturn.put("location", location);


        JSONArray gases = new JSONArray();

        for (Gas gas:gasesArray
                ) {
            gases.put(gas.toJSON());
        }
        toReturn.put("gases",gases);

        toReturn.put("owner",owner);

        return toReturn;
    }

}
