package abass.com.firebasepushnotifications;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        String longtitude = getIntent().getStringExtra("longtitude");
        String latitude = getIntent().getStringExtra("latitude");
        String Domain = getIntent().getStringExtra("Domain");

        String LocationUEL = "http://maps.google.com/maps?q="+latitude+","+longtitude;

        fromText = (TextView) findViewById(R.id.from_txt);
        DomainTxt = (TextView) findViewById(R.id.domain_txt);
        MessageTxt = (TextView) findViewById(R.id.message);
        placeTxt = (TextView) findViewById(R.id.place);

        fromText.setText(dataFrom);
        DomainTxt.setText(Domain);
        MessageTxt.setText(dataMessage);
        placeTxt.setText(LocationUEL);

    }
}
