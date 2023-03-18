package com.example.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditDetails extends AppCompatActivity {

    public SQLiteDatabase db;

    EditText phone_no;
    Button button_update;
    TextView textView;
    String uid;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("Id");

        db = (new Database(this)).getWritableDatabase();

        textView = (TextView)findViewById(R.id.textView);
        phone_no = (EditText)findViewById(R.id.phone);
        button_update = (Button)findViewById(R.id.update);


    }
    public void update(View v)
    {
        String ph = phone_no.getText().toString();
        if (ph.length() == 0)
        {
            Toast.makeText(this, "Put a Number.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //"UPDATE USER_DETAILS SET phone = '"+ph+"' WHERE u_id = '"+uid+"' ", null

            db.execSQL("UPDATE USER_DETAILS SET phone='"+ph+"' WHERE id= "+id);
            //startActivity(new Intent(EditDetails.this, UserDetails.class));
            Intent intent = new Intent(this,UserDetails.class);
            Integer id1 = id;
            Bundle b = new Bundle();
            b.putInt("Id",id1);
            intent.putExtras(b);
            this.startActivity(intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditDetails.this, SecondaryActivity.class));
        finish();
    }
}
