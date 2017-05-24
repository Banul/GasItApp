package com.example.user.proba3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by jaroslaw on 23.05.2017.
 */

public class DialogChooseGas extends DialogFragment {

    public static final String MyPREFERENCES = "GasItAppPrefs";
    public static final String FUEL_CHOOSE = "fuelChoosePrefsKey";

    SharedPreferences sharedPreferences;


    public DialogChooseGas(SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fuel_choose, container, false);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.rgFuelChoose);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedID) {
                onRadioButtonClicked(checkedID);
            }
        });
        return v;
    }

    public void onRadioButtonClicked(int checkedID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (checkedID) {
            case R.id.rbPb95:

                    editor.putString(FUEL_CHOOSE, "Pb95");
                break;
            case R.id.rbPb98:

                    editor.putString(FUEL_CHOOSE, "Pb98");
                break;
            case R.id.rbON:

                    editor.putString(FUEL_CHOOSE, "ON");
                break;
            case R.id.rbLPG:

                    editor.putString(FUEL_CHOOSE, "LPG");
                break;

        }
        dismiss();
    }
}
