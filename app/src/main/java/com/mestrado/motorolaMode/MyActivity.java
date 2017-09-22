package com.mestrado.motorolaMode;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.Rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int initialHour;
    private int finalHour;
    private int initialTimeMode;
    private int finalTimeMode;
    private GoogleApiClient mGoogleApiClient;
    private Switch switchSleepingMode;
    private Switch switchHomeMode;
    private Spinner spinnerInitialTime;
    private Spinner spinnerFinalTime;
    private Spinner spinnerInitialTimeMode;
    private Spinner spinnerFinalTimeMode;
    private Button buttonLocation;
    private TextView textViewLocation;
    private Date initialTime;
    private Date finalTime;
    private Place place;
    private List<Rule> rulesSleepingMode = new ArrayList<>();
    private List<Rule> rulesHomeMode = new ArrayList<>();
    private List<Rule> rulesMeetingMode = new ArrayList<>();
    private List<Rule> rulesDrivingMode = new ArrayList<>();
    private final int PLACE_PICKER_REQUEST = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECEIVE_SMS};
    public static final String LOG = "LOG_TESTE";

    public void setInitialHour(int initialHour) {
        this.initialHour = initialHour;
    }

    public void setFinalHour(int finalHour) {
        this.finalHour = finalHour;
    }

    public void setInitialTimeMode(int initialTimeMode) {
        this.initialTimeMode = initialTimeMode;
    }

    public void setFinalTimeMode(int finalTimeMode) {
        this.finalTimeMode = finalTimeMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API).build();
        mGoogleApiClient.connect();

        checkPermissions();
        //initializeSleepingModeSwitch();
        initializeHomeModeSwitch();
        //initializeMeetingModeSwitch();
        //initializeDrivingModeSwitch();
    }

    private void checkPermissions() {
        int permissionLocation = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionContacts = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS);
        int permissionPhone = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE);
        int permissionSms = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS);

        if (permissionLocation != PackageManager.PERMISSION_GRANTED
                || permissionContacts != PackageManager.PERMISSION_GRANTED
                || permissionPhone != PackageManager.PERMISSION_GRANTED
                || permissionSms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 123);
        }
    }

    //BEGIN SleepingMode
    private void initializeSleepingModeSwitch() {
        switchSleepingMode = (Switch) findViewById(R.id.SleepingSwitch);

        configureSpinnerTime();
        switchSleepingMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setVisibilityOfAllSpinner(false);
                    //startSleepingMode();
                } else {
                    setVisibilityOfAllSpinner(true);
                    //killRules(rulesSleepingMode);
                    rulesSleepingMode = new ArrayList<Rule>();
                }
            }
        });
    }

    private void configureSpinnerTime() {
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(this,
                R.array.hours_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMode = ArrayAdapter.createFromResource(this,
                R.array.hours_mode, android.R.layout.simple_spinner_item);
        initializeSpinnerTime();
        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInitialTime.setAdapter(adapterHours);
        spinnerFinalTime.setAdapter(adapterHours);
        spinnerInitialTimeMode.setAdapter(adapterMode);
        spinnerFinalTimeMode.setAdapter(adapterMode);
        setListenersInSpinnerTime();
    }

    private void initializeSpinnerTime() {
        spinnerInitialTime = (Spinner) findViewById(R.id.SpinnerInitialTime);
        spinnerFinalTime = (Spinner) findViewById(R.id.SpinnerFinalTime);
        spinnerInitialTimeMode = (Spinner) findViewById(R.id.SpinnerInitialTimeMode);
        spinnerFinalTimeMode = (Spinner) findViewById(R.id.SpinnerFinalTimeMode);
    }

    private void setListenersInSpinnerTime() {
        spinnerInitialTime.setOnItemSelectedListener(new MyActivity.Listener1());
        spinnerFinalTime.setOnItemSelectedListener(new MyActivity.Listener2());
        spinnerInitialTimeMode.setOnItemSelectedListener(new MyActivity.Listener3());
        spinnerFinalTimeMode.setOnItemSelectedListener(new MyActivity.Listener4());
    }

    private void setVisibilityOfAllSpinner(boolean visibilityOfAllSpinner) {
        spinnerInitialTime.setEnabled(visibilityOfAllSpinner);
        spinnerFinalTime.setEnabled(visibilityOfAllSpinner);
        spinnerInitialTimeMode.setEnabled(visibilityOfAllSpinner);
        spinnerFinalTimeMode.setEnabled(visibilityOfAllSpinner);
    }

    private void startSleepingMode() {
        Date actualTime = new Date();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(actualTime);
        calendar2.setTime(actualTime);
        /*calendar1.set(Calendar.HOUR, calculateProperHour(initialHour));
        calendar1.set(Calendar.AM_PM, initialTimeMode);
        calendar2.set(Calendar.HOUR, calculateProperHour(finalHour));
        calendar2.set(Calendar.AM_PM, finalTimeMode);

        if (calendar1.getTime().before(actualTime)) {
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (calendar2.getTime().before(calendar1.getTime())) {
            calendar2.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) + 1);
        }*/

        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.HOUR, calendar2.get(Calendar.HOUR) + 1);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        initialTime = calendar1.getTime();
        finalTime = calendar2.getTime();

        AwarenessAPIContextTime context = new AwarenessAPIContextTime(initialTime, finalTime);
        //Log.i(MyActivity.LOG, initialTime.toString());
        //Log.i(MyActivity.LOG, finalTime.toString());
        rulesSleepingMode.add(new RuleInitial(this, initialTime));
        rulesSleepingMode.add(new RuleRinging(this, context));
        rulesSleepingMode.add(new RuleSilence(this, context));
        context.setDateInterval(initialTime, finalTime);
        rulesSleepingMode.add(new RuleFinal(this, context, finalTime));

        for (Rule r : rulesSleepingMode) {
            r.startContextsMonitoring();
        }
    }

    private int calculateProperHour(int hour) {
        if (hour == 12) {
            return 0;
        } else {
            return hour;
        }
    }
    //END SleepingMode

    private void killRules(List<Rule> rules) {
        for (Rule r : rules) {
            r.killContextsAndActions();
        }
    }

    //BEGIN HomeMode
    private void initializeHomeModeSwitch() {
        switchHomeMode = (Switch) findViewById(R.id.HomeSwitch);

        initializeLocationButton();
        switchHomeMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (place == null) {
                        Toast.makeText(MyActivity.this, "Place needs to be set", Toast.LENGTH_LONG).show();
                        switchHomeMode.setChecked(false);
                    } else {
                        startHomeMode();
                    }
                } else {
                    unregisterMessageBroadcastReceiver();
                    killRules(rulesHomeMode);
                    rulesHomeMode = new ArrayList<Rule>();
                }
            }
        });
    }

    private void initializeLocationButton() {
        buttonLocation = (Button) findViewById(R.id.BtSetLocation);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MyActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            place = PlacePicker.getPlace(this, data);
            textViewLocation = (TextView) findViewById(R.id.TViewLocation);

            if (place != null) {
                textViewLocation.setText(" " + place.getAddress());
            } else {
                textViewLocation.setText(" Location unavailable");
            }
        }
    }

    private void startHomeMode() {
        //Log.i(LOG, "start Home Mode");
        Action a = new ActionSpeakCall();

        rulesHomeMode.add(new RuleHome(this, a, place));
        rulesHomeMode.add(new RuleStopSpeaking(this, a, place));
        rulesHomeMode.add(new RuleMessage(this, place));

        for (Rule r : rulesHomeMode) {
            r.startContextsMonitoring();
        }
    }

    private void unregisterMessageBroadcastReceiver() {
        Log.i(MyActivity.LOG, "unregister broadcast receiver");

        for (Rule r : rulesHomeMode) {
            if (r instanceof RuleMessage) {
                for (Context c : r.getContexts()) {
                    if (c instanceof NewContextIncomingMessage) {
                        unregisterReceiver(((NewContextIncomingMessage) c).getMessageBroadcastReceiver());
                    }
                }
            }
        }
    }
    //END HomeMode

    //BEGIN MeetingMode
    private void initializeMeetingModeSwitch() {
        Switch sw = (Switch) findViewById(R.id.MeetingSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startMeetingMode();
                } else {

                }
            }
        });
    }

    private void startMeetingMode() {

    }
    //END MeetingMode

    //BEGIN DrivingMode
    private void initializeDrivingModeSwitch() {
        Switch sw = (Switch) findViewById(R.id.DrivingSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startDrivingMode();
                } else {

                }
            }
        });
    }

    private void startDrivingMode() {

    }
    //END DrivingMode

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private class Listener1 implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setInitialHour(Integer.parseInt((String) parent.getItemAtPosition(position)));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class Listener2 implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setFinalHour(Integer.parseInt((String) parent.getItemAtPosition(position)));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class Listener3 implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String mode = (String) parent.getItemAtPosition(position);

            if (mode.contains("AM")) {
                setInitialTimeMode(0);
            } else if (mode.contains("PM")) {
                setInitialTimeMode(1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class Listener4 implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String mode = (String) parent.getItemAtPosition(position);

            if (mode.contains("AM")) {
                setFinalTimeMode(0);
            } else if (mode.contains("PM")) {
                setFinalTimeMode(1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
