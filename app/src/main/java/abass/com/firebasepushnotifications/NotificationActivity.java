package abass.com.firebasepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NotificationActivity extends AppCompatActivity {

    private TextView fromText ;
    private TextView DomainTxt;
    private TextView MessageTxt;
    private TextView placeTxt ;
    private TextView Status ;
    private Button sendRespond;
    private String Message;
    private String longt, lati;

    private String mCurrentID;
    private String mCurrentName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;

    LocationManager locationManager;
    private FusedLocationProviderClient client;
    private static GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Vector<String> SentUsers = new Vector<>();

    String dataMessage ;
    String dataFrom ;
    String longtitude = null ;
    String latitude = null ;
    String Domain;
    String request_id= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        dataMessage = getIntent().getStringExtra("message");
        dataFrom = getIntent().getStringExtra("from_user_id");
        longtitude = getIntent().getStringExtra("longtitude");
        latitude = getIntent().getStringExtra("latitude");
        Domain = getIntent().getStringExtra("domain");
        request_id =getIntent().getStringExtra("request_id");

        String LocationUEL = "http://maps.google.com/maps?q="+latitude+","+longtitude;

        fromText = (TextView) findViewById(R.id.from_txt);
        DomainTxt = (TextView) findViewById(R.id.domain_txt);
        MessageTxt = (TextView) findViewById(R.id.message);
        placeTxt = (TextView) findViewById(R.id.place);
        Status = (TextView) findViewById(R.id.status);
        sendRespond = (Button) findViewById(R.id.respondButton);

        client = LocationServices.getFusedLocationProviderClient(NotificationActivity.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(NotificationActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        requestPermission();

        mAuth = FirebaseAuth.getInstance();

        new GetStatus().execute(Integer.parseInt(request_id));

        placeTxt.setClickable(true);
        placeTxt.setMovementMethod(LinkMovementMethod.getInstance());
        placeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longtitude);

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });
        fromText.setText(dataFrom);
        DomainTxt.setText(Domain);
        MessageTxt.setText(dataMessage);
        placeTxt.setText(LocationUEL);

        sendRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationServiceEnabled()){
                    if(isNetworkAvailable()){
                        if (ActivityCompat.checkSelfPermission( NotificationActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(NotificationActivity.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        client.getLastLocation().addOnSuccessListener( NotificationActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    longt = ""+location.getLongitude();
                                    lati = ""+location.getLatitude();
                                }
                            }
                        });
                        SendNotificationsRespond();
                    }else{
                        Toast.makeText(NotificationActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(NotificationActivity.this, "Location Is Disabled.", Toast.LENGTH_SHORT).show();
                    showSettingDialog();
                }
                new changeState().execute(Integer.parseInt(request_id));
            }
        });

    }

    private void SendNotificationsRespond() {
        mfirestore = FirebaseFirestore.getInstance();
        mCurrentID = mAuth.getUid();

        mfirestore.collection("Users").document(mCurrentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mCurrentName = documentSnapshot.get("name").toString();
            }
        });

        Message = "I'm willing to help";

        if (Message.equals("")){
            Toast.makeText(NotificationActivity.this,"من فضلك ادخل معلومات عن طلب المساعدة",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mCurrentID == null || mCurrentName == null || Domain== null || longtitude == null || latitude == null)
        {
            Toast.makeText(NotificationActivity.this,"Something Went Wrong Please Try Again...",Toast.LENGTH_SHORT).show();
            return;
        }
        new changeState().execute(Integer.parseInt(request_id));
        Toast.makeText(NotificationActivity.this,"The Help Request Sent ",Toast.LENGTH_SHORT).show();
    }

    class GetStatus extends AsyncTask<Integer ,Void,String>{

        @Override
        protected String doInBackground(Integer... integers) {
            String url = "http://refadatours.com/android/getStatus.php?id="+integers[0];
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
        protected void onPostExecute(String feed) {
            if(feed.equals("waiting")){
                sendRespond.setVisibility(View.VISIBLE);
            }
            Status.setText(feed);
        }
    }
    class changeState extends AsyncTask<Integer ,Void,String>{
        @Override
        protected String doInBackground(Integer... integers) {
            String url = "http://refadatours.com/android/changeState.php?id="+integers[0];
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
        protected void onPostExecute(final String feed) {
            sendRespond.setVisibility(View.INVISIBLE);
            Status.setText("closed");

            mfirestore.collection("Users").addSnapshotListener(NotificationActivity.this,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                        if(doc.getType()== DocumentChange.Type.ADDED){
                            String user_id = doc.getDocument().getId();
                            User temp_user = doc.getDocument().toObject(User.class);

                            if(user_id.equals(feed)) {
                                SentUsers.add(user_id);
                            }
                        }

                    }
                }
            });

            for(int i=0 ; i < SentUsers.size();i++){
                Map<String , Object> notificationMessage = new HashMap<>();
                notificationMessage.put("message", Message);
                notificationMessage.put("from", mCurrentID);
                notificationMessage.put("user_name", mCurrentName);
                notificationMessage.put("domain", Domain);
                notificationMessage.put("longtitude",longt);
                notificationMessage.put("latitude",lati);
                notificationMessage.put("requestID",null);

                mfirestore.collection("Users/"+SentUsers.elementAt(i)+"/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationActivity.this,"Error :  "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Toast.makeText(NotificationActivity.this,"Respond Message Sent ",Toast.LENGTH_SHORT).show();
        }
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
                final com.google.android.gms.common.api.Status status = result.getStatus();
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
                            status.startResolutionForResult(NotificationActivity.this, REQUEST_CHECK_SETTINGS);
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
                            if (ActivityCompat.checkSelfPermission( NotificationActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(NotificationActivity.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            client.getLastLocation().addOnSuccessListener( NotificationActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null){
                                        longt = ""+location.getLongitude();
                                        lati = ""+location.getLatitude();
                                    }
                                }
                            });
                            SendNotificationsRespond();
                        }else{
                            Toast.makeText(NotificationActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(NotificationActivity.this, "No...", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(NotificationActivity.this,new String[]{ACCESS_FINE_LOCATION},1);
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
}
