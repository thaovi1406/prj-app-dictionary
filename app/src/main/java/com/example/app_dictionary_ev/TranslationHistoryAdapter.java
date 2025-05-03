package com.example.app_dictionary_ev;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TranslationHistoryAdapter extends RecyclerView.Adapter<TranslationHistoryAdapter.ViewHolder> {
    private List<TranslationHistoryModel> historyList;
    private Context context;
    private DatabaseHelper dbHelper;

    public TranslationHistoryAdapter(Context context, List<TranslationHistoryModel> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_his_translated, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TranslationHistoryModel history = historyList.get(position);
        holder.textInput.setText(history.getInputText());
        holder.textTranslated.setText(history.getTranslatedText());

        holder.buttonDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa lịch sử này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        int id = history.getId();
                        boolean success = dbHelper.deleteTranslationHistory(id);
                        if (success) {
                            historyList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, historyList.size());
                        } else {
                            Toast.makeText(context, "Lỗi khi xóa lịch sử", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_translation_history);

            TextView textViewInput = dialog.findViewById(R.id.dialog_textViewInput);
            TextView textViewTranslated = dialog.findViewById(R.id.dialog_textViewTranslated);
            Button buttonClose = dialog.findViewById(R.id.dialog_buttonClose);

            textViewInput.setText(history.getInputText());
            textViewTranslated.setText(history.getTranslatedText());

            buttonClose.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textInput, textTranslated;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textInput = itemView.findViewById(R.id.textViewOriginal);
            textTranslated = itemView.findViewById(R.id.textViewTranslated);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}