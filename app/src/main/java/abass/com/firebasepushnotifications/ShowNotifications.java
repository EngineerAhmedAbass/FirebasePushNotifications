package abass.com.firebasepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import abass.com.firebasepushnotifications.Request.LoginActivity;
import abass.com.firebasepushnotifications.Request.MyNotification;
import abass.com.firebasepushnotifications.Request.NotificationsRecyclerAdapter;

public class ShowNotifications extends AppCompatActivity {
    private RecyclerView mNotificationsListView;
    private Toolbar toolbar;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String mCurrentID;

    private Context context;

    private List<MyNotification> notificationsList;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notifications);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();

        mNotificationsListView = (RecyclerView) findViewById(R.id.notifications_l);

        notificationsList = new ArrayList<>();

        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(this,notificationsList);

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

        notificationsList.clear();
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
                        notificationsList.add(notifications);

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
}
