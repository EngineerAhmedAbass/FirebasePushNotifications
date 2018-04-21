package abass.com.firebasepushnotifications.Sos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import abass.com.firebasepushnotifications.Home;
import abass.com.firebasepushnotifications.R;

public class SosActivity extends Activity {
    public Switch sos_switch;
    public boolean sos_flag;
    public ArrayList<String> Names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        loadData();
//--------------------------------------------------------------------------------------------------------------------------
        sos_switch = findViewById(R.id.sos_switch);
        Button edit_contacts = findViewById(R.id.edit_contacts);
        Button send_sos_msg = findViewById(R.id.send_sos_msg);
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
                saveData();
            }
        });
        send_sos_msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this,
                        Send_SOS_Message.class);
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
    public void onBackPressed() {
        saveData();
        Intent intent = new Intent(SosActivity.this, Home.class);
        startActivity(intent);
    }

    public void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Names);
        editor.putString("names",json);
        editor.putBoolean("sos_flag",sos_flag);
        editor.apply();
    }
    public void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("names",null);
        sos_flag = sharedPreferences.getBoolean("sos_flag",false);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Names = gson.fromJson(json,type);
        if (Names == null)
        {
            Names = new ArrayList<>();
        }

    }
}
