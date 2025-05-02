package com.example.app_dictionary_ev.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseInitializer {
    private static final String PREFS_NAME = "db_init_prefs";
    private static final String KEY_INITIALIZED = "is_initialized";
    private static final AtomicBoolean isInitializing = new AtomicBoolean(false);

    public interface InitializationCallback {
        void onComplete(int count);
        void onError(Exception e);
    }

    public static void populateDatabase(Context context, InitializationCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        AppDatabase db = AppDatabase.getInstance(context);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Kiểm tra xem đã khởi tạo chưa
                List<DictionaryEntry> existingData = db.dictionaryDao().getAll();
                if (prefs.getBoolean(KEY_INITIALIZED, false) && !existingData.isEmpty()) {
                    callback.onComplete(0);
                    return;
                }

                // Kiểm tra xem có thread khác đang khởi tạo không
                if (!isInitializing.compareAndSet(false, true)) {
                    callback.onComplete(0);
                    return;
                }

                // Khởi tạo dữ liệu
                int count = populateData(context, db);

                // Đánh dấu đã khởi tạo
                prefs.edit().putBoolean(KEY_INITIALIZED, true).apply();
                callback.onComplete(count);
            } catch (Exception e) {
                Log.e("DatabaseInitializer", "Initialization failed", e);
                callback.onError(e);
            } finally {
                isInitializing.set(false);
            }
        });
    }

    private static int populateData(Context context, AppDatabase db) throws Exception {
        int totalInserted = 0;
        try (InputStream is = context.getAssets().open("anhviet.json");
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             JsonReader reader = new JsonReader(isr)) {

            Gson gson = new Gson();
            reader.beginArray();

            List<DictionaryEntry> batch = new ArrayList<>();
            final int BATCH_SIZE = 50;

            while (reader.hasNext()) {
                DictionaryEntry entry = gson.fromJson(reader, DictionaryEntry.class);
                if (entry != null) {
                    batch.add(entry);
                    totalInserted++;

                    if (batch.size() >= BATCH_SIZE) {
                        db.dictionaryDao().insertAll(batch);
                        batch.clear();
                    }
                }
            }

            if (!batch.isEmpty()) {
                db.dictionaryDao().insertAll(batch);
            }

            reader.endArray();
        } catch (Exception e) {
            throw new Exception("Failed to load anhviet.json. Ensure the file exists in assets and is correctly formatted.", e);
        }
        return totalInserted;
    }
}