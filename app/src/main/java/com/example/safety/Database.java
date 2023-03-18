package com.example.safety;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="user_database";
    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USER_DETAILS (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS USER_DETAILS");
        onCreate(db);
    }
}
