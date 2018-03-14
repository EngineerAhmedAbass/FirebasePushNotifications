package abass.com.firebasepushnotifications;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    private TextView mNotifyData ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        String dataMessage = getIntent().getStringExtra("message");
        String dataFrom = getIntent().getStringExtra("from_user_id");

        mNotifyData = (TextView) findViewById(R.id.notif_text);

        mNotifyData.setText("From : "+dataFrom+" | MESSAGE :  "+dataMessage);


    }
}
