package com.example.user.proba3.dataModel;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public class GasStation {
    private String stationName;
    private String ownerName;
    // Lokalizacja
    private double latitide;
    private double longitude;
    private Address address;
    // Lista paliw oferowanych na stacji
    ArrayList<Gas> gases;

    public GasStation()
    {

    }

    public GasStation(String stationName, String ownerName, double latitude, double longitude, Address address, ArrayList<Gas> gases) {
        this.stationName = stationName;
        this.ownerName = ownerName;
        this.latitide = latitude;
        this.longitude = longitude;
        this.address = address;
        this.gases = gases;
    }

    public String getStationName() {
        return stationName;
    }

    public String getOwnerName() {
        return ownerName;
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
        String stationName = entity.getJSONObject(0).getString("name");
        String ownerName = entity.getJSONObject(0).getString("owner");
        ArrayList<Gas> gases = new ArrayList<>();
        JSONArray gasesArray = entity.getJSONObject(0).getJSONArray("gases");
        for(int i = 0; i<gasesArray.length(); i++) {
            gases.add(Gas.parseJSON(gasesArray.getJSONObject(i)));
        }
        JSONObject locationJSON = entity.getJSONObject(0).getJSONObject("location");
        double latitude = locationJSON.getDouble("latitude");
        double longitude = locationJSON.getDouble("longitude");
      //  JSONObject addressJSON = jsonObject.getJSONObject("adr");                         //todo zastanowić się nad strukturą, co powinna posiadać klasa GasStation
       // Address address = Address.parseJSON(addressJSON);
        Address address = new Address();
        return new GasStation(stationName, ownerName, latitude, longitude, address, gases);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();

        toReturn.put("station-name", stationName);
        toReturn.put("name", generateAPIKeyName());

        JSONObject addressJSON = address.toJSON();
        toReturn.put("adr", addressJSON);

        JSONObject locationJSON = new JSONObject();
        locationJSON.put("latitude", latitide);
        locationJSON.put("longitude", longitude);
        toReturn.put("location", locationJSON);

        return toReturn;
    }
    private String generateAPIKeyName() {
        return this.ownerName + " " + this.address.getCity() + " " + this.address.getStreet() + " " + this.address.getNumber();
    }
}
