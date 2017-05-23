

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

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
import java.util.Objects;


//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RequestCallback<String>, GoogleMap.OnMarkerClickListener {

    private final int LOCATIONPERMISSION = 0;
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
    private ArrayList<Marker> lista = new ArrayList<Marker>();
    private boolean czyPokazacPrompt = false;
    private boolean czyPromptZostalpokazany = false;
    private Location lokalizacjaStacjiNaKtorejJestesmy;
    private Location ObecnaLokacja;
    private Spinner spinner;
    private KlasaSprawdzajacaCzyPokazacPrompt obj;
    private Marker melbourne;
    private DialogChooseGas dialogChooseGas;

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


          /*  gpsTracker = new GPSTracker(this.getApplicationContext());
            mLocation = gpsTracker.getLocation();

                longitude = mLocation.getLongitude();
                latitude = mLocation.getLatitude();*/


            setContentView(R.layout.activity_maps);


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
                Log.d("locationChanged","changed");
                ObecnaLokacja = location;


                if (now != null) {
                    {
                        now.remove();
                    }
                }

                // Getting latitude of the current location
                 latitude = location.getLatitude();

                // Getting longitude of the current location
                 longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                now = mMap.addMarker(new MarkerOptions().position(latLng).title("Twoje polozenie").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
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

                    Log.d("dupa100","dupa100");
                    long czas = System.currentTimeMillis();
                    long czasPrompt = System.currentTimeMillis();

                    if (obj==null) {
                        Log.d("dupa18","dupa18");
                        obj = new KlasaSprawdzajacaCzyPokazacPrompt(czas, latLng);
                        obj.execute();

                    }



                    if (czyPokazacPrompt && !czyPromptZostalpokazany) {

                        LatLng polo = new LatLng(latitude, longitude);
                        DialogDodajStacjeMarker dialog2 = new DialogDodajStacjeMarker();
                        dialog2.show(getFragmentManager(), "my_dialog");
                        czyPromptZostalpokazany = true;
                        czyPokazacPrompt = false;
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("lokacja1", "cosTam2");


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
        // Toolbar mToolbar = (Toolbar)findViewById(R.id.button3);

        // Button mBut = (Button) findViewById(R.id.button2);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        Button pokazStacje = (Button) findViewById(R.id.PokazStacje);


        pokazStacje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadRequestTask downloadRequestTask = new DownloadRequestTask(new RequestCallback<String>() {
                    @Override
                    public void updateFromResponse(String response) {
                        try {
                            JSONArray jResponse = new JSONArray(response);
                            for (int i = 0; i < jResponse.length(); i++) {
                                GasStation gasStation = GasStation.parseJSON(jResponse.getJSONObject(i));
                                LatLng station = new LatLng(gasStation.getLatitiude(), gasStation.getLongitude());
                                ArrayList<Gas> ListaGaz = gasStation.zwrocListeGazow();

                                ArrayList <Gas> listaObiektow  = new ArrayList<>();

                                // tutaj ten snippet.
                                String snippetString = ListaGaz.get(0).getName()+" "+ListaGaz.get(0).getPrice();
                                Marker stationMarker = mMap.addMarker(new MarkerOptions().position(station).title(gasStation.getOwner()).snippet(snippetString));
                                lista.add(stationMarker);
                                Log.d("aaaa","ccbb");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //multiple lines snippet
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getApplicationContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getApplicationContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getApplicationContext());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

                downloadRequestTask.execute(url, "GET", "500000", Double.toString(latitude), Double.toString(longitude));


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
                czyTrybSledzenia = !czyTrybSledzenia;

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
            case R.id.choose_gas_type:

                // Wyświetlanie dialogu z wyborem
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if(prev!=null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                dialogChooseGas = new DialogChooseGas(new JobDoneCallback() {
                    @Override
                    public void jobDone() {
                       // gasChanged();

                    }
                }, getBaseContext());
                dialogChooseGas.show(ft, "dialog");

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
public void gasChanged() {
    dialogChooseGas.dismiss();
}

    protected void onStop() {
        super.onStop();
        Log.d("lokacja", "stop");
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    void kill_activity() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //   dialog.dismiss();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONPERMISSION);

//        final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

        mMap = googleMap;

//   n


       // mMap.setOnMarkerClickListener(this);
        float zoom = 15;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //    mMap.setMyLocationEnabled(true);

        final LatLng polozenie = new LatLng(latitude, longitude);
        // mMap.addMarker(new MarkerOptions().position(polozenie).title("Marker here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, zoom));

        if (mMap != null) {
            mMap.setOnMapLongClickListener(
                    new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng latLng) {

                    DialogDodajStacjeMarker dialog = new DialogDodajStacjeMarker(latLng, lista, mMap);
                    dialog.show(getFragmentManager(), "my_dialog");

                    //              mMap.addMarker(new MarkerOptions().position(latLng));
//
//                    lista = new ArrayList<LatLng>();
           //         lista.add(latLng);

                    Log.d("lista", lista.toString());

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

    public boolean SprawdzCzyJestesNaStacji(LatLng latLng) {
        Log.d("sprawdzStacje", "SprawdzStacje");
        boolean zwroc = false;
        Location lokacjaNasza = new Location("");
        lokacjaNasza.setLongitude(latLng.longitude);
        lokacjaNasza.setLatitude(latLng.latitude);
        Location lokacjaMarkera = new Location("");
        double dystans = 1000;

        if (lista != null && lista.size()!=0) {
            zwroc = false;
            for (Marker punkt : lista) {
                Log.d("aa", "bbccddf");
                lokacjaMarkera.setLatitude(punkt.getPosition().latitude);
                lokacjaMarkera.setLongitude(punkt.getPosition().longitude);
                dystans = lokacjaNasza.distanceTo(lokacjaMarkera); //dystans w metrach
                Log.d("dystanss",String.valueOf(dystans));
                Log.d("chuj12","chuj12");

                if (dystans <= 200) {
                    Log.d("zwracam true", "zwracam true");
                    zwroc = true;
                    lokalizacjaStacjiNaKtorejJestesmy = lokacjaMarkera;
                    break;
                    }

                }
            }

        return zwroc;
    }



    public boolean CzyJestesmyNaStacjiWersjaBezIterowaniaPoLiscie(Location lokalizacjaStacjiNaKtorejJestesmy, Location lokacjaNasza)
    {
        double dystans = lokalizacjaStacjiNaKtorejJestesmy.distanceTo(lokacjaNasza);
        if (dystans <= 200) {
            Log.d("Jestm na stacji","na stacji");
            boolean zwroc = true;
            return zwroc;
        }

        else
            return false;
    }

    @Override
    public void updateFromResponse(String response) {

        Log.d("data77", "upd");
        Log.d("data78", response);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("dupa123","dupa123");
//        if(!marker.isInfoWindowShown()) {
//            melbourne.showInfoWindow();
//            Log.d("isShownh","isShownh");
//        }
//        if(marker.isInfoWindowShown()) {
//            melbourne.hideInfoWindow();
//            Log.d("isHiden","isHiden");
//
//        }

        return true;
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
            Log.d("kebabNaCienkim", "kebabNaCienkim");
            while (System.currentTimeMillis() - _czas < 10000) {
                Log.d("Sprawdz", "Sprawdzam");
                if (!CzyJestesmyNaStacjiWersjaBezIterowaniaPoLiscie(lokalizacjaStacjiNaKtorejJestesmy,ObecnaLokacja))
                    break;
                else {
                    czyPokazacPrompt = true;
                    Log.d("pokazprompt","pokazprompt");
//                    try {
//                    //    wait(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            return czyPokazacPrompt;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATIONPERMISSION){
            if(permissions.length == 1 && Objects.equals(permissions[0], Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){

                mMap.setMyLocationEnabled(true);

            }
            }
        }

    }







