package abass.com.firebasepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import abass.com.firebasepushnotifications.Request.LoginActivity;
import abass.com.firebasepushnotifications.Request.MyNotification;
import abass.com.firebasepushnotifications.Request.NotificationsRecyclerAdapter;

public class ShowNotifications extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView mNotificationsListView;
    private Toolbar toolbar;
    private Spinner Sorting_Spiner;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String mCurrentID;

    private Context context;
    private List<MyNotification> notificationsList_Displayed;
    private List<MyNotification> notificationsList_Blood_Request;
    private List<MyNotification> notificationsList_Help_Request;
    private List<MyNotification> notificationsList_Responces;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;

    private String Test;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("Test","............. OnSave .......");
        outState.putString("test", "Welcome back to Activity");
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e("Test","............. OnRestore .......");
        super.onRestoreInstanceState(savedInstanceState);
        Test = savedInstanceState.getString("test");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Test","............. OnDestroy .......");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("test","Hellooooooooooooo");
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Test","............. OnCreate .......");
        setContentView(R.layout.activity_show_notifications);



        /*  Start Spinner Code */

        Sorting_Spiner =(Spinner) findViewById(R.id.sorting_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_method, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        Sorting_Spiner.setAdapter(adapter);

        /*  End Spinner Code */

        Sorting_Spiner.setOnItemSelectedListener(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Notifications");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Test=settings.getString("test","Null");

        Toast.makeText(this, Test, Toast.LENGTH_SHORT).show();
        mFirestore = FirebaseFirestore.getInstance();

        mNotificationsListView = (RecyclerView) findViewById(R.id.notifications_l);

        notificationsList_Displayed = new ArrayList<>();
        notificationsList_Help_Request = new ArrayList<>();
        notificationsList_Blood_Request = new ArrayList<>();
        notificationsList_Responces = new ArrayList<>();

        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(this, notificationsList_Displayed);

        mNotificationsListView.setHasFixedSize(true);
        mNotificationsListView.setLayoutManager(new LinearLayoutManager(this));
        mNotificationsListView.setAdapter(notificationsRecyclerAdapter);
        mAuth = FirebaseAuth.getInstance();
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
                Intent GoToNotifications = new Intent(this, ShowNotifications.class);
                startActivity(GoToNotifications);
                break;
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        notificationsList_Displayed.clear();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser == null ){
            sendToLogin();
        }else{
            mFirestore = FirebaseFirestore.getInstance();
            mCurrentID = mAuth.getUid();
        }

        mFirestore.collection("Users").document(mCurrentID).collection("Notifications").addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String Notification_Id = doc.getDocument().getId();
                        MyNotification notifications = doc.getDocument().toObject(MyNotification.class).withId(Notification_Id);
                        MyBackgroundService myBackgroundService =new MyBackgroundService();
                        double Dist = distance(Double.parseDouble(myBackgroundService.latitude), Double.parseDouble(myBackgroundService.longtitude), Double.parseDouble(notifications.getLatitude()), Double.parseDouble(notifications.getLongtitude()));
                        notifications.setDistance(Dist);
                        if(notifications.getType().equals("Request")){
                            if(notifications.getDomain().equals("تبرع بالدم")){
                                notificationsList_Blood_Request.add(notifications);
                            }else{
                                notificationsList_Help_Request.add(notifications);
                            }
                        }else{
                            notificationsList_Responces.add(notifications);
                        }
                        notificationsList_Displayed.add(notifications);
                        notificationsRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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

    public class Custom_Distace_Comparator implements Comparator<MyNotification> {
        @Override
        public int compare(MyNotification o1, MyNotification o2) {
            if(o1.getDistance() == o2.getDistance()){
                return 0;
            }else if(o1.getDistance() > o2.getDistance()){
                return 1;
            }else{
                return -1;
            }
        }
    }
    public class Custom_Date_Comparator implements Comparator<MyNotification> {
        @Override
        public int compare(MyNotification o1, MyNotification o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String Selected_sort = adapterView.getItemAtPosition(i).toString();
        if(Selected_sort.equals("Select Sorting Method")){
            Toast.makeText(this, "Please Choose Sorting Method", Toast.LENGTH_SHORT).show();
        }else if(Selected_sort.equals("Ascending by Distance")){
            Collections.sort(notificationsList_Displayed,new Custom_Distace_Comparator());
        }else if(Selected_sort.equals("Descending by Distance")){
            Collections.sort(notificationsList_Displayed,new Custom_Distace_Comparator());
            Collections.reverse(notificationsList_Displayed);
        }
        else if(Selected_sort.equals("Ascending by Time")){
            Collections.sort(notificationsList_Displayed,new Custom_Date_Comparator());
        }else if(Selected_sort.equals("Descending by Time")){
            Collections.sort(notificationsList_Displayed,new Custom_Date_Comparator());
            Collections.reverse(notificationsList_Displayed);
        }
        notificationsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
