package com.example.booktracker.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.booktracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.ContentValues.TAG;

public class SetGeoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final float HUE_BLUE = 200f;
    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final float DEFAULT_ZOOM = 10;
    private FusedLocationProviderClient mFusedLocationClient;
    private final Marker[] marker = {null};
    private Marker pickupMarker = marker[0];
    private Double pickupLat = null;
    private Double pickupLng = null;
    private GoogleMap map;

    //edmonton
    private LatLng defaultLocation = new LatLng(53.5461, -113.4938);

    /**
     *  SetGeo creation - create map, initialize buttons, GPS permissions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_geo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button cancelBtn = findViewById(R.id.geo_cancel_button);
        cancelBtn.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        });

        Button confirmBtn = findViewById(R.id.geo_confirm_button);
        confirmBtn.setOnClickListener(view -> {
            if (pickupMarker != null) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("pickupLat", pickupLat);
                returnIntent.putExtra("pickupLng", pickupLng);
                setResult(RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show();
            }
        });

        checkGPS();

    }

    /**
     *   Setup map location based on current location if available otherwise default edmonton
     *   Listener for pin placement of pickup location
     * @param retMap map returned from creation
     */
    @Override
    public void onMapReady(GoogleMap retMap) {
        map = retMap;
        if (!mLocationPermissionGranted) {
            Log.d(TAG, "Current location is unavailable. Using defaults.");
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            getCurrentLocation();
        }
        Toast.makeText(this, "Hold location to place pickup spot", Toast.LENGTH_SHORT).show();

        map.setOnMapLongClickListener(latLng -> {
            pickupLat = latLng.latitude;
            pickupLng = latLng.longitude;
            if (pickupMarker != null) {
                pickupMarker.setPosition(latLng);
            } else {
                pickupMarker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_BLUE))
                        .position(latLng)
                        .title("Pickup location"));
            }
        });
    }

    /**
     *   If GPS available - retrieve lat/lon and
     */
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                LatLng userCurrentPosition = new LatLng(lat, lon);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentPosition, 15));
                Log.d(TAG, "lat: " + lat);
                Log.d(TAG, "lon: " + lon);
            }
        });
    }

    /**
     *   Check google services, GPS enabled, and app location permission
     *   If permission not granted - prompt
     */
    public void checkGPS() {
        if (isServicesOK()) {
            if (isGPSEnabled()) {
                if (!mLocationPermissionGranted) {
                    getLocationPermission();
                }
            }
        }
    }

    /**
     *   Check if google services are functional
     */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SetGeoActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SetGeoActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     *   Check if device GPS is enabled, if not prompt option to enable
     */
    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /**
     *   Dialog to choose whether user wants GPS functionality or not
     *   If yes then handle callback to verify with onActivityResult
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // continue without GPS functionality
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *   If user wants GPS enabled - check upon returning to app whether it was actually enabled
     *   If enabled then ensure app specific location is enabled
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (!mLocationPermissionGranted) {
                getLocationPermission();
            }
        }
    }

    /**
     *   https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
     *   Request specific permissions to use location for bookTracker
     *   The result of the permission request is handled by a callback,
     *   onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     *   https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
     *   Results from requesting location permission for bookTracker
     *   If allowed set map to zoom on current location
     * @param requestCode
     * @param permissions
     * @param grantResults - empty if permission is not granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                getCurrentLocation();
            }
        }
    }

}