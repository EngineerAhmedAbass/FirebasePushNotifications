package abass.com.firebasepushnotifications.Request;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.HashMap;
import java.util.Map;

import abass.com.firebasepushnotifications.Home;
import abass.com.firebasepushnotifications.MyBackgroundService;
import abass.com.firebasepushnotifications.R;

public class  LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginBtn;
    private Button mRegPageBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private android.support.v4.widget.ContentLoadingProgressBar loginProgBar;
    PermissionManager permissionManager;
    MyBackgroundService myBackgroundService=new MyBackgroundService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        mEmail = (EditText) findViewById(R.id.edemail);
        mPassword = (EditText) findViewById(R.id.edpassword);
        mLoginBtn = (Button) findViewById(R.id.btnLogin);
        mRegPageBtn = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginProgBar =(android.support.v4.widget.ContentLoadingProgressBar) findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mRegPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgBar.setVisibility(View.VISIBLE);
                String email = mEmail.getText().toString();
                String Password = mPassword.getText().toString();
                if(email.equals("")){
                    loginProgBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,"Error : You Must Enter Your Email",Toast.LENGTH_SHORT).show();
                }else if (Password.equals("")){
                    loginProgBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,"Error :  You Must Enter Your Password",Toast.LENGTH_SHORT).show();
                }else if(!isNetworkAvailable()){
                    loginProgBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,"Error :  Check Your Internet Connection",Toast.LENGTH_SHORT).show();

                }else{
                mAuth.signInWithEmailAndPassword(email,Password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    String token_Id = FirebaseInstanceId.getInstance().getToken();
                                    String current_Id = mAuth.getCurrentUser().getUid();
                                    myBackgroundService.mCurrentID=current_Id;
                                    Map<String, Object> tokenMap = new HashMap<>();
                                    tokenMap.put("token_id",token_Id);
                                    mFirestore.collection("Users").document(current_Id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SendToMain();
                                        }
                                    });

                                } else {
                                    loginProgBar.setVisibility(View.INVISIBLE);
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this,"Error : "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            }
        });

}

    private void SendToMain() {
        loginProgBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoginActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}