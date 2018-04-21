package abass.com.firebasepushnotifications.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import abass.com.firebasepushnotifications.R;
import abass.com.firebasepushnotifications.SettingsActivity;
import abass.com.firebasepushnotifications.ShowNotifications;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainMap extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView seekBarValue;
    String Data = "3";
    private Toolbar toolbar;
    private CheckBox hospital;
    private CheckBox police ;
    private CheckBox pharmacy;
    private ArrayList<String> Selected_Date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        Selected_Date = new ArrayList<String>();
        // Spinner element
        //final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button button = (Button) findViewById(R.id.button);
        seekBarValue = findViewById(R.id.textView3);
        final SeekBar Seek = findViewById(R.id.seekBar);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        hospital = findViewById(R.id.hospital);
        police = findViewById(R.id.police);
        pharmacy = findViewById(R.id.pharmacy);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Places");

        // Spinner click listener
        //spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("hospital");
        categories.add("pharmacy");
        categories.add("police");
        categories.add("ambulance");

        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        //spinner.setAdapter(dataAdapter);
        Seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText("within " + String.valueOf(progress) + " KM");
                Data = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataString="";
                Intent intent = new Intent(MainMap.this, MapsActivity.class);
                /*for(int i=0 ; i < Selected_Date.size();i++){
                    if(i==Selected_Date.size()-1){
                        dataString+=Selected_Date.get(i);
                    }else{
                        dataString+=Selected_Date.get(i)+"|";
                    }
                }*/
                intent.putStringArrayListExtra("data",Selected_Date);
                intent.putExtra("Distance", Data);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onCheckBoxClicked(View view) {
        Selected_Date.clear();
        boolean checked = ((CheckBox) view).isChecked();
        if (hospital.isChecked()) {
            Selected_Date.add("hospital");
        } else {
            Selected_Date.remove("hospital");
        }
        if (police.isChecked()) {
            Selected_Date.add("police");
        } else {
            Selected_Date.remove("police");
        }
        if (pharmacy.isChecked()) {
            Selected_Date.add("pharmacy");
        } else {
            Selected_Date.remove("pharmacy");
        }
        Toast.makeText(this, Selected_Date.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification:
                Intent GoToNotifications = new Intent(this, ShowNotifications.class);
                startActivity(GoToNotifications);
                break;
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            default:


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        seekBarValue.setText(" within " + String.valueOf(Data) + " KM");
        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}