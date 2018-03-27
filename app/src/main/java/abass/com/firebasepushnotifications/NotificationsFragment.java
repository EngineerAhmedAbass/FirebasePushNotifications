package abass.com.firebasepushnotifications;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends android.support.v4.app.Fragment {

    private RecyclerView mNotificationsListView;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String mCurrentID;

    private Context context;

    private List<MyNotification> notificationsList;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_notifications, container, false);

        context = container.getContext();

        mFirestore = FirebaseFirestore.getInstance();

        mNotificationsListView = (RecyclerView) view.findViewById(R.id.notifications_l);

        notificationsList = new ArrayList<>();

        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(container.getContext(),notificationsList);

        mNotificationsListView.setHasFixedSize(true);
        mNotificationsListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mNotificationsListView.setAdapter(notificationsRecyclerAdapter);

        mAuth = FirebaseAuth.getInstance();

        return view;
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

        mFirestore.collection("Users").document(mCurrentID).collection("Notifications").addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
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
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }
}
