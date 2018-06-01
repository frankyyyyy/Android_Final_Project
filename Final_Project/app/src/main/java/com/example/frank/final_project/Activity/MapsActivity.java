package com.example.frank.final_project.Activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Map demonstration on store address location
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.map_toolbar)
    Toolbar toolbar;

    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private String mStoreAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_Fg);
        mapFragment.getMapAsync(this);

        // Setup tool bar button
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        // Set back button function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     *  Create map ready for call back
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mStoreAddress = CurrentUser.getStore().getAddress();
        mGeocoder = new Geocoder(this);
        try {
            List<Address> addressList = mGeocoder.getFromLocationName(mStoreAddress, 3);
            if(addressList != null && addressList.size()>0){
                Address addressLatLng = addressList.get(0);
                LatLng storeLocation = new LatLng(addressLatLng.getLatitude(), addressLatLng.getLongitude());
                mMap.addMarker(new MarkerOptions().position(storeLocation).title(mStoreAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(storeLocation));
            }else{
                finish();
                Toast.makeText(this, getString(R.string.map_error_warning), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
