package com.example.user.proba3;

import org.json.JSONException;
import org.json.JSONObject;

        import org.json.JSONException;
        import org.json.JSONObject;

/**
 * Reprezentuje pozycję paliwa do sprzedania.
 */

public class Gas {
    private String name;
    private double price;

    public Gas(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    public static Gas parseJSON(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        double price = jsonObject.getDouble("price");

        return new Gas(name, price);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject toReturn = new JSONObject();

        toReturn.put("name", name);
        toReturn.put("price", price);

        return toReturn;
    }
}
