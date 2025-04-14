package com.example.app_dictionary_ev;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VocabHisHolder extends RecyclerView.ViewHolder {
    TextView word, pronounce, pos, meaning;
    public VocabHisHolder(@NonNull View itemView) {

        super(itemView);
        word = itemView.findViewById(R.id.tvWord);
        pronounce = itemView.findViewById(R.id.tvPronounce);
        pos = itemView.findViewById(R.id.tvPos);
        meaning = itemView.findViewById(R.id.tvMeaning);
    }
}
