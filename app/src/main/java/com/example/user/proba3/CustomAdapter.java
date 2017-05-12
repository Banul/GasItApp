package com.example.user.proba3;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.proba3.DialogDodajStacjeMarker;
import com.example.user.proba3.ItemData;

/***** Adapter class extends with ArrayAdapter ******/
public class CustomAdapter extends ArrayAdapter<String>{

    private Activity activity;
    private ArrayList data;
    public Resources res;
    ItemData tempValues=null;
    LayoutInflater inflater;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(
            Activity activitySpinner,
            int textViewResourceId,
            ArrayList objects,
            Resources resLocal
    )
    {
        super(activitySpinner, textViewResourceId, objects);

        /********** Take passed values **********/
        activity = activitySpinner;
        data     = objects;
        res      = resLocal;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_rows, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (ItemData) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.company);
        ImageView companyLogo = (ImageView)row.findViewById(R.id.image);

        if(position==0){

            // Default selected Spinner item
            label.setText("Prosze wybrac paliwo");
        }
        else
        {
            // Set values for spinner each row
            label.setText(tempValues.getText());
            switch(position) {
                case 1:
                    companyLogo.setImageResource(R.drawable.pb95);
                    break;
                case 2:
                    companyLogo.setImageResource(R.drawable.pb98);
                    break;
                case 3:
                    companyLogo.setImageResource(R.drawable.on);
                    break;
                case 4:
                    companyLogo.setImageResource(R.drawable.gaz);
                    break;
            }



        }

        return row;
    }
}
