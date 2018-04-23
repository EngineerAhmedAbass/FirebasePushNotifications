package abass.com.firebasepushnotifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import abass.com.firebasepushnotifications.Maps.MainMap;
import abass.com.firebasepushnotifications.Request.HelpRequest;
import abass.com.firebasepushnotifications.Request.LoginActivity;
import abass.com.firebasepushnotifications.Request.bloodDonationRequest;
import abass.com.firebasepushnotifications.Sos.SosActivity;

public class Home extends AppCompatActivity {
    public Button Help_Request_BTN;
    public Button Blood_Donor_BTN;
    public Button SOS_BTN;
    public Button Places_BTN;
    public Button First_Aid_BTN;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Home.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        Help_Request_BTN = findViewById(R.id.help_request_Btn);
        Blood_Donor_BTN = findViewById(R.id.Blood_BTN);
        SOS_BTN = findViewById(R.id.SOS_BTN);
        Places_BTN = findViewById(R.id.Places_BTN);
        First_Aid_BTN = findViewById(R.id.First_Aid_BTN);
        Toolbar toolbar = findViewById(R.id.app_bar);
        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Help_Request_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent HelpIntent = new Intent(Home.this, HelpRequest.class);
                startActivity(HelpIntent);
            }
        });
        Blood_Donor_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bloodIntent = new Intent(Home.this, bloodDonationRequest.class );
                startActivity(bloodIntent);
            }
        });
        SOS_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent HelpIntent = new Intent(Home.this, SosActivity.class);
                startActivity(HelpIntent);
            }
        });
        Places_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MapsIntnet = new Intent(Home.this, MainMap.class);
                startActivity(MapsIntnet);
            }
        });
        First_Aid_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Go To First Aid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.notification:
                Intent GoToNotifications = new Intent(Home.this, ShowNotifications.class);
                startActivity(GoToNotifications);
                 break;
            case R.id.settings:
                Intent settings = new Intent(Home.this, SettingsActivity.class);
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
        if(isNetworkAvailable()) {
            Intent myService = new Intent(Home.this, MyBackgroundService.class);
            stopService(myService);
            Map<String, Object> tokenMapRemove = new HashMap<>();
            tokenMapRemove.put("token_id", FieldValue.delete());
            String mCurrentID = mAuth.getCurrentUser().getUid();
            mfirestore.collection("Users").document(mCurrentID).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.signOut();
                    Intent LoginIntent = new Intent(Home.this, LoginActivity.class);
                    startActivity(LoginIntent);
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser == null) {
            sendToLogin();
        }else{
            mfirestore = FirebaseFirestore.getInstance();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(Home.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
