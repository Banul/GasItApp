package com.example.user.proba3;

import android.app.DialogFragment;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.proba3.dataModel.Gas;
import com.example.user.proba3.dataModel.GasStation;
import com.example.user.proba3.network.RequestCallback;
import com.example.user.proba3.network.UploadRequestTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
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
        View view = inflater.inflate(R.layout.dialog_update_price, container, false);

        fuelImage = (ImageView) view.findViewById(R.id.updateFuel);
        btnOK = (Button) view.findViewById(R.id.updateOk);
        btnPlus = (ImageButton) view.findViewById(R.id.updatePlus);
        btnMinus = (ImageButton) view.findViewById(R.id.updateMinus);
        edtTxtPrice = (EditText) view.findViewById(R.id.updatePrice);

        edtTxtPrice.setText(Double.toString(getCurrentPrice()));

        updateFuelImage();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePrice();
                dismiss();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DecimalFormat df = new DecimalFormat("#.00");


                String text = String.valueOf(edtTxtPrice.getText());
                Double wartD = Double.parseDouble(text);
                String wartoscS;
                wartoscS = df.format(wartD);
                wartoscS = wartoscS.replace(",", ".");
                Double wartoscD = Double.parseDouble(wartoscS);
                String wartoscDS = String.valueOf(wartoscD);
                Double mniejszeWartD = wartoscD - 0.01;


                String toWpisz = df.format(mniejszeWartD);
                toWpisz = toWpisz.replace(",", ".");
                edtTxtPrice.setText(toWpisz, TextView.BufferType.EDITABLE);
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DecimalFormat df = new DecimalFormat("#.00");


                String text = String.valueOf(edtTxtPrice.getText());
                Double wartD = Double.parseDouble(text);
                String wartoscS;
                wartoscS = df.format(wartD);
                wartoscS = wartoscS.replace(",", ".");
                Double wartoscD = Double.parseDouble(wartoscS);
                String wartoscDS = String.valueOf(wartoscD);
                Double mniejszeWartD = wartoscD + 0.01;


                String toWpisz = df.format(mniejszeWartD);
                toWpisz = toWpisz.replace(",", ".");
                edtTxtPrice.setText(toWpisz, TextView.BufferType.EDITABLE);

            }
        });

        return view;
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
                String kodOdpowiedzi = response;
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

        ArrayList<Gas> gases = gasStation.zwrocListeGazow();
        if(gases != null) {
            for (Gas gas : gases) {
                if (gas.getName().equals(fuelName))
                    return gas.getPrice();
            }
        }
        return 0;
    }
}
