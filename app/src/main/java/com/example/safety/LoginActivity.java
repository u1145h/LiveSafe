package com.example.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    String uid, pa;

    TextView textView;
    EditText user_id, password;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = (new Database(this)).getReadableDatabase();

        textView = (TextView)findViewById(R.id.textView);
        user_id = (EditText) findViewById(R.id.u_id);
        password = (EditText)findViewById(R.id.password);
        button = (ImageButton)findViewById(R.id.sign_in);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, SecondaryActivity.class));
        finish();
    }

    public void signIn(View v)
    {
        uid = user_id.getText().toString();
        pa = password.getText().toString();
        user_id.setText("");
        password.setText("");

        Cursor cursor = db.rawQuery("SELECT * FROM USER_DETAILS ORDER BY id", null);
        cursor.moveToFirst();

        Integer flag = 0;
        while (cursor.isAfterLast() == false)
        {
            String u = cursor.getString(2);
            String p =cursor.getString(3);

            if (uid.equals(u) && pa.equals(p))
            {
                String id = cursor.getString(0);
                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this,UserDetails.class);
                    Integer id1 = Integer.parseInt(id);
                    Bundle b = new Bundle();
                    b.putInt("Id",id1);
                    intent.putExtras(b);
                LoginActivity.this.startActivity(intent);

                flag = 1;
                break;
            }
            cursor.moveToNext();
        }
        if (flag == 0)
        {
            Toast.makeText(this, "Please Enter valid Login details.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

    }
}
