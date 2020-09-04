package com.example.dogwalker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {

    Context context;
    Location location;
    double latitude;
    double longitude;
    //이동 거리 계산 관련 변수
    Location lastKnownLocation;
    double distance=0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //10m
    private static final long MIN_TIME_BW_UPDATES = 1000;  //1분 (1000 * 60 * 1)
    protected LocationManager locationManager;

    //생성자 -> GpsTracker 객체가 생성되면 getLocation() 메소드가 실행된다
    public GpsTracker(Context context) {
        this.context = context;
        //getLocation 메소드는 locationManager를 통해 GPS와 네트워크가 사용가능한지 체크한다 . (isGPSEnabled, isNetworkEnabled)
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d("DeveloperLog", "GpsTracker : isGPSEnabled -> "+isGPSEnabled);
            Log.d("DeveloperLog", "GpsTracker : isNetworkEnabled -> "+isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                //GPS와 네트워크 사용불가
            } else {
                //GPS와 네트워크 사용가능
                //-> 위치에 접근할 수 있는 권한 을 획득한다. (hasFineLocationPermission, hasCoarseLocationPermission)
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                Log.d("DeveloperLog", "GpsTracker : hasFineLocationPermission -> "+hasFineLocationPermission);
                Log.d("DeveloperLog", "GpsTracker : hasCoarseLocationPermission -> "+hasCoarseLocationPermission);

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    ;
                } else
                    return null;

                //GPS 와 네트워크 중 무엇이 사용가능한지 체크하고
                //권한에 맞게 locationManager 를 사용하여 위치를 가져옴
                //getLatitude() ,getLongitude() 메소드를 통해 위도/경도 값을 얻어옴
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Log.d("DeveloperLog", "GpsTracker : "+e.toString());
        }

        return location;
    }

    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public void stopUsingGPS() {
        if(locationManager != null) {
            locationManager.removeUpdates(GpsTracker.this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        //앱 백그라운드 시에도 위치 정보 받아온다
        Log.d("DeveloperLog", "onLocationChanged() :"+location.getLongitude() + " / " +location.getLatitude());

        // Get the last location.
        if(lastKnownLocation==null) {
            lastKnownLocation = location;
            Log.d("DeveloperLog","Distance: null");
        }
        else {
            distance=lastKnownLocation.distanceTo(location);
            Log.d("DeveloperLog","Distance:"+distance);
            lastKnownLocation=location;

            Toast.makeText(this, distance+"", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("DeveloperLog", "onStatusChanged()");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("DeveloperLog", "onProviderEnabled()");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("DeveloperLog", "onProviderDisabled()");

    }
}
