package com.example.app_dictionary_ev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {
    private List<String> suggestions;
    private boolean[] likedStates;

    public SuggestionAdapter(List<String> suggestions) {
        this.suggestions = suggestions;
        this.likedStates = new boolean[suggestions.size()];
    }

    // Thêm phương thức để cập nhật dữ liệu
    public void updateSuggestions(List<String> newSuggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(newSuggestions);
        this.likedStates = new boolean[suggestions.size()];
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.tvSuggestion.setText(suggestions.get(position));
        holder.ivHeart.setImageResource(likedStates[position] ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        holder.ivHeart.setOnClickListener(v -> {
            likedStates[position] = !likedStates[position];
            holder.ivHeart.setImageResource(likedStates[position] ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSuggestion;
        ImageView ivHeart;
        ImageView ivArrow;
        ImageView ivSearchIcon;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuggestion = itemView.findViewById(R.id.tvSuggestion);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            ivSearchIcon = itemView.findViewById(R.id.ivSearchIcon);
        }
    }
}