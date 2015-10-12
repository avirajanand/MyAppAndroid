package com.playtang.commonnavigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.playtang.commonnavigation.login.PlayTangOnLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class PlayTangVenue extends BaseActivity implements PlayTangOnLoadingListener{

    private ArrayAdapter<String> sportsAdapter;
    private ArrayList<String> sportsArray;
    private Spinner sportsSpinner;
    private SpinnerAdapter sportsSpinnerAdapter;
    private boolean isData = false;
    private Button addVenueButton;
    protected PlayTangOnLoadingListener onLoadingListener;
    private ProgressDialog progressDialog;
    private EditText venueNameField;
    private EditText venueCityField;
    private EditText venueStateField;
    private EditText venueAddressField;
    private EditText venuePinField;
    private EditText venueContactPhoneField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playtang_venue);

        onLoadingListener= this;

        venueNameField=(EditText) findViewById(R.id.venue_name);
        venueCityField=(EditText) findViewById(R.id.venue_city);
        venueStateField=(EditText) findViewById(R.id.venue_state);
        venueAddressField=(EditText) findViewById(R.id.venue_address);
        venuePinField=(EditText) findViewById(R.id.venue_pin);
        venueContactPhoneField=(EditText) findViewById(R.id.venue_contact);
        sportsSpinner= (Spinner)findViewById(R.id.sports_available);
        sportsSpinner.setBackground(getDrawable(R.drawable.gradient_background));
        addVenueButton=(Button) findViewById(R.id.parse_addvenue_button);
        addSportsVenue();
//sportsSpinner= new Spinner(getSupportActionBar().getThemedContext());
        sportsArray = new ArrayList<>();

        getSportsLocalDataBackground();

    }

    private void addSportsVenue() {
        addVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sport= sportsSpinner.getSelectedItem().toString();
                String venueName = venueNameField.getText().toString().trim();
                String venueCity = venueCityField.getText().toString().trim();
                String venueState = venueStateField.getText().toString().trim();
                String venueAddress = venueAddressField.getText().toString().trim();
                String venuePin =  venuePinField.getText().toString().trim();
                String venueContactPhone = venueContactPhoneField.getText().toString().trim();
               // venueContactPhoneField.get


                if(sport.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the sports available", Toast.LENGTH_SHORT).show();
                }
                else if(venueName.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the venue name", Toast.LENGTH_SHORT).show();
                }
                else if(venueCity.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the city ", Toast.LENGTH_SHORT).show();
                }
                else if(venueState.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the state ", Toast.LENGTH_SHORT).show();
                }
                else if(venueAddress.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the address ", Toast.LENGTH_SHORT).show();
                }
                else if(venuePin.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the pincode ", Toast.LENGTH_SHORT).show();
                }
                else if(venueContactPhone.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter the phone number ", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(ParseUser.getCurrentUser()==null){
                        Toast.makeText(getApplicationContext(), "Kindly logon to add venue", Toast.LENGTH_SHORT).show();
                    }else {
                        onLoadingStart(true);
                        saveVenueInBackground(venueName, venueCity, venueAddress, venueState, venuePin, venueContactPhone, sport);
                    }
                }

            }
        });
    }

    private void saveVenueInBackground(String venueName,String venueCity,String venueAddress,String venueState,String venuePin,String venueContact,String sport) {
        ArrayList<String> sportsAvailable = new ArrayList<>();
        sportsAvailable.add(sport);
        ParseObject venueObject = new ParseObject("Venue");

        ParseRelation<ParseObject> addedBy = venueObject.getRelation("added_by");
        addedBy.add(ParseUser.getCurrentUser());

        venueObject.put("sports_available",sportsAvailable);
        venueObject.put("name",venueName);
        venueObject.put("address",venueAddress);
        venueObject.put("city",venueCity);
        venueObject.put("state",venueState);
        venueObject.put("pinCode",venuePin);
        venueObject.put("contact",venueContact);
       // venueObject.put("added_by",addedBy);

        venueObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onLoadingFinish();
                if (e == null)
                    Toast.makeText(getApplicationContext(), "Venue has been added successfully ", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Venue cant be added :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getSportsLocalDataBackground() {
        //Toast.makeText(getApplicationContext(), "Inside getSportsLocalDataBackground", Toast.LENGTH_LONG).show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sports");
        query.fromLocalDatastore();
//        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sports, ParseException e) {
                if (e == null) {
                    //Toast.makeText(getApplicationContext(), "query completed , size:"+sports.size(), Toast.LENGTH_LONG).show();
                    int i = 0;
                    for (ParseObject sport : sports) {
                        sportsArray.add(i++, sport.getString("sports_name"));
                    }
                    if (sportsArray.size() > 0) {
                        sportsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.com_playtang_ui_spinner_dropdown_title, sportsArray);
                        sportsAdapter.setDropDownViewResource(R.layout.com_playtang_ui_dropdown_spinner);
                        sportsSpinner.setAdapter(sportsAdapter);
                    }
          //          Toast.makeText(getApplicationContext(), "data added :", Toast.LENGTH_LONG).show();
                    isData = true;

                } else {
                    Toast.makeText(getApplicationContext(), "query error :" + e.toString(), Toast.LENGTH_LONG).show();
                    //objectRetrievalFailed();
                }
            }
        });
    }


    private void getSportsDataBackground() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sports");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sports, ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "query completed :", Toast.LENGTH_LONG).show();
                    int i = 0;
                    for (ParseObject sport : sports) {
                        sportsArray.add(i++, sport.getString("sports_name"));
                    }
                    if (sportsArray.size() > 0) {
                        sportsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.com_playtang_ui_spinner_dropdown_title, sportsArray);
                        sportsAdapter.setDropDownViewResource(R.layout.com_playtang_ui_dropdown_spinner);
                        //sportsAdapter.setDropDownViewTheme(R.style.ParseLoginUI_Spinner);
                        sportsSpinner.setAdapter(sportsAdapter);
                    }
                     Toast.makeText(getApplicationContext(), "data added :", Toast.LENGTH_LONG).show();
                     isData = true;

                } else {
                    Toast.makeText(getApplicationContext(), "query error :" + e.toString(), Toast.LENGTH_LONG).show();
                    //objectRetrievalFailed();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_play_tang_venue, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when we are in progress retrieving some data.
     *
     * @param showSpinner
     *     Whether to show the loading dialog.
     */
    @Override
    public void onLoadingStart(boolean showSpinner) {
        if (showSpinner) {
            progressDialog = ProgressDialog.show(this, null,
                    getString(com.playtang.commonnavigation.R.string.com_parse_ui_progress_dialog_text), true, false);
        }
    }

    /**
     * Called when we are finished retrieving some data.
     */
    @Override
    public void onLoadingFinish() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
