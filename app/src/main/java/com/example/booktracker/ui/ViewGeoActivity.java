package com.example.booktracker.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.booktracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewGeoActivity extends AppCompatActivity implements OnMapReadyCallback {
    Double pickupLat = null;
    Double pickupLng = null;

    /**
     *  SetGeo creation - create map, initialize buttons, GPS permissions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickupLat = getIntent().getDoubleExtra("pickupLat", -1);
        pickupLng = getIntent().getDoubleExtra("pickupLng", -1);


        setContentView(R.layout.activity_view_geo);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button doneBtn = findViewById(R.id.view_geo_done_button);
        doneBtn.setOnClickListener(view -> {
            finish();
        });

    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng pickupLoc = new LatLng(pickupLat, pickupLng);
        map.addMarker(new MarkerOptions()
                .position(pickupLoc)
                .title("Pickup Location"));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

    }
}
