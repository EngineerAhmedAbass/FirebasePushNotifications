package abass.com.firebasepushnotifications.Request;

import android.app.Notification;
import android.app.ProgressDialog;
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
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import abass.com.firebasepushnotifications.Home;
import abass.com.firebasepushnotifications.MyBackgroundService;
import abass.com.firebasepushnotifications.R;
import abass.com.firebasepushnotifications.SettingsActivity;
import abass.com.firebasepushnotifications.ShowNotifications;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class NotificationActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    String dataMessage;
    String dataFrom;
    String longtitude = null;
    String latitude = null;
    String Domain;
    String request_id = null;
    String type;

    ProgressDialog progressDialog;

    private Toolbar toolbar;
    private TextView fromText;
    private TextView DomainTxt;
    private TextView MessageTxt;
    private TextView placeTxt;
    private TextView Status;
    private Button sendRespond;
    private String Message;
    private String mCurrentID;
    private String mCurrentName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Responding");
        progressDialog.setMessage("Please Wait till Response Completes");
        dataMessage = getIntent().getStringExtra("message");
        dataFrom = getIntent().getStringExtra("from_user_id");
        longtitude = getIntent().getStringExtra("longtitude");
        latitude = getIntent().getStringExtra("latitude");
        Domain = getIntent().getStringExtra("domain");
        request_id = getIntent().getStringExtra("request_id");
        type = getIntent().getStringExtra("type");

        String LocationUEL = "http://maps.google.com/maps?q=" + latitude + "," + longtitude;

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(Domain);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getTitle());
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

        mAuth = FirebaseAuth.getInstance();

        placeTxt.setClickable(true);
        placeTxt.setMovementMethod(LinkMovementMethod.getInstance());
        placeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longtitude);

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

        if (!type.equals("response")) {
            if (isLocationServiceEnabled()) {
                if (isNetworkAvailable()) {
                } else {
                    Toast.makeText(NotificationActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(NotificationActivity.this, "Location Is Disabled.", Toast.LENGTH_SHORT).show();
                showSettingDialog();
            }

            new GetStatus().execute(Integer.parseInt(request_id));
            sendRespond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendRespond.setClickable(false);
                    if (isLocationServiceEnabled()) {
                        if (isNetworkAvailable()) {
                            progressDialog.show();
                            SendNotificationsRespond();
                        } else {
                            sendRespond.setClickable(true);
                            Toast.makeText(NotificationActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        sendRespond.setClickable(true);
                        Toast.makeText(NotificationActivity.this, "Location Is Disabled.", Toast.LENGTH_SHORT).show();
                        showSettingDialog();
                    }
                }
            });
        } else {
            Status.setText("response");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification:
                Intent GoToNotifications = new Intent(NotificationActivity.this, ShowNotifications.class);
                startActivity(GoToNotifications);
                break;
            case R.id.settings:
                Intent settings = new Intent(NotificationActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.log_out:
                Log_Out();
                break;
            default:


        }
        return super.onOptionsItemSelected(item);
    }

    private void Log_Out() {
        Intent myService = new Intent(NotificationActivity.this, MyBackgroundService.class);
        stopService(myService);
        Map<String, Object> tokenMapRemove = new HashMap<>();
        tokenMapRemove.put("token_id", FieldValue.delete());
        String mCurrentID = mAuth.getCurrentUser().getUid();
        mfirestore.collection("Users").document(mCurrentID).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mAuth.signOut();
                Intent LoginIntent = new Intent(NotificationActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser == null) {
            sendToLogin();
        } else {
            mfirestore = FirebaseFirestore.getInstance();
            mCurrentID = mAuth.getUid();
            mfirestore.collection("Users").document(mCurrentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mCurrentName = documentSnapshot.get("name").toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NotificationActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void SendNotificationsRespond() {
        mfirestore = FirebaseFirestore.getInstance();
        Message = "I'm willing to help";
        new changeState().execute(Integer.parseInt(request_id));
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
                        if (isNetworkAvailable()) {
                            if (ActivityCompat.checkSelfPermission(NotificationActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(NotificationActivity.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(NotificationActivity.this, "Location Enabled .", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NotificationActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(NotificationActivity.this, "Location Disabled...", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    public boolean isLocationServiceEnabled() {
        boolean gps_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        return gps_enabled;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendToLogin() {
        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    class GetStatus extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            String url = "http://refadatours.com/android/getStatus.php?id=" + integers[0];
            HttpEntity httpEntity = null;
            try {
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
            return entityResponse;
        }

        protected void onPostExecute(String feed) {
            if (feed.equals("waiting")) {
                sendRespond.setVisibility(View.VISIBLE);
            }
            Status.setText(feed);
        }
    }

    class changeState extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            String url = "http://refadatours.com/android/changeState.php?id=" + integers[0];
            HttpEntity httpEntity = null;
            try {
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
            return entityResponse;
        }

        protected void onPostExecute(final String feed) {
            Status.setText("closed");
            MyBackgroundService myBackgroundService= new MyBackgroundService();
            Date currentTime = Calendar.getInstance().getTime();
            Map<String, Object> notificationMessage = new HashMap<>();
            notificationMessage.put("message", Message);
            notificationMessage.put("from", mCurrentID);
            notificationMessage.put("user_name", mCurrentName);
            notificationMessage.put("domain", Domain + " (Response)");
            notificationMessage.put("longtitude", myBackgroundService.longtitude);
            notificationMessage.put("latitude", myBackgroundService.latitude);
            notificationMessage.put("requestID", request_id);
            notificationMessage.put("type", "response");
            notificationMessage.put("date", currentTime);
            mfirestore.collection("Users/" + feed + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    sendRespond.setVisibility(View.INVISIBLE);
                    progressDialog.hide();
                    Toast.makeText(NotificationActivity.this, "Respond Sent ", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.hide();
                    sendRespond.setClickable(true);
                    Toast.makeText(NotificationActivity.this, "Error :  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
