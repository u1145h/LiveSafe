package com.example.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    TextView tv;
    EditText name, phone_no;
    Button sub;
    String n, ph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=(new Database(this)).getWritableDatabase();

        name=(EditText)findViewById(R.id.name);
        phone_no=(EditText)findViewById(R.id.emergNum);
        sub=(Button)findViewById(R.id.sign_up);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void sub(View v)
    {
        n = name.getText().toString();
        ph = phone_no.getText().toString();

        if (n.length()==0 ||  ph.length()==0)
        {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.execSQL("INSERT INTO USER_DETAILS (name, phone) VALUES ('"+n+"','"+ph+"')");

            Intent in=new Intent(MainActivity.this, SecondaryActivity.class);
            MainActivity.this.startActivity(in);
        }

    }
}
