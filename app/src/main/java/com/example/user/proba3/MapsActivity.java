

package com.example.user.proba3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import com.example.user.proba3.dataModel.Gas;
import com.example.user.proba3.dataModel.GasStation;
import com.example.user.proba3.network.DownloadRequestTask;
import com.example.user.proba3.network.RequestCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RequestCallback<String>, GoogleMap.OnMarkerClickListener {

    private final int LOCATIONPERMISSION = 0;
    private GoogleMap mMap;
    double latitude = 0, longitude = 0;
    private LocationManager locationManager;
    private LocationListener mlocListener;
    private Marker now;
    private ProgressDialog dialog;
    private ArrayList<GasStation> listaStacji = new ArrayList<GasStation>();
    private boolean czyPokazacPrompt = false;
    private boolean czyPromptZostalpokazany = false;
    private Location lokalizacjaStacjiNaKtorejJestesmy;
    private Location ObecnaLokacja;
    private Spinner spinner;
    //    private KlasaSprawdzajacaCzyPokazacPrompt obj;
    private boolean sprawdzaniePromta = false;
    private DialogChooseGas dialogChooseGas;
    private double lowestPrice;
    TimerTask timerTask;
    Timer timer;

    private boolean czyTrybSledzenia = true;
    String url;
    String chosenFuel = "PB95";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getString(R.string.api_url);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                downloadStations();
            }
        };

        timer = new Timer();


        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!enabled)
            showAlertWindow();

        if (enabled) {

            setContentView(R.layout.activity_maps);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            dialog = new ProgressDialog(MapsActivity.this);
            dialog.setTitle("KAPPA");
            dialog.show();


        }


        mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dialog.dismiss();
                ObecnaLokacja = location;


                // Getting latitude of the current location
                latitude = location.getLatitude();

                // Getting longitude of the current location
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                if (czyTrybSledzenia) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }

                //TODO dodac snippety do stacji
                Location nasza = new Location("");
                nasza.setLatitude(latLng.latitude);
                nasza.setLongitude(latLng.longitude);

                if (lokalizacjaStacjiNaKtorejJestesmy != null && czyPromptZostalpokazany) {
                    double dystans = nasza.distanceTo(lokalizacjaStacjiNaKtorejJestesmy); //dystans w metrach

                    if (dystans > 200) {
                        czyPromptZostalpokazany = false;
                    }
                }

                if (SprawdzCzyJestesNaStacji(latLng)) {

                    Log.d("dupa100", "dupa100");
                    long czas = System.currentTimeMillis();

                   /* if (obj == null) {
                        obj = new KlasaSprawdzajacaCzyPokazacPrompt(czas, latLng);
                        obj.execute();

                    }*/

                    if (sprawdzaniePromta == false) {
                        Log.d("dupa18", "dupa18");
                        sprawdzaniePromta = true;
                        KlasaSprawdzajacaCzyPokazacPrompt obj = new KlasaSprawdzajacaCzyPokazacPrompt(czas, latLng);
                        obj.execute();

                    }


                /*    if (czyPokazacPrompt && !czyPromptZostalpokazany) {

                       // DialogDodajStacjeMarker dialog2 = new DialogDodajStacjeMarker();
                     //   dialog2.show(getFragmentManager(), "my_dialog");

                        DialogUpdatePrice updateDialog = new DialogUpdatePrice();
                        updateDialog.setFuelName(chosenFuel);
                        updateDialog.setGasStation(getStationByLocation(lokalizacjaStacjiNaKtorejJestesmy));
                        updateDialog.show(getFragmentManager(),"updateDialog");
                        czyPromptZostalpokazany = true;
                        czyPokazacPrompt = false;
                    }*/
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


            }


            @Override
            public void onProviderEnabled(String provider) {


                setContentView(R.layout.activity_maps);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
            }

            @Override
            public void onProviderDisabled(String provider) {


            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lokalizator:
                czyTrybSledzenia = !czyTrybSledzenia;

            case R.id.Normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.Terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.Satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.Hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.choose_gas_type:

                // Wyświetlanie dialogu z wyborem
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                dialogChooseGas = new DialogChooseGas(this);
                dialogChooseGas.show(ft, "dialog");

                break;
            default:
                break;


        }
        return super.onContextItemSelected(item);


    }

    protected void onResume() {
        super.onResume();


    }

    protected void onStop() {
        super.onStop();
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    void kill_activity() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        timer.schedule(timerTask, 3000, 60000);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONPERMISSION);


        mMap = googleMap;

        float zoom = 15;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        final LatLng polozenie = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, zoom));

        if (mMap != null) {
            mMap.setOnMapLongClickListener(
                    new GoogleMap.OnMapLongClickListener() {

                        @Override
                        public void onMapLongClick(LatLng latLng) {

                            DialogDodajStacjeMarker dialog = new DialogDodajStacjeMarker(latLng, mMap);
                            dialog.show(getFragmentManager(), "my_dialog");
                        }
                    });
        }


    }


    public void showAlertWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Twoj GPS jest wylaczany, czy chcesz go wlaczyc?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean SprawdzCzyJestesNaStacji(LatLng latLng) {
        Log.d("sprawdzStacje", "SprawdzStacje");
        boolean zwroc = false;
        Location lokacjaNasza = new Location("");
        lokacjaNasza.setLongitude(latLng.longitude);
        lokacjaNasza.setLatitude(latLng.latitude);
        Location lokacjaMarkera = new Location("");
        double dystans = 1000;

        if (listaStacji != null && listaStacji.size() != 0) {
            zwroc = false;
            for (GasStation punkt : listaStacji) {
                if (punkt.isShown() == true) {

                    lokacjaMarkera.setLatitude(punkt.getLatitiude());
                    lokacjaMarkera.setLongitude(punkt.getLongitude());
                    dystans = lokacjaNasza.distanceTo(lokacjaMarkera); //dystans w metrach


                    if (dystans <= 200) {

                        zwroc = true;
                        lokalizacjaStacjiNaKtorejJestesmy = lokacjaMarkera;
                        break;
                    }
                }

            }
        }

        return zwroc;
    }


    public boolean CzyJestesmyNaStacjiWersjaBezIterowaniaPoLiscie(Location lokalizacjaStacjiNaKtorejJestesmy, Location lokacjaNasza) {
        double dystans = lokalizacjaStacjiNaKtorejJestesmy.distanceTo(lokacjaNasza);
        if (dystans <= 200) {

            boolean zwroc = true;
            return zwroc;
        } else
            return false;
    }

    @Override
    public void updateFromResponse(String response) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    public void changeFuelPreference(String fuel)

    {
        chosenFuel = fuel;
        ArrayList<GasStation> tablStacj = new ArrayList<>();
        for (GasStation element : listaStacji) {
            element.setShown(false);
            ArrayList<Gas> listaGazow = element.zwrocListeGazow(); //lista gazów dla danej stacji
            for (Gas gas : listaGazow) {

                if (gas.getName().replace(" ", "").equals(fuel)) {
                    tablStacj.add(element);
                }

            }
        }

        Collections.sort(tablStacj, new GasStationComparator(fuel));
        if (tablStacj.size() != 0) {

            GasStation stationLowestPrice = tablStacj.get(0); // stacja z najnizszym szukanym paliwem
            ArrayList<Gas> listaGazowSzukamyNajmniejszejWartosci = stationLowestPrice.zwrocListeGazow();

            for (int i = 0; i < listaGazowSzukamyNajmniejszejWartosci.size(); i++) {
                if (fuel.equals(listaGazowSzukamyNajmniejszejWartosci.get(i).getName().replace(" ", ""))) {
                    lowestPrice = listaGazowSzukamyNajmniejszejWartosci.get(i).getPrice();

                    break;
                }
            }

            ArrayList<GasStation> najmnniejszeCeny = new ArrayList<>();
            ArrayList<GasStation> srednieCeny = new ArrayList<>();
            ArrayList<GasStation> wysokieCeny = new ArrayList<>();

            for (GasStation element : tablStacj) {
                ArrayList<Gas> listaG = element.zwrocListeGazow();

                for (int i = 0; i < element.zwrocListeGazow().size(); i++) {
                    if (fuel.equals(listaG.get(i).getName().replace(" ", ""))) {
                        double cena = listaG.get(i).getPrice();

                        if (cena <= lowestPrice + 0.1) {
                            najmnniejszeCeny.add(element);
                            continue;
                        } else if (cena > lowestPrice + 0.1 && cena <= lowestPrice + 0.3) {
                            srednieCeny.add(element);
                            continue;
                        } else {
                            wysokieCeny.add(element);
                            continue;
                        }

                    }
                }
            }
            mMap.clear();
            for (GasStation stacja : najmnniejszeCeny) {

                ArrayList<Gas> g = stacja.zwrocListeGazow();
                stacja.setShown(true);
                double price = g.get(ZwrocIndeksListy(stacja, fuel)).getPrice();
                mMap.addMarker(new MarkerOptions().position(new LatLng(stacja.getLatitiude(), stacja.getLongitude())).title(stacja.getOwner()).snippet(fuel + " " + price).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            }

            for (GasStation stacja : srednieCeny) {

                ArrayList<Gas> g = stacja.zwrocListeGazow();
                stacja.setShown(true);

                double price = g.get(ZwrocIndeksListy(stacja, fuel)).getPrice();
                mMap.addMarker(new MarkerOptions().position(new LatLng(stacja.getLatitiude(), stacja.getLongitude())).title(stacja.getOwner()).snippet(fuel + " " + price).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }


            for (GasStation stacja : wysokieCeny) {
                ArrayList<Gas> g = stacja.zwrocListeGazow();
                stacja.setShown(true);

                double price = g.get(ZwrocIndeksListy(stacja, fuel)).getPrice();
                mMap.addMarker(new MarkerOptions().position(new LatLng(stacja.getLatitiude(), stacja.getLongitude())).title(stacja.getOwner()).snippet(fuel + " " + price).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        } else
            mMap.clear();

    }

    public int ZwrocIndeksListy(GasStation gs, String co) {
        int zwroc = 0;
        ArrayList<Gas> listaGaz = new ArrayList<>();
        listaGaz = gs.zwrocListeGazow();

        for (int i = 0; i < listaGaz.size(); i++) {
            if (co.equals(listaGaz.get(i).getName().replace(" ", ""))) {
                zwroc = i;
                break;
            }
        }
        return zwroc;

    }

    private class KlasaSprawdzajacaCzyPokazacPrompt extends AsyncTask<Void, Void, Boolean> {
        long _czas;
        LatLng _polozenie;

        public KlasaSprawdzajacaCzyPokazacPrompt(long _czas, LatLng _polozenie) {
            this._czas = _czas;
            this._polozenie = _polozenie;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            czyPokazacPrompt = false;

            while (System.currentTimeMillis() - _czas < 10000) {

                if (!CzyJestesmyNaStacjiWersjaBezIterowaniaPoLiscie(lokalizacjaStacjiNaKtorejJestesmy, ObecnaLokacja)) {
                    czyPokazacPrompt = false;
                    break;
                } else {
                    czyPokazacPrompt = true;
                }
            }

            if (czyPokazacPrompt && !czyPromptZostalpokazany) {
                DialogUpdatePrice updateDialog = new DialogUpdatePrice();
                updateDialog.setFuelName(chosenFuel);
                updateDialog.setGasStation(getStationByLocation(lokalizacjaStacjiNaKtorejJestesmy));
                updateDialog.show(getFragmentManager(), "updateDialog");
                czyPromptZostalpokazany = true;
                czyPokazacPrompt = false;
            }
            return czyPokazacPrompt;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            sprawdzaniePromta = false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATIONPERMISSION) {
            if (permissions.length == 1 && Objects.equals(permissions[0], Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);

            }
        }
    }

    public void downloadStations() {

        listaStacji.clear();
        DownloadRequestTask downloadRequestTask = new DownloadRequestTask(new RequestCallback<String>() {
            @Override
            public void updateFromResponse(String response) {
                try {
                    JSONArray jResponse = new JSONArray(response);
                    for (int i = 0; i < jResponse.length(); i++) {
                        GasStation gasStation = GasStation.parseJSON(jResponse.getJSONObject(i));
                        listaStacji.add(gasStation);
                    }

                    changeFuelPreference(chosenFuel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        downloadRequestTask.execute(url, "GET", "50000", Double.toString(latitude), Double.toString(longitude));
    }

    public GasStation getStationByLocation(Location lokalizacjaStacji) {
        GasStation returnStation = new GasStation();
        for (GasStation gasStation : listaStacji
                ) {
            Location currentStationLocation = new Location("");
            currentStationLocation.setLatitude(gasStation.getLatitiude());
            currentStationLocation.setLongitude(gasStation.getLongitude());
            if (currentStationLocation.distanceTo(lokalizacjaStacji) < 200) {
                returnStation = gasStation;
                break;
            }

        }

        return returnStation;
    }

}







