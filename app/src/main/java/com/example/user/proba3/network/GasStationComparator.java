package com.example.user.proba3.network;

import com.example.user.proba3.dataModel.Gas;
import com.example.user.proba3.dataModel.GasStation;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by User on 2017-05-24.
 */

public class GasStationComparator implements Comparator<GasStation> {

    private String whatToCompare;

public GasStationComparator(String whatToCompare)
{
    this.whatToCompare = whatToCompare;
}
    @Override
    public int compare(GasStation o1, GasStation o2) {
        ArrayList<Gas> g1 = o1.zwrocListeGazow();
        ArrayList<Gas> g2 = o2.zwrocListeGazow();
        int indexG1=0;
        int indexG2=0;


        for (int i =0; i<g1.size();i++)
        {
         if (whatToCompare.equals(g1.get(i).getName()))
         {
             indexG1 = i;
             break;
         }

        }

        for (int i =0; i<g2.size();i++)
        {
            if (whatToCompare.equals(g2.get(i).getName()))
            {
                indexG2 = i;
                break;
            }

        }

        int zwroc=0;

        if(g1.get(indexG1).getPrice() < g2.get(indexG2).getPrice())
            zwroc = -1;
        else if (g1.get(indexG1).getPrice() > g2.get(indexG2).getPrice())
            zwroc = 1;
        else if (g1.get(indexG1).getPrice() == g2.get(indexG2).getPrice())
            zwroc = 0;


        return zwroc;

    }
}
