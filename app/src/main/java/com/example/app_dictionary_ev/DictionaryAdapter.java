package com.example.app_dictionary_ev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> {
    private List<DictionaryEntry> data = new ArrayList<>();

    public void setData(List<DictionaryEntry> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DictionaryDiffCallback(this.data, newData));
        this.data.clear();
        this.data.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DictionaryEntry entry = data.get(position);
        holder.wordTextView.setText(entry.getWord());

        // Hiển thị nghĩa đầu tiên (nếu có)
        List<DictionaryEntry.Meaning> meanings = entry.getMeanings();
        if (meanings != null && !meanings.isEmpty()) {
            holder.meaningTextView.setText(meanings.get(0).getDefinition());
        } else {
            holder.meaningTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;
        TextView meaningTextView;

        ViewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(android.R.id.text1);
            meaningTextView = itemView.findViewById(android.R.id.text2);
        }
    }

    static class DictionaryDiffCallback extends DiffUtil.Callback {
        private final List<DictionaryEntry> oldList;
        private final List<DictionaryEntry> newList;

        DictionaryDiffCallback(List<DictionaryEntry> oldList, List<DictionaryEntry> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getWord().equals(newList.get(newItemPosition).getWord());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}