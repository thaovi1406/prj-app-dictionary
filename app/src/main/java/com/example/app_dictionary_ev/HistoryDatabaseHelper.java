package com.example.app_dictionary_ev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FavoriteWords.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MEANING = "meaning";

    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_PRONUNCIATION + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_MEANING + " TEXT)";
        db.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Thêm từ vào lịch sử
    public boolean addHistoryWord(String word, String pronunciation, String type, String meaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_PRONUNCIATION, pronunciation);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_MEANING, meaning);
        long result = db.insert(TABLE_HISTORY, null, values);
        db.close();
        return result != -1;
    }

    // Lấy toàn bộ từ trong lịch sử
    public Cursor getHistoryWords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HISTORY, new String[]{
                        COLUMN_WORD, COLUMN_PRONUNCIATION, COLUMN_TYPE, COLUMN_MEANING},
                null, null, null, null, null);
    }

    // (Tùy chọn) Xóa toàn bộ lịch sử
    public void clearHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null);
        db.close();
    }
    public int deleteWordsFromHistory(List<String> words) {
        if (words == null || words.isEmpty()) return 0;

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            placeholders.append("?");
            if (i < words.size() - 1) placeholders.append(",");
        }

        String whereClause = "word IN (" + placeholders + ")";
        String[] whereArgs = words.toArray(new String[0]);

        int deletedRows = db.delete(TABLE_HISTORY, whereClause, whereArgs);
        db.close();
        return deletedRows;
    }

}
