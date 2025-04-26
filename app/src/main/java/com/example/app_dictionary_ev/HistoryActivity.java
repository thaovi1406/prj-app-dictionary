package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Lịch sử");

        RecyclerView recyclerView = findViewById(R.id.rvVocab);

        List<VocabHisModal> items = new ArrayList<VocabHisModal>();
        items.add(new VocabHisModal("barrack","/[ˈbærək]/", "(n)", "hét to để phản đối hoặc chế giễu, la ó"));
        items.add(new VocabHisModal("attention","/[ˈbærək]/", "(n)", "hét to để phản đối hoặc chế giễu, la ó"));
        items.add(new VocabHisModal("name","/[ˈbærək]/", "(n)", "hét to để phản đối hoặc chế giễu, la ó"));
        items.add(new VocabHisModal("new","/[ˈbærək]/", "(n)", "hét to để phản đối hoặc chế giễu, la ó"));
        items.add(new VocabHisModal("old","/[ˈbærək]/", "(n)", "hét to để phản đối hoặc chế giễu, la ó"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(new VocabAdapter(getApplicationContext(),items));

        ImageButton btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}
