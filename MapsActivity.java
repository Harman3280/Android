package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private int fineRequestCode=101;
    private LatLng userLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Double rr;
    private Double distance;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==fineRequestCode){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                //if permission for location is granted
                mMap.setMyLocationEnabled(true);
                getCurrLocation();
            }
            else {
                Toast.makeText(this, "This app requires location permission", Toast.LENGTH_SHORT).show();
                finish();
                //we don't have the permission
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        rr=intent.getDoubleExtra("rr",0);
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        enableUserLocation();
        getCurrLocation();
        mMap.setOnMapLongClickListener(this);
        //  mMap.addMarker(new MarkerOptions().position(userLocation).title("You"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,6));

    }
    private void getCurrLocation(){
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location !=null){
                    userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                   // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.getLatitude(),location.getLatitude()), 12));
                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,11.5f));
                }else{
                    //Not Found
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Error","Not Found");
            }
        });
    }

    private void enableUserLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //if permission for location is granted
            mMap.setMyLocationEnabled(true);
        }else{
            //if not granted ask for it
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                //show user a dialogue box that why you need a permission and them ask for it
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},fineRequestCode);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},fineRequestCode);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        AddMarker(latLng);
        AddCircle(latLng,rr);
        Double lt=latLng.latitude,lg=latLng.longitude;
        customizeTask.Latitude.setText(Double.toString(lt));
        customizeTask.Longitude.setText(Double.toString(lg));
        distance= SphericalUtil.computeDistanceBetween(userLocation,latLng);
        Toast.makeText(this,distance/1000+" km apart Location Saved !",Toast.LENGTH_LONG).show();
    }
    private void AddMarker(LatLng latLng){
        MarkerOptions markerOptions=new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
        // Toast.makeText(this,"Location Saved !",Toast.LENGTH_LONG);
    }
    private void AddCircle(LatLng latLng,double radius){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}