package com.example.app_dictionary_ev.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseInitializer {
    private static final String PREFS_NAME = "db_init_prefs";
    private static final String KEY_INITIALIZED = "is_initialized";
    //PREFS_NAME và KEY_INITIALIZED: Dùng để lưu trạng thái đã khởi tạo database chưa trong SharedPreferences.
    private static final AtomicBoolean isInitializing = new AtomicBoolean(false);
    //AtomicBoolean: Để đảm bảo rằng chỉ có một luồng(thread) duy nhất có thể khởi tạo database.
    public interface InitializationCallback {
        void onComplete(int count);
        void onError(Exception e);
    }
    //Callback interface để thông báo khi khởi tạo xong hoặc có lỗi xảy ra.
    //onComplete(int count): được gọi khi khởi tạo thành công, với count là số lượng từ đã được thêm vào database.
    //onError(Exception e): được gọi khi có lỗi xảy ra trong quá trình khởi tạo database.

    public static void populateDatabase(Context context, InitializationCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        // Đã khởi tạo rồi
        if (prefs.getBoolean(KEY_INITIALIZED, false))
        {
            callback.onComplete(0);
            return;
        }
        //callback: là interface để trả về kết quả khi hoàn thành hoặc khi có lỗi.
        //tạo 1 thread riêng biệt để chạy việc đọc file JSON và ghi vào database
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            // Lấy instance của AppDatabase
            //Mỗi thread vẫn cần lấy database riêng biệt để tránh lỗi.

            if (prefs.getBoolean(KEY_INITIALIZED, false))
            {
                callback.onComplete(0);
                return;
            }
            //Kiểm tra lại lần nữa vì có thể trong lúc thread này chờ chạy thì thread khác đã khởi tạo xong rồi.
            //
            //Nếu rồi thì gọi callback và thoát luôn.

            // Đang có thread khác khởi tạo
            if (!isInitializing.compareAndSet(false, true)) {
                callback.onComplete(0);
                return;
            }

            try {
                // Thực hiện khởi tạo
                int count = populateData(context, db);
                // Đọc file JSON và thêm dữ liệu vào database

                // Đánh dấu thành công
                prefs.edit().putBoolean(KEY_INITIALIZED, true).apply();
                //Lưu trạng thái đã khởi tạo vào SharedPreferences
                callback.onComplete(count);
                //Gọi callback.onComplete với số lượng từ đã được thêm vào database.
            } catch (Exception e) {
                Log.e("DatabaseInitializer", "Initialization failed", e);
                callback.onError(e);
                //Gọi callback.onError với lỗi xảy ra trong quá trình khởi tạo.
            } finally {
                isInitializing.set(false);
            }
            // Đặt lại trạng thái khởi tạo
        });
    }
    private static int populateData(Context context, AppDatabase db) throws Exception {
        int totalInserted = 0;
        //Hàm tĩnh trả về số lượng phần tử đã chèn thành công (totalInserted).
        //
        //throws Exception: báo hiệu hàm này có thể ném lỗi nếu có sự cố trong quá trình đọc hoặc ghi dữ liệu.
        //
        //Biến totalInserted dùng đếm số bản ghi đã thêm vào database.
        try (InputStream is = context.getAssets().open("anhviet.json");
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             JsonReader reader = new JsonReader(isr))
                //mở file JSON trong thư mục assets
                //InputStream: để đọc dữ liệu từ file JSON
                //InputStreamReader: để chuyển đổi InputStream thành dạng ký tự
                //JsonReader: để đọc dữ liệu JSON từ InputStreamReader

        {

            Gson gson = new Gson();
            reader.beginArray(); // Bắt đầu mảng JSON
            //Bắt đầu đọc mảng JSON
            //JsonReader sẽ đọc từng phần tử trong mảng JSON
            //Mỗi phần tử là một đối tượng DictionaryEntry
            //DictionaryEntry là lớp đại diện cho một từ trong từ điển
            //Mỗi đối tượng DictionaryEntry sẽ được chuyển đổi từ JSON thành đối tượng Java


            List<DictionaryEntry> batch = new ArrayList<>();
            final int BATCH_SIZE = 50; // hoặc 100 nếu máy mạnh hơn
            //Kích thước của mỗi lô (batch) dữ liệu sẽ được chèn vào database
            //BATCH_SIZE: số lượng phần tử sẽ được chèn vào database cùng một lúc
            //Giúp tăng hiệu suất khi chèn dữ liệu vào database
            //Thay vì chèn từng phần tử một, ta sẽ chèn theo lô



            while (reader.hasNext()) {
                DictionaryEntry entry = gson.fromJson(reader, DictionaryEntry.class);
                if (entry != null) {
                    batch.add(entry);
                    totalInserted++;

                    if (batch.size() >= BATCH_SIZE) {
                        db.dictionaryDao().insertAll(batch);
                        batch.clear(); // reset danh sách
                    }
                }
                //Chuyển đổi từng phần tử JSON thành đối tượng DictionaryEntry
                //Nếu entry không null, tức là đã chuyển đổi thành công
                //Thêm entry vào danh sách batch
                //Tăng biến đếm totalInserted lên 1
                //Nếu kích thước của batch đã đủ lớn (>= BATCH_SIZE), thì chèn tất cả các phần tử trong batch vào database
                //Sau đó xóa sạch danh sách batch để chuẩn bị cho các phần tử tiếp theo

            }

            // Insert nốt những phần còn lại
            if (!batch.isEmpty()) {
                db.dictionaryDao().insertAll(batch);
            }
            //Chèn nốt các phần tử còn lại trong batch vào database
            //Nếu batch không rỗng, tức là còn phần tử chưa được chèn vào database


            reader.endArray();
            //Kết thúc mảng JSON
        }
        return totalInserted;
        //Trả về tổng số phần tử đã được chèn vào database
    }
}