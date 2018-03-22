package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    private TextView fromText ;
    private TextView DomainTxt;
    private TextView MessageTxt;
    private TextView placeTxt ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        String dataMessage = getIntent().getStringExtra("message");
        String dataFrom = getIntent().getStringExtra("from_user_id");
        final String longtitude = getIntent().getStringExtra("longtitude");
        final String latitude = getIntent().getStringExtra("latitude");
        String Domain = getIntent().getStringExtra("domain");

        String LocationUEL = "http://maps.google.com/maps?q="+latitude+","+longtitude;

        fromText = (TextView) findViewById(R.id.from_txt);
        DomainTxt = (TextView) findViewById(R.id.domain_txt);
        MessageTxt = (TextView) findViewById(R.id.message);
        placeTxt = (TextView) findViewById(R.id.place);
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
}
