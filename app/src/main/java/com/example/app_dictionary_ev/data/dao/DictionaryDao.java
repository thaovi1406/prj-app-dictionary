package com.example.app_dictionary_ev.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.List;

@Dao //interface dùng để truy vấn csdl, định nghĩa các thao tác như insert, update, delete, select.

public interface DictionaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //thao tác thêm
    //dữ liệu vào csdl, nếu có dữ liệu trùng thì thay thế
    //Nếu một từ đã tồn tại (dựa vào khóa chính @PrimaryKey trong model), thì sẽ ghi đè (replace) từ đó
        //Tránh lỗi crash do trùng khóa.
    void insertAll(List<DictionaryEntry> entries);

    @Query("SELECT * FROM dictionary WHERE word = :word")
    DictionaryEntry findByWord(String word);
    //truy vấn từ điển theo từ khóa, trả về một đối tượng DictionaryEntry
    //Nếu không tìm thấy từ khóa, sẽ trả về null
    //Từ khóa :word sẽ được thay thế bằng giá trị của biến word trong phương thức này
    //Ví dụ: nếu word = "apple", thì câu truy vấn sẽ là "SELECT * FROM dictionary WHERE word = 'apple'"

    @Query("SELECT * FROM dictionary")
    List<DictionaryEntry> getAll();
    //truy vấn tất cả các từ trong csdl, trả về một danh sách các đối tượng DictionaryEntry
    //Nếu csdl rỗng, sẽ trả về một danh sách rỗng
    //Ví dụ: nếu csdl có 3 từ, thì câu truy vấn sẽ là "SELECT * FROM dictionary" và trả về một danh sách 3 đối tượng DictionaryEntry

    @Query("SELECT COUNT(*) FROM dictionary")
    int getCount();
    //truy vấn số lượng từ trong csdl, trả về một số nguyên


    @Query("SELECT * FROM dictionary WHERE word LIKE :query || '%' LIMIT 20")
    //truy vấn các từ trong csdl có từ khóa bắt đầu bằng query, trả về một danh sách các đối tượng DictionaryEntry
    //LIMIT 20: chỉ lấy tối đa 20 kết quả, giúp tiết kiệm bộ nhớ và tăng hiệu năng -> trả về danh sách
    //các từ có từ khóa bắt đầu bằng query, ví dụ: nếu query = "app", thì câu truy vấn sẽ là "SELECT * FROM dictionary WHERE word LIKE 'app%' LIMIT 20"
    List<DictionaryEntry> searchWords(String query);
}