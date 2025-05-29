package com.example.the_sobat_gapai.DB_HELPER;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TOKEN = "token";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TOKEN + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveToken(String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOKEN, token);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String getToken() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TOKEN}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String token = cursor.getString(0);
            cursor.close();
            db.close();
            return token;
        }
        return null;
    }

    // Mendapatkan ID pengguna
    public int getUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);  // Ambil ID pengguna dari database
            cursor.close();
            db.close();
            return userId;
        }
        return -1; // Jika ID tidak ditemukan, kembalikan -1
    }


    public void clearToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
