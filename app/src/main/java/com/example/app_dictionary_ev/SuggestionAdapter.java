
package com.example.app_dictionary_ev;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.ArrayList;
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
    }

    public interface OnSuggestionClickListener {
        void onSuggestionClick(DictionaryEntry entry);
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultsearch, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        DictionaryEntry entry = suggestions.get(position);
        holder.tvWord.setText(entry.word);

        // Chuyển đổi từ loại sang dạng rút gọn
        String shortPos = POS_SHORT_FORM.getOrDefault(entry.pos, entry.pos);
        String definition = entry.meanings != null && !entry.meanings.isEmpty()
                ? entry.meanings.get(0).definition
                : "Không có định nghĩa";

        holder.tvMeaning.setText(String.format("(%s) %s", shortPos, definition));

        // Xử lý sự kiện click cho TextView (dòng chữ)
        holder.tvWord.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(entry);
            }
        });

        // Xử lý sự kiện click cho ImageView (mũi tên)
        holder.ivArrow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(entry);
            }
        });
        // Cấu hình sự kiện click cho các view khác nếu cần
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(entry);
            }
        });
        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
        boolean isFavorite = dbHelper.isFavorite(entry.word);
        holder.ivHeart.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        //vi
        holder.ivHeart.setOnClickListener(v -> {
            DatabaseHelper dbHelper1 = new DatabaseHelper(holder.itemView.getContext());
            boolean currentlyFavorite = dbHelper1.isFavorite(entry.word);

            if (currentlyFavorite) {
                dbHelper1.removeFromFavorites(entry.word);
                holder.ivHeart.setImageResource(R.drawable.ic_heart);
                Toast.makeText(holder.itemView.getContext(), "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper1.addToFavorites(entry);
                holder.ivHeart.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(holder.itemView.getContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
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
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMeaning;
        ImageView ivArrow; // Đảm bảo ánh xạ ivArrow
        ImageView ivHeart;
        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord); // Khai báo tvWord
            tvMeaning = itemView.findViewById(R.id.tvMeaning); // Khai báo tvMeaning
            ivArrow = itemView.findViewById(R.id.ivArrow); // Khai báo ivArrow
            ivHeart = itemView.findViewById(R.id.ivHeart); // Khai báo ivHeart
        }

    }
}
