package com.example.user.proba3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.proba3.R.id.przyciskPlus;
import static com.example.user.proba3.R.id.spinner;
import static com.example.user.proba3.R.id.tekst;

/**
 * Created by User on 2017-04-24.
 */

public class DialogDodajStacjeMarker extends DialogFragment implements AdapterView.OnItemSelectedListener,RequestCallback<String> {

    private LatLng polozenie;
    private Spinner spinner;
    private Spinner spinner2;
    private Activity activity ;
    private ImageButton przyciskMinus;
    private ImageButton przyciskPlus;
    private EditText tekst;
    DownloadRequestTask download = new DownloadRequestTask(this);
    CustomAdapter adapter;
    CustomAdapterStacje adapterStacje;
    public ArrayList<ItemData> CustomListViewValuesArr = new ArrayList<ItemData>();
    public ArrayList<ItemData> ListaNaStacje = new ArrayList<ItemData>();

    public DialogDodajStacjeMarker()
    {

    }

    public DialogDodajStacjeMarker(LatLng ltlng)
    {
        this.polozenie = ltlng;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dodaj_stacje_marker, null);
        activity = this.getActivity();
        spinner2 = (Spinner) view.findViewById(R.id.spinner2); // dla paliw
        spinner = (Spinner) view.findViewById(R.id.spinner);// dla stacji
        przyciskMinus = (ImageButton) view.findViewById(R.id.przyciskMinus);
        przyciskPlus= (ImageButton) view.findViewById(R.id.przyciskPlus);
        tekst = (EditText) view.findViewById(R.id.tekst);

        przyciskMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df = new DecimalFormat("#.00");


                String text = String.valueOf(tekst.getText());
                Log.d("mesydz",text);
                Double wartD = Double.parseDouble(text);
                String wartoscS;
                wartoscS = df.format(wartD);
                wartoscS=wartoscS.replace(",",".");
                Log.d("mesydz1",wartoscS);
                Double wartoscD = Double.parseDouble(wartoscS);
                String wartoscDS = String.valueOf(wartoscD);
                Log.d("mesydz2",wartoscDS);
                Double mniejszeWartD = wartoscD-0.01;


                String toWpisz = df.format(mniejszeWartD);
                toWpisz = toWpisz.replace(",", ".");
                tekst.setText(toWpisz, TextView.BufferType.EDITABLE);

            }
        });

        przyciskPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df = new DecimalFormat("#.00");


                String text = String.valueOf(tekst.getText());
                Log.d("mesydz",text);
                Double wartD = Double.parseDouble(text);
                String wartoscS;
                wartoscS = df.format(wartD);
                wartoscS=wartoscS.replace(",",".");
                Log.d("mesydz1",wartoscS);
                Double wartoscD = Double.parseDouble(wartoscS);
                String wartoscDS = String.valueOf(wartoscD);
                Log.d("mesydz2",wartoscDS);
                Double mniejszeWartD = wartoscD+0.01;


                String toWpisz = df.format(mniejszeWartD);
                toWpisz = toWpisz.replace(",", ".");
                tekst.setText(toWpisz, TextView.BufferType.EDITABLE);

            }
        });

        setListData();
        setListDataPaliwa();
        Resources res = getResources();

        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
        adapterStacje = new CustomAdapterStacje(activity, R.layout.spinner_rows, ListaNaStacje,res);


        spinner.setAdapter(adapterStacje);
        spinner2.setAdapter(adapter);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                      //  ArrayList<String> lista = new ArrayList<String>();
                        ItemData wybranaStacja = (ItemData) spinner.getSelectedItem();
                        String nazwaStacji = wybranaStacja.getText();
                        ItemData wybranePaliwo = (ItemData) spinner2.getSelectedItem();
                        String nazwaPaliwa = wybranePaliwo.getText();
                        String cena = String.valueOf(tekst.getText());
                        Double cenaD = Double.valueOf(cena);

                        GasStation stacjaBenz = new GasStation(nazwaStacji,polozenie.latitude,polozenie.longitude);
                        Gas gaz = new Gas(nazwaPaliwa,cenaD);

                        JSONObject stacj = new JSONObject();
                        JSONObject gas = new JSONObject();
                        try {
                            stacj = stacjaBenz.toJSON();
                            gas = gaz.toJSON();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




//                        Log.d("banul12",lista.get(0));
//                        Log.d("banul",lista.get(1));
//                        Log.d("banul",lista.get(2));

                        //wyślij tę listę do API

                    }
                })
                .setNegativeButton("Wyjdz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setListData() {
            final ItemData sched = new ItemData();
            final ItemData sched1 = new ItemData();
            final ItemData sched2= new ItemData();
            final ItemData sched3 = new ItemData();
            final ItemData sched4 = new ItemData();

            /******* Firstly take data in model object ******/

        sched.setText("Please select company ");
        CustomListViewValuesArr.add(sched);
            sched1.setText("PB95" );
            CustomListViewValuesArr.add(sched1);
        sched2.setText("PB98" );
        CustomListViewValuesArr.add(sched2);
            sched3.setText("ON ");
            CustomListViewValuesArr.add(sched3);
            sched4.setText("LPG ");
        CustomListViewValuesArr.add(sched4);
    }
    public void setListDataPaliwa() {
            final ItemData sched = new ItemData();
            final ItemData sched1 = new ItemData();
            final ItemData sched2= new ItemData();
            final ItemData sched3 = new ItemData();
            final ItemData sched4 = new ItemData();

            /******* Firstly take data in model object ******/

        sched.setText("Prosze podaj nazwe paliwa");
        ListaNaStacje.add(sched);
            sched1.setText("Shell" );
            ListaNaStacje.add(sched1);
        sched2.setText("Orlen" );
        ListaNaStacje.add(sched2);
            sched3.setText("Statoil");
            ListaNaStacje.add(sched3);
            sched4.setText("Lukoil ");
        ListaNaStacje.add(sched4);
    }

    @Override
    public void updateFromResponse(String response) {

    }
}
