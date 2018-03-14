package abass.com.firebasepushnotifications;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends android.support.v4.app.Fragment {

    private Button mLogOutBtn;
    private Button mSendNotificationBtn;

    private String mCurrentID;
    private String mCurrentName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private DatabaseReference mDatabase;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth= FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentID = mAuth.getUid();

        mfirestore.collection("Users").document(mCurrentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mCurrentName = documentSnapshot.get("name").toString();
            }
        });

        mLogOutBtn = (Button) view.findViewById(R.id.logOutBtn);
        mSendNotificationBtn =(Button) view.findViewById(R.id.sendNotifications);

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String , Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id", FieldValue.delete());

                mfirestore.collection("Users").document(mCurrentID).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.signOut();
                        Intent LoginIntent = new Intent(container.getContext(), LoginActivity.class);
                        startActivity(LoginIntent);

                    }
                });

            }
        });

        mSendNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Message = "Help Me Please !!!!";
                mfirestore.collection("Users").addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                            if(doc.getType()== DocumentChange.Type.ADDED){
                                String user_id = doc.getDocument().getId();
                                User temp_user = doc.getDocument().toObject(User.class);

                                if(user_id.equals(mCurrentID)){
                                    continue;
                                }

                                Map<String , Object> notificationMessage = new HashMap<>();
                                notificationMessage.put("message", Message);
                                notificationMessage.put("from", mCurrentID);
                                notificationMessage.put("user_name", mCurrentName);

                                mfirestore.collection("Users/"+user_id+"/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(container.getContext(),"Notifications Sent ",Toast.LENGTH_LONG).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(container.getContext(),"Error :  "+ e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }
                    }
                });

            }
        });
        return view;
    }

}
