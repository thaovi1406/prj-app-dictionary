package com.example.app_dictionary_ev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FavoriteWords.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "favorites";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_PRONUNCIATION = "pronunciation";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MEANING = "meaning";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_PRONUNCIATION + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_MEANING + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Thêm từ vào danh sách yêu thích
    public boolean addFavoriteWord(String word, String pronunciation, String type, String meaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_PRONUNCIATION, pronunciation);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_MEANING, meaning);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    // Kiểm tra xem từ đã có trong danh sách yêu thích chưa
    public boolean isWordFavorite(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_WORD},
                COLUMN_WORD + "=?", new String[]{word}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Xóa từ khỏi danh sách yêu thích
    public boolean removeFavoriteWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_WORD + "=?", new String[]{word});
        db.close();
        return result > 0;
    }
}