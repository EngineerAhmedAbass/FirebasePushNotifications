package abass.com.firebasepushnotifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import abass.com.firebasepushnotifications.Maps.AppController;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MyBackgroundService extends Service implements ConnectivityReceiver.ConnectivityReceiverListener {
    public boolean Updated;

    static public String longtitude, latitude;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL =  5*60*1000;  /* 5 Minutes */
    private long FASTEST_INTERVAL = 1*05*1000; /* 5 Seconds */
    LocationManager locationManager;

    static public String mCurrentID;
    static public String mCurrentName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;


    Handler handler = new Handler();
    private static final String TAG = "MyBackgroundService";

    public MyBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service is On.....");

        scheduleSendLocation();
        AppController.getInstance().setConnectivityListener(this);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mCurrentID = mAuth.getCurrentUser().getUid();
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("Users").document(mCurrentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mCurrentName = documentSnapshot.get("name").toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Useeeeer Name ",e.getMessage());
                }
            });
        }
        startLocationUpdates();
    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                startLocationUpdates();
                Log.e(TAG, "User ID is ==> "+mCurrentID);
                changeLocation();
                handler.postDelayed(this, 30*60*1000);
            }
        }, 30*60*1000);
    }

    public void changeLocation(){
        if(isNetworkAvailable()){
            Log.e(TAG, longtitude+" "+latitude);
            UpdateCurrentLocation();
        }else{
            Log.e(TAG, "Location Had To Be Updated");
            Updated = false;
        }
    }

    private void UpdateCurrentLocation() {
        if(mCurrentID != null){
            mFirestore = FirebaseFirestore.getInstance();
            Map<String, Object> UpdatedLocation = new HashMap<>();
            UpdatedLocation.put("latitude",latitude);
            UpdatedLocation.put("longtitude",longtitude);
            mFirestore.collection("Users").document(mCurrentID).update(UpdatedLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Updated = true;
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isLocationServiceEnabled(){
        boolean gps_enabled= false;

        if(locationManager ==null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled ;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.e(TAG, "Connection Changed.....");
        if(isConnected && Updated==false){
            Log.e(TAG, "Location Updated In DataBase.....");
            UpdateCurrentLocation();
        }
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged...");
        // New location has now been determined
        longtitude = Double.toString(location.getLongitude());
        latitude = Double.toString(location.getLatitude());
        Log.e(TAG, "New Location ... "+longtitude+" "+latitude );
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

}
