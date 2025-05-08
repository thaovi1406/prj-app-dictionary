package com.example.app_dictionary_ev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AppDictionary.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng favorites
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MEANING = "meaning";

    // Bảng translation_history
    public static final String TABLE_TRANSLATION_HISTORY = "translation_history";
    public static final String COLUMN_HISTORY_ID = "id";
    public static final String COLUMN_INPUT_TEXT = "inputText";
    public static final String COLUMN_TRANSLATED_TEXT = "translatedText";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_PRONUNCIATION + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_MEANING + " TEXT)";
        db.execSQL(createFavoritesTable);

        String createHistoryTranTable = "CREATE TABLE " + TABLE_TRANSLATION_HISTORY + " (" +
                COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INPUT_TEXT + " TEXT, " +
                COLUMN_TRANSLATED_TEXT + " TEXT )";
        db.execSQL(createHistoryTranTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATION_HISTORY);
        onCreate(db);
    }

//    public boolean addFavoriteWord(String word, String pronunciation, String type, String meaning) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_WORD, word);
//        values.put(COLUMN_PRONUNCIATION, pronunciation);
//        values.put(COLUMN_TYPE, type);
//        values.put(COLUMN_MEANING, meaning);
//        long result = db.insert(TABLE_FAVORITES, null, values);
//        db.close();
//        return result != -1;
//    }
public boolean addFavoriteWord(String word, String pronunciation, String type, String meaning) {
    SQLiteDatabase db = this.getWritableDatabase();

    // Kiểm tra xem từ đã tồn tại chưa
    String query = "SELECT 1 FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_WORD + " = ?";
    Cursor cursor = db.rawQuery(query, new String[]{word});

    boolean exists = cursor.moveToFirst();  // true nếu đã có
    cursor.close();

    if (exists) {
        db.close();
        return false; // Không thêm vì đã tồn tại
    }

    // Nếu chưa có thì thêm
    ContentValues values = new ContentValues();
    values.put(COLUMN_WORD, word);
    values.put(COLUMN_PRONUNCIATION, pronunciation);
    values.put(COLUMN_TYPE, type);
    values.put(COLUMN_MEANING, meaning);

    long result = db.insert(TABLE_FAVORITES, null, values);
    db.close();
    return result != -1;
}


    public boolean isWordFavorite(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{COLUMN_WORD},
                COLUMN_WORD + "=?", new String[]{word}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean removeFavoriteWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FAVORITES, COLUMN_WORD + "=?", new String[]{word});
        db.close();
        return result > 0;
    }

    public boolean insertTranslationHistory( String inputText, String translatedText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INPUT_TEXT, inputText);
        values.put(COLUMN_TRANSLATED_TEXT, translatedText);
        long result = db.insert(TABLE_TRANSLATION_HISTORY, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getTranslationHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRANSLATION_HISTORY, null, null, null, null, null, null);
    }

    public boolean deleteTranslationHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRANSLATION_HISTORY, COLUMN_HISTORY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }
    public int removeFavoriteWords(List<String> words) {
        if (words == null || words.isEmpty()) return 0;

        SQLiteDatabase db = this.getWritableDatabase();

        // Tạo danh sách ? tương ứng số lượng từ
        StringBuilder placeholders = new StringBuilder();
        String[] args = new String[words.size()];
        for (int i = 0; i < words.size(); i++) {
            placeholders.append("?,");
            args[i] = words.get(i);
        }
        placeholders.setLength(placeholders.length() - 1); // Xóa dấu phẩy cuối

        String whereClause = COLUMN_WORD + " IN (" + placeholders + ")";
        int rowsDeleted = db.delete(TABLE_FAVORITES, whereClause, args);
        db.close();
        return rowsDeleted;
    }
    public boolean addToFavorites(DictionaryEntry entry) {
        if (entry == null || entry.word == null) return false;

        if (isWordFavorite(entry.word)) return false;

        StringBuilder meaningBuilder = new StringBuilder();
        if (entry.meanings != null && !entry.meanings.isEmpty()) {
            for (DictionaryEntry.Meaning m : entry.meanings) {
                meaningBuilder.append("➜ ").append(m.definition).append("\n");
                if (m.example != null) meaningBuilder.append(m.example).append("\n");
                if (m.note != null) meaningBuilder.append("(").append(m.note).append(")\n");
            }
        }

        return addFavoriteWord(
                entry.word,
                entry.pronunciation != null ? entry.pronunciation : "",
                entry.pos != null ? entry.pos : "",
                meaningBuilder.toString().trim()
        );
    }
    public VocabModel getWordDetails(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        VocabModel model = null;

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE word = ?", new String[]{word});
        if (cursor.moveToFirst()) {
            try {
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
                String pos = cursor.getString(cursor.getColumnIndexOrThrow("pos"));
                String meaningsJson = cursor.getString(cursor.getColumnIndexOrThrow("meanings"));

                Gson gson = new Gson();
                Type type = new TypeToken<List<Meaning>>() {}.getType();
                List<Meaning> meanings = gson.fromJson(meaningsJson, type);

                model = new VocabModel(word, pronunciation, pos, meanings);
            } catch (IllegalArgumentException e) {
                Log.e("DB_ERROR", "Column missing: " + e.getMessage());
            }
        }
        cursor.close();
        return model;
    }



}