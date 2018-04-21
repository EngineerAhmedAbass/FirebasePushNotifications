package abass.com.firebasepushnotifications.Sos;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import abass.com.firebasepushnotifications.R;

public class Send_SOS_Message extends AppCompatActivity {
    Button buttonSend;
    public ArrayList<Integer> index_arr = new ArrayList<>();
    public ArrayList<String> Names = new ArrayList<>();
    public ArrayList<String> Numbers = new ArrayList<>();
    public boolean sos_switch;
    public TextView nameView;
    public TextView phoneView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__sos__message);
        buttonSend =  findViewById(R.id.buttonSend);
        RelativeLayout parent_Relative_layout = findViewById(R.id.parent_Relative_layout2);

       loadData();
//------------------------------------------------------------------------------------------------------------------------------------------------
        for (int i=0; i<Names.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") RelativeLayout rowView = (RelativeLayout) inflater.inflate(R.layout.field2, null);
            Random r = new Random();
            int ii = r.nextInt(1000 - 1) + 1;

            rowView.setId(ii);
            index_arr.add(ii);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (parent_Relative_layout.getChildCount() > 0) {
                int y = parent_Relative_layout.getChildCount() - 1;
                params.addRule(RelativeLayout.BELOW, index_arr.get(y));
                rowView.setLayoutParams(params);
            }
            // Add the new row before the add field button.
            parent_Relative_layout.addView(rowView, params);
            nameView = rowView.findViewById(R.id.textName);
            phoneView = rowView.findViewById(R.id.textPhone);

            nameView.setText(Names.get(i));
            phoneView.setText(Numbers.get(i));
        }
//------------------------------------------------------------------------------------------------------------------------------------------------

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String smss = "Please Help Me!";
                Toast.makeText(Send_SOS_Message.this, smss, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),Send_SOS_Message.class);

                Intent iiintent = new Intent(Send_SOS_Message.this, SosActivity.class);

                iiintent.putExtra("names",Names);
                iiintent.putExtra("numbers",Numbers);
                iiintent.putExtra("sos_switch",sos_switch);
                PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                try {
                    SmsManager sms = SmsManager.getDefault();
                    for (int i=0; i<Numbers.size();i++) {
                        String temp = Numbers.get(i);
                        Toast.makeText(getApplicationContext(), Numbers.get(i), Toast.LENGTH_LONG).show();
                        sms.sendTextMessage(temp, null, smss, pi, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
                    }
                    startActivity(iiintent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Send_SOS_Message.this, SosActivity.class);
        startActivity(intent);
    }
    public void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("names",null);
        String json2 = sharedPreferences.getString("numbers",null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Names = gson.fromJson(json,type);
        Numbers = gson.fromJson(json2,type);
        if (Names == null)
        {
            Names = new ArrayList<>();
            Numbers = new ArrayList<>();
        }

    }

}
