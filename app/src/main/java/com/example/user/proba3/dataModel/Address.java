package com.example.user.proba3.dataModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jaroslaw on 25.04.2017.
 */

public class Address {
    private String street;
    private int number;
    private String city;
    private String zipcode;
    private String state;
    private String country;

    public Address(){}

    public Address(String street, int number, String city, String zipcode, String state, String country) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.zipcode = zipcode;
        this.state = state;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public int getNumber() {
        return number;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getCountry() {
        return country;
    }
    public static Address parseJSON(JSONObject addressJSON) throws JSONException {
        String zipcode = addressJSON.getString("zip");
        String country = addressJSON.getString("country");
        String street = addressJSON.getString("addr1");
        int number = Integer.parseInt(addressJSON.getString("addr2"));
        String city = addressJSON.getString("city");
        String state = addressJSON.getString("state");
        return new Address(street, number, city, zipcode, state, country);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();

        toReturn.put("addr1", street);
        toReturn.put("addr2", number);
        toReturn.put("zip", zipcode);
        toReturn.put("country", country);
        toReturn.put("state", state);
        toReturn.put("city", city);

        return toReturn;
    }
}
