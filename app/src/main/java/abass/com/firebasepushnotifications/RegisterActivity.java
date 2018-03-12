package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    DatePicker datePicker;
    String  day  , month , year ;
    private Button mRegBtn , mLoginPageBtn;
    private ProgressBar mregisterprogressbar;
    String Myname , myemail , myPassword , Myphone ,MyCity ,MyStreet ,MyNID ;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        datePicker = (DatePicker) findViewById(R.id.DOB);
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
                 Myname = fullname.getText().toString();
                 myemail = email.getText().toString();
                 myPassword = password.getText().toString();
                 Myphone = phone.getText().toString();
                 MyCity = city.getText().toString();
                 MyStreet = city.getText().toString();
                 MyNID = nid.getText().toString();
                 day = String.valueOf(datePicker.getDayOfMonth());
                 month =String.valueOf(datePicker.getMonth() + 1);
                 year =String.valueOf(datePicker.getYear());

                if(ValidateData()){
                    mAuth.createUserWithEmailAndPassword(myemail,myPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mregisterprogressbar.setVisibility(View.VISIBLE);
                                String User_id = mAuth.getCurrentUser().getUid();
                                Map<String,Object> userMap= new HashMap<>();
                                userMap.put("name",Myname);
                                userMap.put("email",myemail);
                                userMap.put("phone",Myphone);
                                userMap.put("city",MyCity);
                                userMap.put("street",MyStreet);
                                userMap.put("nid",MyNID);
                                userMap.put("day",day);
                                userMap.put("month",month);
                                userMap.put("year",year);
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

    private boolean ValidateData() {
            if(Myname == ""){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Name ",Toast.LENGTH_SHORT).show();
                return false;
            }else if (myemail == ""){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter an Email ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(myPassword == ""){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Password ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(MyNID == ""){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter Your National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(day == "" && month == "" && year==""){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter Your Date of Birth ",Toast.LENGTH_SHORT).show();
                return false;
            }
        return true;
    }

    private void SendToMain() {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
    }
}
