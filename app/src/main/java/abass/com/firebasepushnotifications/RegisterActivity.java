package abass.com.firebasepushnotifications;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullname,email , password ,phone , city , street , nid ;
    DatePicker datePicker;
    int  day  , month , year ;
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
                 day = datePicker.getDayOfMonth();
                 month =(datePicker.getMonth() + 1);
                 year =(datePicker.getYear());

                if(ValidateData()){
                    mAuth.createUserWithEmailAndPassword(myemail,myPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                ProgressDialog.show(RegisterActivity.this, "Registering", "Please Wait until Registering completes ");
                                mregisterprogressbar.setVisibility(View.VISIBLE);

                                String User_id = mAuth.getCurrentUser().getUid();
                                String Token_id = FirebaseInstanceId.getInstance().getToken();

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
                                userMap.put("token_id",Token_id);

                                mFirestore.collection("Users").document(User_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SendToMain();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
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
            if(Myname.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Name ",Toast.LENGTH_SHORT).show();
                return false;
            }else if (myemail.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter an Email ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(myPassword.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Password ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(MyNID.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter Your National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }else if((MyNID.length() != 14)){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter A Valid National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }
            int NIDYear  =  Integer.parseInt(MyNID.substring(0,1));
            int NIDBYear =  Integer.parseInt(MyNID.substring(1,3));
            int NIDMonth =  Integer.parseInt(MyNID.substring(3,5));
            int NIDDay   =  Integer.parseInt(MyNID.substring(5,7));
            int City     =  Integer.parseInt(MyNID.substring(7,9));
            if(NIDBYear>=50){
                NIDBYear+=1900;
            }else{
                NIDBYear+=2000;
            }
            if((year>=2000 && NIDYear != 3)  || (year<2000 && NIDYear != 2)  || (year != NIDBYear) || (month != NIDMonth)|| (day != NIDDay) || (City >35 && City != 88) ){
                Toast.makeText(RegisterActivity.this,"Error : Invalid National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (year>2001){
               Toast.makeText(RegisterActivity.this,"Error : You Are to young to Register ",Toast.LENGTH_SHORT).show();
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
