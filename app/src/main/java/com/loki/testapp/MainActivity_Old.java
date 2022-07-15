package com.loki.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.loki.testapp.databinding.ActivityMainBinding;
import com.loki.testapp.helpers.LocationHelper;
import com.loki.testapp.model.Forecast;
import com.loki.testapp.network.RestApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity_Old extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getCanonicalName();
    ActivityMainBinding binding;
    private LocationHelper locationHelper;
    private Location lastLocation;
    private LocationCallback locationCallback;

    private  RestApiClient restApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restApiClient = new RestApiClient();

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        this.binding.btnReverseGeocoding.setOnClickListener(this::onClick);
        this.binding.btnOpenMap.setOnClickListener(this::onClick);

        this.locationHelper = LocationHelper.getInstance();
        this.locationHelper.checkPermissions(this);

//        this.lastLocation = this.locationHelper.getLastLocation(this);
//        if (this.lastLocation != null){
//            this.binding.tvLocationAddress.setText(this.lastLocation.toString());
//        }else{
//            this.binding.tvLocationAddress.setText("Last location not obtained");
//        }

        this.locationHelper.getLastLocation(this).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null){
                    lastLocation = location;
//                    binding.tvLocationAddress.setText(lastLocation.toString());
                    Address obtainedAddress = locationHelper.performForwardGeocoding(getApplicationContext(), lastLocation);

                    if (obtainedAddress != null){


                        restApiClient.fetchWeatherForecast(lastLocation.getLongitude(), lastLocation.getLatitude(), new Callback<Forecast>() {
                            @Override
                            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                                Forecast forecast = response.body();
                                if(forecast != null)
                                {
                                    Log.w("PrintDa", forecast.getCurrent().getWindDir());
                                    binding.tvRegion.setText(forecast.getLocation().getRegion());
                                    binding.tvCountry.setText(forecast.getLocation().getCountry());
                                    binding.tvName.setText(forecast.getCurrent().getCondition().getText());

                                    binding.tvTemprature.setText(forecast.getCurrent().getTempC()+" C");
                                    binding.tvFeellike.setText(forecast.getCurrent().getFeelslikeC()+" C");
                                    binding.tvWind.setText(forecast.getCurrent().getWindKph()+" Kph");
                                    binding.tvWindDirection.setText(forecast.getCurrent().getWindDir());
                                    binding.tvHumidity.setText(forecast.getCurrent().getHumidity() +" %");
                                    binding.tvUvindex.setText(forecast.getCurrent().getUv() +"");
                                    binding.tvVisibility.setText(forecast.getCurrent().getVisKm()+" Km");

                                    Log.w("Lat<>",forecast.getLocation().getLat()+"<>");
                                    Log.w("Lat",forecast.getLocation().getLon()+"<>");

                                }

                            }

                            @Override
                            public void onFailure(Call<Forecast> call, Throwable t) {
                             //   hideProgressBar();
                            }
                        });
                        binding.tvLocationAddress.setText(obtainedAddress.getAddressLine(0));
                    }else{
                        binding.tvLocationAddress.setText(lastLocation.toString());
                    }
                }else{
                    binding.tvLocationAddress.setText("Last location not obtained");
                }
            }
        });

        this.initiateLocationListener();
    }

    private void initiateLocationListener(){
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for(Location loc : locationResult.getLocations()){
                    lastLocation = loc;

                    Address obtainedAddress = locationHelper.performForwardGeocoding(getApplicationContext(), lastLocation);

                    if (obtainedAddress != null){
                        binding.tvLocationAddress.setText(obtainedAddress.getAddressLine(0));
                    }else{
                        binding.tvLocationAddress.setText(lastLocation.toString());
                    }

                    //use the updated location

                    //call weather API with updated location
                }
            }
        };

        this.locationHelper.requestLocationUpdates(this, locationCallback);
    }

    @Override
    public void onClick(View v) {
        if (v != null){
            switch (v.getId()){
                case R.id.btn_open_map:{
                    this.openMap();
                    break;
                }
                case R.id.btn_reverse_geocoding:{
                    this.doReverseGeocoding();
                    break;
                }
            }
        }
    }

    private void openMap(){
        Log.d(TAG, "onClick: Open map to show location");
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("EXTRA_LAT", this.lastLocation.getLatitude());
        mapIntent.putExtra("EXTRA_LNG", this.lastLocation.getLongitude());
        startActivity(mapIntent);
    }

    private void doReverseGeocoding(){
        Log.d(TAG, "onClick: Perform reverse geocoding to get coordinates from address.");

        if (this.locationHelper.locationPermissionGranted){
            String givenAddress = this.binding.editAddress.getText().toString();
            LatLng obtainedCoordinates = this.locationHelper.performReverseGeocoding(this, givenAddress);

            if (obtainedCoordinates != null){
                this.binding.tvLocationCoordinates.setText("Lat : " + obtainedCoordinates.latitude + "\nLng : " + obtainedCoordinates.longitude);
            }else{
                this.binding.tvLocationCoordinates.setText("No coordinates obtained");
            }
        }else{
            this.binding.tvLocationCoordinates.setText("Couldn't get the coordinates for given address");
        }
    }

    private void doGeocoding(){

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.locationHelper.stopLocationUpdates(this, this.locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.locationHelper.stopLocationUpdates(this, this.locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.locationHelper.requestLocationUpdates(this, this.locationCallback);
    }
}