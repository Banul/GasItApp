package com.example.user.proba3;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.user.proba3.dataModel.Gas;
import com.example.user.proba3.dataModel.GasStation;
import com.example.user.proba3.network.RequestCallback;
import com.example.user.proba3.network.UploadRequestTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jaroslaw on 24.05.2017.
 */

public class DialogUpdatePrice extends DialogFragment {

    private ImageView fuelImage;
    private Button btnOK;
    private ImageButton btnPlus, btnMinus;
    private EditText edtTxtPrice;


    private String fuelName;
    private GasStation gasStation;

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public void setGasStation(GasStation gasStation) {
        this.gasStation = gasStation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.update_price, container, false);

        edtTxtPrice.setText(Double.toString(getCurrentPrice()));

        updateFuelImage();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePrice();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double price = Double.parseDouble(edtTxtPrice.getText().toString());
                price += 0.01;
                edtTxtPrice.setText(Double.toString(price));
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double price = Double.parseDouble(edtTxtPrice.getText().toString());
                price -= 0.01;
                edtTxtPrice.setText(Double.toString(price));
            }
        });

        return v;
    }

    private void updateFuelImage() {
        switch (fuelName) {
            case "PB95":
                fuelImage.setImageResource(R.drawable.pb95);
                break;
            case "PB98":
                fuelImage.setImageResource(R.drawable.pb98);
                break;
            case "ON":
                fuelImage.setImageResource(R.drawable.on);
                break;
            case "LPG":
                fuelImage.setImageResource(R.drawable.gaz);
                break;
        }
    }

    public void updatePrice() {
        UploadRequestTask uploadRequestTask = new UploadRequestTask(new RequestCallback<String>() {
            @Override
            public void updateFromResponse(String response) {

            }
        });

        double price = Double.parseDouble(edtTxtPrice.getText().toString());
        String name = fuelName;
        String uuid = gasStation.getUuid();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", uuid);
            jsonObject.put("name", name);
            jsonObject.put("price", price);

            String url = getString(R.string.api_url);
            String method = "POST";
            String action = "approveNewPrice";
            String data = jsonObject.toString();

            uploadRequestTask.execute(url, method, action, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public double getCurrentPrice() {
        for(Gas gas : (ArrayList<Gas>)gasStation.zwrocListeGazow()) {
            if(gas.getName().equals(fuelName))
                return gas.getPrice();
        }
        return 0;
    }
}
