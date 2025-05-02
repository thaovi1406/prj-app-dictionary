package com.example.app_dictionary_ev;

import android.app.Application;
import android.util.Log;

import com.example.app_dictionary_ev.data.db.DatabaseInitializer;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
    }

    private void initDatabase(){
        DatabaseInitializer.populateDatabase(this, new DatabaseInitializer.InitializationCallback() {
            @Override
            public void onComplete(int count) {
                Log.d("Database","Khởi tạo thành công");
            }
            @Override
            public void onError(Exception e) {
                Log.e("Database","Khởi tạo thất bại",e);
            }
        });
    }
}
