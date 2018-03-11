package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullname,email , password ,phone , city , street , nid ;
    private String  day  , month , year ;
    private Button mRegBtn , mLoginPageBtn;
    private ProgressBar mregisterprogressbar;

    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


        fullname = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        phone= (EditText) findViewById(R.id.Phone);
        city= (EditText) findViewById(R.id.city);
        street= (EditText) findViewById(R.id.street);
        nid= (EditText) findViewById(R.id.NID);
        mRegBtn = (Button) findViewById(R.id.btnRegister);
        mLoginPageBtn = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mregisterprogressbar = (ProgressBar) findViewById(R.id.registerprogressbar);



        mLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Myname = fullname.getText().toString();
                String myemail = email.getText().toString();
                String myPassword = password.getText().toString();
                final String Myphone = phone.getText().toString();
                if(!TextUtils.isEmpty(Myname) && !TextUtils.isEmpty(myemail) && !TextUtils.isEmpty(myPassword)){
                    mAuth.createUserWithEmailAndPassword(myemail,myPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mregisterprogressbar.setVisibility(View.VISIBLE);
                                String User_id = mAuth.getCurrentUser().getUid();
                                Map<String,Object> userMap= new HashMap<>();
                                userMap.put("name",Myname);
                                userMap.put("phone",Myphone);
                                mFirestore.collection("Users").document(User_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SendToMain();
                                    }
                                });

                            }else{
                                mregisterprogressbar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this,"Error : "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

    }

        private void SendToMain() {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
    }
}
