//// TODO: 2017-04-23 Pomyśleć zeby ten geolokalizator to był dynamiczny framgment

package com.example.user.proba3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,RequestCallback<String> {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    private Location mLocation2;
    double latitude = 0, longitude = 0;
    private LocationManager locationManager;
    private LocationListener mlocListener;
    private Marker now;
    private Marker dodany;
    private Marker pierwszy;
    private ProgressDialog dialog;
    private EditText tekst;
    private Button przycisk;
    private List<LatLng> lista;
    private boolean czyPokazacPrompt = false;
    private boolean czyPromptZostalpokazany = false;
    private Location lokalizacjaStacjiNaKtorejJestesmy;
    private Spinner spinner;
    DownloadRequestTask download = new DownloadRequestTask(this);
    private boolean czyTrybSledzenia = true;
    String url = "https://script.google.com/macros/s/AKfycbwi_fjw8oLX5gYWuPmukORIFkV4S-hzJRqBlIFngtLCq7uE5j4/exec";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("lokacja", "create");
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!enabled)
            showAlertWindow();

        if (enabled) {
            gpsTracker = new GPSTracker(this.getApplicationContext());
            mLocation = gpsTracker.getLocation();
            longitude = mLocation.getLongitude();
            latitude = mLocation.getLatitude();


            setContentView(R.layout.activity_maps);




            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            dialog = ProgressDialog.show(MapsActivity.this, "", "Prosze czekac na wczytanie sie aplikacji", true);
            dialog.show();

        }

        mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dialog.dismiss();

                if (now != null) {
                    {
                        now.remove();
                    }

                }

                // Getting latitude of the current location
                double latitude = location.getLatitude();

                // Getting longitude of the current location
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                now = mMap.addMarker(new MarkerOptions().position(latLng));
                if (czyTrybSledzenia){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }

                Location nasza = new Location("");
                nasza.setLatitude(latLng.latitude);
                nasza.setLongitude(latLng.longitude);

                if (lokalizacjaStacjiNaKtorejJestesmy!=null && czyPromptZostalpokazany) {
                    double dystans = nasza.distanceTo(lokalizacjaStacjiNaKtorejJestesmy); //dystans w metrach

                    if (dystans > 250) {
                        czyPromptZostalpokazany = false;
                    }
                }

                if (SprawdzCzyJestesNaStacji(latLng)) {

                    long czas = System.currentTimeMillis();
                    long czasPrompt = System.currentTimeMillis();

                    KlasaSprawdzajacaCzyPokazacPrompt obj = new KlasaSprawdzajacaCzyPokazacPrompt(czas, latLng);




                    if (czyPokazacPrompt && !czyPromptZostalpokazany) {

                        LatLng polo = new LatLng(latitude,longitude);
                        DialogDodajStacjeMarker dialog2 = new DialogDodajStacjeMarker(polo);
                        dialog2.show(getFragmentManager(), "my_dialog");
                        czyPromptZostalpokazany = true;

                    }




                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("lokacja1", "cosTam2");


            }

            @Override
            public void onProviderEnabled(String provider) {

                Log.d("lokacja1", "cosTam");

                longitude = mLocation.getLongitude();
                latitude = mLocation.getLatitude();
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
        // Toolbar mToolbar = (Toolbar)findViewById(R.id.button3);

        // Button mBut = (Button) findViewById(R.id.button2);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        Button pokazStacje = (Button) findViewById(R.id.PokazStacje);

        pokazStacje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                download.execute(url, "GET");

                Log.d("data70","data70");


                Log.d("data71","data71");


            }
        });
        setSupportActionBar(mToolbar);
     //   tekst = (EditText) findViewById(R.id.editText);
     //   przycisk = (Button) findViewById(R.id.button2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("menu", "menuu");
        Log.d("cokolwi", "dzialaj");
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lokalizator:
                czyTrybSledzenia=!czyTrybSledzenia;

