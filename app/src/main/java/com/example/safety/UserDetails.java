package com.example.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserDetails extends AppCompatActivity {

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

        textView = (TextView)findViewById(R.id.textView);
        name = (TextView)findViewById(R.id.textView_name);
        u_id = (TextView)findViewById(R.id.textView_user_id);
        e_phone = (TextView)findViewById(R.id.textView_e_phone);
        button = (Button)findViewById(R.id.edit);

        Bundle bundle = getIntent().getExtras();
        Integer integer = bundle.getInt("Id");

        id = integer;
        Cursor cursor = db.rawQuery("SELECT * FROM USER_DETAILS WHERE id = "+integer+"",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false)
        {
            textView.setText("ABOUT");
            name.setText(cursor.getString(1));
            u_id.setText(cursor.getString(2));
            e_phone.setText(cursor.getString(4));
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
