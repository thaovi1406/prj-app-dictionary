package com.example.app_dictionary_ev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder> {
    private List<Meaning> meanings;

    public MeaningAdapter(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    @NonNull
    @Override
    public MeaningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meaning, parent, false);
        return new MeaningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningViewHolder holder, int position) {
        Meaning meaning = meanings.get(position);
        holder.tvDefinition.setText("➜ " + meaning.getDefinition());

        if (meaning.getExample() != null && !meaning.getExample().isEmpty()) {
            holder.tvExample.setText(meaning.getExample());
            holder.tvExample.setVisibility(View.VISIBLE);
        } else {
            holder.tvExample.setVisibility(View.GONE);
        }

        if (meaning.getNote() != null && !meaning.getNote().isEmpty()) {
            holder.tvNote.setText(meaning.getNote());
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return meanings != null ? meanings.size() : 0;
    }

    static class MeaningViewHolder extends RecyclerView.ViewHolder {
        TextView tvDefinition;
        TextView tvExample;
        TextView tvNote;

        MeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDefinition = itemView.findViewById(R.id.tvDefinition);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}