//                if (tekst.isShown()) {
//                    tekst.setVisibility(View.INVISIBLE);
//                    przycisk.setVisibility(View.INVISIBLE);
//
//                } else if (!tekst.isShown()) {
//                    tekst.setVisibility(View.VISIBLE);
//                    przycisk.setVisibility(View.VISIBLE);



//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    }
//
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
//
//                }//              break;
            case R.id.Normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.Terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Log.d("terrain", "terra");
                break;

            case R.id.Satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.Hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;


        }
        return super.onContextItemSelected(item);


    }


    protected void onResume() {
        super.onResume();
        Log.d("lokacja", "resume");

    }


    protected void onStop() {
        super.onStop();
        Log.d("lokacja", "stop");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    protected void onDestroy() {
        super.onDestroy();
    }

    void kill_activity() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        float zoom = 15;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        final LatLng polozenie = new LatLng(latitude, longitude);
      // mMap.addMarker(new MarkerOptions().position(polozenie).title("Marker here"));
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie,zoom));

        if (mMap != null)
        {
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng latLng) {

                    DialogDodajStacjeMarker dialog = new DialogDodajStacjeMarker(polozenie);
                    dialog.show(getFragmentManager(), "my_dialog");
                    pierwszy = mMap.addMarker(new MarkerOptions().position(latLng));
                    lista = new ArrayList<LatLng>();
                    lista.add(latLng);
                    Log.d("lista",lista.toString());

                }
            });
        }
    }


    public void showAlertWindow()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Twoj GPS jest wylaczany, czy chcesz go wlaczyc?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog,final int id) {

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }




//    public void geoLocate(View view) {
//        EditText et = (EditText) findViewById(R.id.editText);
//        String location = et.getText().toString();
//        Geocoder gc = new Geocoder(this);
//        try {
//            List<Address> list = gc.getFromLocationName(location, 1);
//            Address address = list.get(0);
//            String locality = address.getLocality();
//            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
//            double latitude = address.getLatitude();
//            double longitude = address.getLongitude();
//            goToLocation(latitude,longitude,15);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//        public void goToLocation(double lat, double lon, float zoom)
//        {
//            LatLng ll = new LatLng(lat, lon);
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
//            mMap.moveCamera(update);
//        }

        public boolean SprawdzCzyJestesNaStacji(LatLng latLng)
        {

            boolean zwroc = false;
            Location lokacjaNasza = new Location("");
            lokacjaNasza.setLongitude(latLng.longitude);
            lokacjaNasza.setLatitude(latLng.latitude);
            Location lokacjaMarkera = new Location("");
            double dystans=1000;

            if (lista != null) {
                for (LatLng punkt : lista) {
                    Log.d("aa","bbccddf");
                    lokacjaMarkera.setLatitude(punkt.latitude);
                    lokacjaMarkera.setLongitude(punkt.longitude);
                    dystans = lokacjaNasza.distanceTo(lokacjaMarkera); //dystans w metrach
                    if (dystans<=200) {
                        zwroc = true;
                        lokalizacjaStacjiNaKtorejJestesmy = lokacjaMarkera;
                        break;
                    }
                }

            }



            return zwroc;
        }

    @Override
    public void updateFromResponse(String response) {

        Log.d("data77","upd");
        Log.d("data78",response);


    }


    private class KlasaSprawdzajacaCzyPokazacPrompt extends AsyncTask< Void, Void ,Boolean >
        {
            long _czas;
            LatLng _polozenie;

            public  KlasaSprawdzajacaCzyPokazacPrompt(long _czas,LatLng _polozenie){
                this._czas = _czas;
                this._polozenie = _polozenie;
            }


            @Override
            protected Boolean doInBackground(Void... params) {

                while (System.currentTimeMillis()-_czas < 10000)
                {
                    if (SprawdzCzyJestesNaStacji(_polozenie) == false)
                        break;
                    else {
                        czyPokazacPrompt = true;
                    }
                }
                return czyPokazacPrompt;
            }
        }

    }





