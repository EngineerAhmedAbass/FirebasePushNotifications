package abass.com.firebasepushnotifications.Sos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import abass.com.firebasepushnotifications.Home;
import abass.com.firebasepushnotifications.R;

public class SosActivity extends Activity {
    public Switch sos_switch;
    public boolean sos_flag;

    /*private TextView latituteField;
    private TextView longitudeField;*/
    public ArrayList<String> Names = new ArrayList<>();
    public ArrayList<String> Numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
//--------------------------------------------------------------------------------------------------------------------------
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            Names = savedInstanceState.getStringArrayList("names");
            Numbers = savedInstanceState.getStringArrayList("numbers");
            sos_flag = savedInstanceState.getBoolean("sos");
        } else {
            // Probably initialize members with default values for a new instance
        }
//--------------------------------------------------------------------------------------------------------------------------
        sos_switch = findViewById(R.id.sos_switch);
        /*latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);*/
        Button edit_contacts = findViewById(R.id.edit_contacts);
        Button send_sos_msg = findViewById(R.id.send_sos_msg);
//--------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------------------------------
        Intent iintent = getIntent();
        Bundle bd = iintent.getExtras();
        if (bd != null) {
            Names = (ArrayList<String>) bd.get("names");
            Numbers = (ArrayList<String>) bd.get("numbers");
            sos_flag = bd.getBoolean("sos_switch");
        }
        sos_switch.setChecked(sos_flag);
        if (sos_switch.isChecked()) {
            sos_switch.setText(R.string.sos_deactivate);
        } else {
            sos_switch.setText(R.string.sos_activate);
        }
        sos_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sos_switch.isChecked()) {
                    sos_switch.setText(R.string.sos_deactivate);
                } else {
                    sos_switch.setText(R.string.sos_activate);
                }
            }
        });
        send_sos_msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this,
                        Send_SOS_Message.class);
                intent.putExtra("names", Names);
                intent.putExtra("numbers", Numbers);
                intent.putExtra("sos_switch", sos_switch.isChecked());
                if (sos_switch.isChecked()) {
                    if (Names.size() < 1) {
                        Toast.makeText(SosActivity.this, "Please Add contacts first!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SosActivity.this, "Going to Send SOS Message!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SosActivity.this, "Please Activate SOS Message First!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        edit_contacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(SosActivity.this, contacts.class);
                intent.putExtra("sos_switch", sos_switch.isChecked());
                intent.putExtra("names", Names);
                intent.putExtra("numbers", Numbers);

                if (sos_switch.isChecked()) {
                    Toast.makeText(SosActivity.this, "Going to Edit Contacts!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(SosActivity.this, "Please Activate SOS Message First!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putStringArrayList("names", Names);
        savedInstanceState.putStringArrayList("numbers", Numbers);
        savedInstanceState.putBoolean("sos_switch", sos_flag);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SosActivity.this, Home.class);
        intent.putExtra("names", Names);
        intent.putExtra("numbers", Numbers);
        intent.putExtra("sos_switch", sos_flag);
        startActivity(intent);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        Names = savedInstanceState.getStringArrayList("names");
        Numbers = savedInstanceState.getStringArrayList("numbers");
        sos_flag = savedInstanceState.getBoolean("sos");
    }
}
