package com.example.app_dictionary_ev;//package com.example.app_dictionary_ev;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class VocabHisHolder extends RecyclerView.ViewHolder {

    TextView word, pronounce, pos, meaning;
    ImageButton btnAudio;
    CheckBox checkBox;
    public VocabHisHolder(View itemView) {
        super(itemView);
        word = itemView.findViewById(R.id.tvWord);
        pronounce = itemView.findViewById(R.id.tvPronounce);
        pos = itemView.findViewById(R.id.tvPos);
        meaning = itemView.findViewById(R.id.tvMeaning);
        btnAudio = itemView.findViewById(R.id.btnAudio);
        checkBox = itemView.findViewById(R.id.checkBox);
    }
}
