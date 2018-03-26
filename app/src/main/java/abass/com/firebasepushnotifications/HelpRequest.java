package abass.com.firebasepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HelpRequest extends AppCompatActivity {

    private Spinner spinner;
    private EditText requestText;
    private Button SendRequestBtn;
    private Button mLogOutBtn;
    private String Message;
    private String Domain;
    private int RequestID;
    private String longtitude, latitude;

    private String mCurrentID;
    private String mCurrentName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;

    LocationManager locationManager;
    private FusedLocationProviderClient client;
    private static GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Vector<String> SentUsers = new Vector<>();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser == null ){
            sendToLogin();
        }else{
            mfirestore = FirebaseFirestore.getInstance();
            mCurrentID = mAuth.getUid();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_request);

        /*  Start Spinner Code */
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        /*  End Spinner Code */

        requestText = (EditText) findViewById(R.id.text_help);
        SendRequestBtn=(Button) findViewById(R.id.sendrequest);
        mLogOutBtn = (Button) findViewById(R.id.logOutBtn);

        client = LocationServices.getFusedLocationProviderClient(HelpRequest.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(HelpRequest.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        requestPermission();

        mAuth = FirebaseAuth.getInstance();

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id", FieldValue.delete());

                mfirestore.collection("Users").document(mCurrentID).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.signOut();
                        Intent LoginIntent = new Intent(HelpRequest.this, LoginActivity.class);
                        startActivity(LoginIntent);
                    }
                });

            }
        });

        SendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationServiceEnabled()){
                    if(isNetworkAvailable()){
                        if (ActivityCompat.checkSelfPermission( HelpRequest.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(HelpRequest.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        client.getLastLocation().addOnSuccessListener( HelpRequest.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    longtitude = ""+location.getLongitude();
                                    latitude = ""+location.getLatitude();
                                }
                            }
                        });
                        SendNotifications();
                    }else{
                        Toast.makeText(HelpRequest.this, "No Internet.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HelpRequest.this, "Location Is Disabled.", Toast.LENGTH_SHORT).show();
                    showSettingDialog();
                }

            }
        });

    }
    void SendNotifications(){
        mfirestore = FirebaseFirestore.getInstance();
        mCurrentID = mAuth.getUid();

        mfirestore.collection("Users").document(mCurrentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mCurrentName = documentSnapshot.get("name").toString();
            }
        });

        Message = requestText.getText().toString();
        Domain = spinner.getSelectedItem().toString();

        if (Message == null){
            Toast.makeText(HelpRequest.this,"من فضلك ادخل معلومات عن طلب المساعدة",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mCurrentID == null || mCurrentName == null || Domain== null || longtitude == null || latitude == null)
        {
            Toast.makeText(HelpRequest.this,"Something Went Wrong Please Try Again...",Toast.LENGTH_SHORT).show();
            return;
        }

        mfirestore.collection("Users").addSnapshotListener(HelpRequest.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if(doc.getType()== DocumentChange.Type.ADDED){
                        String user_id = doc.getDocument().getId();
                        User temp_user = doc.getDocument().toObject(User.class);

                        if(temp_user.getToken_id() == null || user_id.equals(mCurrentID)){
                            continue;
                        }
                        double Dist = distance(Double.parseDouble(latitude),Double.parseDouble(longtitude),Double.parseDouble(temp_user.getLatitude()),Double.parseDouble(temp_user.getLongtitude()));
                        if(Dist > 10){
                            continue;
                        }
                        SentUsers.add(user_id);
                    }

                }
            }
        });
        new GetRequestID().execute(SentUsers);
        Toast.makeText(HelpRequest.this,"The Help Request Sent ",Toast.LENGTH_SHORT).show();
    }
    class GetRequestID extends AsyncTask<Vector<String>, Void, String> {
        Vector<String> user_ids ;
        protected String doInBackground(Vector<String>... strings) {
            String url = "http://refadatours.com/android/addRequest.php?message="+Message;
            user_ids=strings[0];
            HttpEntity httpEntity = null;
            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String entityResponse = null;
            try {
                entityResponse = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  entityResponse ;
        }

        protected void onPostExecute(String RequestID) {
            for(int i=0 ; i < user_ids.size();i++){
                Map<String , Object> notificationMessage = new HashMap<>();
                notificationMessage.put("message", Message);
                notificationMessage.put("from", mCurrentID);
                notificationMessage.put("user_name", mCurrentName);
                notificationMessage.put("domain", Domain);
                notificationMessage.put("longtitude",longtitude);
                notificationMessage.put("latitude",latitude);
                notificationMessage.put("requestID",RequestID);

                mfirestore.collection("Users/"+user_ids.elementAt(i)+"/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HelpRequest.this,"Error :  "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(HelpRequest.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(HelpRequest.this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HelpRequest.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        if(isNetworkAvailable()){
                            if (ActivityCompat.checkSelfPermission( HelpRequest.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(HelpRequest.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            client.getLastLocation().addOnSuccessListener( HelpRequest.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null){
                                        longtitude = ""+location.getLongitude();
                                        latitude = ""+location.getLatitude();
                                    }
                                }
                            });
                            SendNotifications();
                        }else{
                            Toast.makeText(HelpRequest.this, "No Internet.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(HelpRequest.this, "No...", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        dist = dist / 1000;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
