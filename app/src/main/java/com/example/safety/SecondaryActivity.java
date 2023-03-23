package com.example.safety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skyfishjy.library.RippleBackground;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class SecondaryActivity extends AppCompatActivity {


    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;

    public SQLiteDatabase db;

    String phone_no, message;

    URL url;

    int flag = 0;

    Double latitude, longitude;

    final Handler handler = new Handler();
    Timer timer = new Timer();


    ToggleButton toggleButton;
    ImageButton imageButton;
    ImageButton dev_info;
    TextView textView;
    RippleBackground rippleBackground;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        db = (new Database(this)).getReadableDatabase();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        //imageButton = (ImageButton) findViewById(R.id.imageButton);
        //dev_info = (ImageButton)findViewById(R.id.dev_info);
        textView = (TextView) findViewById(R.id.textView);
        rippleBackground = (RippleBackground) findViewById(R.id.ripple_bg);


        check();
        Toast.makeText(this, "Please enable internet for better results.", Toast.LENGTH_LONG).show();

    }


    public void toggle(View v) {
        if (toggleButton.isChecked()) {
            Toast.makeText(this, "On", Toast.LENGTH_SHORT).show();

            if (flag == 0) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    checkForGPS();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 60000);
            }

            textView.setText("Touch the icon again to Stop sending your location");
            rippleBackground.startRippleAnimation();


        } else {
            textView.setText("Touch the location icon below to send your location to your emergency contact");
            flag = 1;
            timer.cancel();
            rippleBackground.stopRippleAnimation();
            Toast.makeText(this, "Off", Toast.LENGTH_SHORT).show();
        }

    }

    public void info(View v) {
        dialog = new Dialog(this);

        dialog.setContentView(R.layout.developer_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    protected void checkForGPS() {
        //check if gps is enabled or not and then request user to enable it.
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(SecondaryActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(SecondaryActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fetchLocation();
            }
        });
        task.addOnFailureListener(SecondaryActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SecondaryActivity.this, "Turn on GPS.", Toast.LENGTH_LONG).show();
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(SecondaryActivity.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }


    protected void sendMessage() {
        String encd_url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        try {
            url = new URL(encd_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone_no, null, message + url, null, null);
    }


    public void settings(View v) {
        Intent intent = new Intent(SecondaryActivity.this, UserDetails.class);
        SecondaryActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.moveTaskToBack(true);
    }

    //*****************************************//
    //to check if the database is empty or not.
    public void check() {
        String string = "";
        Cursor cursor = db.rawQuery("SELECT * FROM USER_DETAILS", null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            //************//
            /*To get the phone number and the message*/
            phone_no = cursor.getString(2);
            message = cursor.getString(1) + " might be in danger. Here's the last location";
            //************//
            string += "\n" + cursor.getString(0) + cursor.getString(1) + cursor.getString(2);
            cursor.moveToNext();
        }
        cursor.close();
        if (string.length() == 0) {
            Intent intent1 = new Intent(SecondaryActivity.this, MainActivity.class);
            SecondaryActivity.this.startActivity(intent1);
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        latitude = lastKnownLocation.getLatitude();
                        longitude = lastKnownLocation.getLongitude();
                        sendMessage();
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                lastKnownLocation = locationResult.getLastLocation();
                                latitude = lastKnownLocation.getLatitude();
                                longitude = lastKnownLocation.getLongitude();
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        if (ActivityCompat.checkSelfPermission(SecondaryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SecondaryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            }
        });
    }

}