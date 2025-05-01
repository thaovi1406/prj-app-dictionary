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
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {
    private List<DictionaryEntry> suggestions = new ArrayList<>();
    private OnSuggestionClickListener listener;
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
        holder.tvMeaning.setText(entry.pos + " " + entry.meanings.get(0).definition);

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