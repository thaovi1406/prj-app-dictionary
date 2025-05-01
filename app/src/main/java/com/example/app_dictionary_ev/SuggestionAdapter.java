package com.example.app_dictionary_ev;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {
    private List<DictionaryEntry> suggestions = new ArrayList<>();
    private OnSuggestionClickListener listener;
    private static final Map<String, String> POS_SHORT_FORM = new HashMap<>();
    static {
        POS_SHORT_FORM.put("danh từ", "n");
        POS_SHORT_FORM.put("động từ", "v");
        POS_SHORT_FORM.put("tính từ", "adj");
        POS_SHORT_FORM.put("trạng từ", "adv");
        POS_SHORT_FORM.put("giới từ", "prep");
        POS_SHORT_FORM.put("đại từ", "pron");
        POS_SHORT_FORM.put("liên từ", "conj");
        POS_SHORT_FORM.put("ngoại động từ", "v.t");
        POS_SHORT_FORM.put("thán từ", "interj");
        // Thêm các từ loại khác nếu cần
    }
    public interface OnSuggestionClickListener {
        void onSuggestionClick(DictionaryEntry entry);
    }
    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultsearch, parent, false);
        return new SuggestionViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        DictionaryEntry entry = suggestions.get(position);
        holder.tvWord.setText(entry.word);
//        holder.tvMeaning.setText(entry.pos + " " + entry.meanings.get(0).definition);

        // Chuyển đổi từ loại sang dạng rút gọn
        String shortPos = POS_SHORT_FORM.getOrDefault(entry.pos, entry.pos);
        String definition = entry.meanings != null && !entry.meanings.isEmpty()
                ? entry.meanings.get(0).definition
                : "Không có định nghĩa";

        holder.tvMeaning.setText(String.format("(%s) %s", shortPos, definition));


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(entry);
            }
        });
    }
    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void setSuggestions(List<DictionaryEntry> newSuggestions) {
        suggestions = newSuggestions;
        notifyDataSetChanged();
        Log.d("SuggestionAdapter", "Suggestions updated: " + suggestions.size());
    }
    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMeaning;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
        }
    }
}