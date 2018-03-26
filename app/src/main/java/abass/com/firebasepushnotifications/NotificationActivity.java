package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private TextView fromText ;
    private TextView DomainTxt;
    private TextView MessageTxt;
    private TextView placeTxt ;
    private TextView Status ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        String dataMessage = getIntent().getStringExtra("message");
        String dataFrom = getIntent().getStringExtra("from_user_id");
        final String longtitude = getIntent().getStringExtra("longtitude");
        final String latitude = getIntent().getStringExtra("latitude");
        String Domain = getIntent().getStringExtra("domain");
        String request_id =getIntent().getStringExtra("request_id");


        String LocationUEL = "http://maps.google.com/maps?q="+latitude+","+longtitude;

        fromText = (TextView) findViewById(R.id.from_txt);
        DomainTxt = (TextView) findViewById(R.id.domain_txt);
        MessageTxt = (TextView) findViewById(R.id.message);
        placeTxt = (TextView) findViewById(R.id.place);
        Status = (TextView) findViewById(R.id.status);

        new GetStatus().execute(Integer.parseInt(request_id));

        placeTxt.setClickable(true);
        placeTxt.setMovementMethod(LinkMovementMethod.getInstance());
        placeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longtitude);

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

    }
    class GetStatus extends AsyncTask<Integer ,Void,String>{

        @Override
        protected String doInBackground(Integer... integers) {
            String url = "http://refadatours.com/android/getStatus.php?id="+integers[0];
            HttpEntity httpEntity = null;
            try
            {
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
            return  entityResponse ;
        }
        protected void onPostExecute(String feed) {
            Status.setText(feed);
        }
    }
}
