package com.example.qonita.map;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private int REQUEST_CODE_LOCATION = 2;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_maps);
        mydb = new DBHelper(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Button start = (Button) findViewById(R.id.idStart);
        Button stop = (Button) findViewById(R.id.idStop);
        Button tutup = (Button) findViewById(R.id.idTutup);
        start.setOnClickListener(op);
        stop.setOnClickListener(op);
        tutup.setOnClickListener(op);
        locationListener = new MapsListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 200, locationListener);
*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button go = (Button) findViewById(R.id.btnZoom);
        go.setOnClickListener(op);

        Button cari = (Button) findViewById(R.id.btnCari);
        cari.setOnClickListener(op);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));

        EditText lat = (EditText) findViewById(R.id.idLokasiLat);
        EditText lng = (EditText) findViewById(R.id.idLokasiLng);
        String lat1 = String.valueOf(latitude);
        String lng1 = String.valueOf(longitude);
        lat.setText(lat1);
        lng.setText(lng1);




        // Add a marker in Sydney and move the camera
        /*LatLng ITS = new LatLng(-7.28, 112.79);
        mMap.addMarker(new MarkerOptions().position(ITS).title("Marker in ITS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITS, 15));*/
    }

    private void goToPeta(Double lat, Double lng, float z) {
        LatLng Lokasibaru = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(Lokasibaru).title("Marker in " + lat + ":" + lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lokasibaru, z));
        //inputData();
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText waktu = (EditText) findViewById(R.id.idWaktu);
            EditText jarak = (EditText) findViewById(R.id.idJarak);
            switch (v.getId()) {
                case R.id.btnZoom:
                    hideKeyboard(v);
                    goToLokasi();
                    break;
                case R.id.btnCari:
                    EditText zoom = (EditText) findViewById(R.id.etZoom);
                    if (zoom.getText().toString().isEmpty()) {
                        Toast.makeText(MapsActivity.this, "Isi perbesaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                    } else goCari();
                    break;
                case R.id.idStart:
                    Log.i("mapsactivity", "aktifkanGPS : true");
                    if(!waktu.getText().toString().isEmpty() && !jarak.getText().toString().isEmpty()){
                        aktifkanGPS(true);
                    }
                    else if(waktu.getText().toString().isEmpty() && jarak.getText().toString().isEmpty()){
                        Toast.makeText(MapsActivity.this,"Isi waktu dan jarak terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else if(waktu.getText().toString().isEmpty()){
                        Toast.makeText(MapsActivity.this,"Isi waktu terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else if(jarak.getText().toString().isEmpty()){
                        Toast.makeText(MapsActivity.this,"Isi jarak terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.idStop:
                    aktifkanGPS(false);
                    Log.i("mapsactivity", "aktifkanGPS : false");
                    break;
                case R.id.idTutup:
                    onDestroy();
                    MapsActivity.super.onDestroy();
                    break;
            }
        }
    };

    private void goToLokasi() {
        EditText lat = (EditText) findViewById(R.id.idLokasiLat);
        EditText lng = (EditText) findViewById(R.id.idLokasiLng);
        EditText zoom = (EditText) findViewById(R.id.etZoom);

        Double dbllat = Double.parseDouble(lat.getText().toString());
        Double dbllng = Double.parseDouble(lng.getText().toString());
        Float dblzoom = Float.parseFloat(zoom.getText().toString());

        Toast.makeText(this, "Move to Lat : " + dbllat + " Long : " + dbllng, Toast.LENGTH_LONG).show();
        goToPeta(dbllat, dbllng, dblzoom);

    }

    private void goCari() {
        EditText lat = (EditText) findViewById(R.id.idLokasiLat);
        EditText lng = (EditText) findViewById(R.id.idLokasiLng);
        EditText daerah = (EditText) findViewById(R.id.etDaerah);
        Geocoder g = new Geocoder(getBaseContext());
        try {
            List<android.location.Address> daftar = g.getFromLocationName(daerah.getText().toString(), 1);
            Address alamat = daftar.get(0);

            String findAlamat = alamat.getAddressLine(0);
            Double lintang = alamat.getLatitude();
            Double bujur = alamat.getLongitude();

           /* EditText lat = (EditText)findViewById(R.id.idLokasiLat);
            EditText lng = (EditText)findViewById(R.id.idLokasiLng);*/

            Double dbllat = Double.parseDouble(lat.getText().toString());
            Double dbllng = Double.parseDouble(lng.getText().toString());
            hitungJarak(dbllat, dbllng, lintang, bujur);

            Toast.makeText(getBaseContext(), "Found!" + findAlamat, Toast.LENGTH_SHORT).show();
            EditText zoom = (EditText) findViewById(R.id.etZoom);
            Float dblzoom = Float.parseFloat(zoom.getText().toString());
            Toast.makeText(this, "Move to " + findAlamat + " Lat :" + lintang + " Long:" + bujur, Toast.LENGTH_LONG).show();
            goToPeta(lintang, bujur, dblzoom);

            lat.setText(lintang.toString());
            lng.setText(bujur.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hitungJarak(double latAsal, Double lngAsal, double latTujuan, double lngTujuan) {
        Location asal = new Location("asal");
        Location tujuan = new Location("tujuan");
        tujuan.setLatitude(latTujuan);
        tujuan.setLongitude(lngTujuan);
        asal.setLatitude(latAsal);
        asal.setLongitude(lngAsal);
        float jarak = (float) asal.distanceTo(tujuan) / 1000;
        String jaraknya = String.valueOf(jarak);
        Toast.makeText(getBaseContext(), "jarak :" + jaraknya + " km", Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard(View v) {
        InputMethodManager a = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tipe_peta, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.satelit:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void aktifkanGPS(boolean onoff) {
        inputData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }
            else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);

            return;
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (onoff) {
            EditText waktu = (EditText) findViewById(R.id.idWaktu);
            EditText jarak = (EditText) findViewById(R.id.idJarak);

            Integer waktunya = Integer.parseInt(waktu.getText().toString());
            Integer jaraknya = Integer.parseInt(jarak.getText().toString());


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, waktunya, jaraknya, locationListener);
            Toast.makeText(getBaseContext(), "GPS Aktif Time: " + waktunya + " Range: " + jaraknya, Toast.LENGTH_LONG).show();

        } else {

            locationManager.removeUpdates(locationListener);
            Toast.makeText(getBaseContext(), "GPS NonAktif Time: ", Toast.LENGTH_LONG).show();

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void inputData(){
        EditText lat = (EditText) findViewById(R.id.idLokasiLat);
        EditText lng = (EditText) findViewById(R.id.idLokasiLng);
        if(mydb.insertMarker(lat.getText().toString(), lng.getText().toString())){
            Log.i("Activity","lat : "+lat.getText().toString());
            Log.i("Activity","lng : "+lng.getText().toString());
            Toast.makeText(getApplicationContext(), "Marker Added", Toast.LENGTH_SHORT).show();
            int id_x = mydb.getMaxId();
            Log.i("Activity","NEW ID : "+id_x);
        }
        else{
            Toast.makeText(getApplicationContext(), "Adding Marker Fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //optionally, stop location updates if only current location is needed
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }*/

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }



    private class MapsListener implements LocationListener {
        private EditText txtLat, txtLang;

        @Override
        public void onLocationChanged(Location location) {
            txtLang = (EditText) findViewById(R.id.idLokasiLng);
            txtLat = (EditText) findViewById(R.id.idLokasiLat);

            txtLat.setText(String.valueOf(location.getLatitude()));
            txtLang.setText(String.valueOf(location.getLongitude()));
            Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT).show();
            goToLokasi();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    }
