package com.example.app_dictionary_ev.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;

@Database(entities = {DictionaryEntry.class}, version = 1)
//danh sách các bảng (entity) (table) trong database. Ở đây chỉ có một bảng là dictionary.
//version = 1: phiên bản, dùng cho việc nâng cấp database sau này.
public abstract class AppDatabase extends RoomDatabase {
    //abstract class AppDatabase mở rộng từ RoomDatabase, là lớp cơ sở cho việc truy cập vào database.
    //RoomDatabase là lớp gốc cho database của bạn.
    //
    //Bạn phải kế thừa và định nghĩa các DAO mà bạn muốn sử dụng.
    public abstract DictionaryDao dictionaryDao();
    //Room sẽ tự động sinh code để triển khai DAO này.
    //
    //Phương thức này cho phép gọi các truy vấn như insertAll(), searchWords(), v.v.

    //AppDatabase chỉ được tạo ra duy nhất một lần trong toàn bộ ứng dụng
    private static volatile AppDatabase INSTANCE;
    //volatile: đảm bảo rằng các thay đổi đối với biến INSTANCE sẽ được nhìn thấy ngay lập tức bởi tất cả các luồng.
    //Nếu không có volatile, có thể xảy ra tình trạng một luồng đọc giá trị cũ của INSTANCE trước
    // khi luồng khác ghi giá trị mới vào INSTANCE.
    //INSTANCE là biến static để giữ instance duy nhất của AppDatabase.
    //
    //volatile: từ khóa quan trọng để đảm bảo mọi thread luôn nhìn thấy phiên bản mới nhất của INSTANCE.
    public static AppDatabase getDatabase(final Context context) {
        //Nếu INSTANCE đã được khởi tạo, trả về INSTANCE
        //instance là 1 đối tượng đại diện cho toàn bộ csdl, khi cần thao tác thêm, xóa, tìm thì sẽ gọi qua instance
        //Nếu chưa khởi tạo, khởi tạo INSTANCE
        //Nếu INSTANCE == null, tức là chưa khởi tạo

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) { //để đảm bảo rằng chỉ có một luồng(thread) duy nhất
                // có thể khởi tạo INSTANCE, tránh tạo nhiều instance

                if (INSTANCE == null) {
                    //Vì có thể 1 thread khác đã tạo instance trong lúc thread này đang chờ synchronized.
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "dictionary_db"
                    ).build();
                    //Tạo database với tên là dictionary_db
                    //context.getApplicationContext(): lấy context của ứng dụng, không phải của activity
                    //Room.databaseBuilder(): tạo một builder để xây dựng database
                    //AppDatabase.class: lớp cơ sở cho database

                }
            }
        }
        return INSTANCE;
        //Trả về instance của AppDatabase
    }
}