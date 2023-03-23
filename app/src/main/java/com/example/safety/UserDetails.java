package com.example.safety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class UserDetails extends AppCompatActivity {


    RelativeLayout userdetailsLayout;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;


    public SQLiteDatabase db;

    int id = 0;
    String bundle_uid;
    TextView textView, name, u_id, e_phone;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        db = (new Database(this)).getReadableDatabase();

        userdetailsLayout = findViewById(R.id.userDetails);
        textView = (TextView)findViewById(R.id.textView);
        name = (TextView)findViewById(R.id.textView_name);
        e_phone = (TextView)findViewById(R.id.textView_e_phone);
        button = (Button)findViewById(R.id.edit);

        /*Bundle bundle = getIntent().getExtras();
        Integer integer = bundle.getInt("Id");*/

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(),"Device doesn't have fingerprint sensor",Toast.LENGTH_SHORT).show();
                break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(getApplicationContext(),"Biometric not working",Toast.LENGTH_SHORT).show();
                    break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(getApplicationContext(),"No fingerprint assigned",Toast.LENGTH_SHORT).show();
                        break;
        }
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(UserDetails.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                startActivity(new Intent(UserDetails.this, SecondaryActivity.class));
                finishAffinity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
                function();
                userdetailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                startActivity(new Intent(UserDetails.this, SecondaryActivity.class));
                finishAffinity();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("LOGIN")
                .setDescription("Use fingerprint or screen lock to login").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);



    }

    protected void function(){

        id = 1;
        Cursor cursor = db.rawQuery("SELECT * FROM USER_DETAILS WHERE id = "+id+"",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false)
        {
            textView.setText("ABOUT");
            name.setText(cursor.getString(1));
            e_phone.setText(cursor.getString(2));
            cursor.moveToNext();
        }
        cursor.close();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserDetails.this, SecondaryActivity.class));
        finishAffinity();
    }

    public void edit(View v)
    {
        Intent in = new Intent(UserDetails.this, EditDetails.class);
            Integer id1 = id;
            Bundle b = new Bundle();
            b.putInt("Id",id1);
            in.putExtras(b);
        UserDetails.this.startActivity(in);
    }
}
