package com.example.app_dictionary_ev;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder> {

    private final Context context;
    private final List<VocabModel> items;
    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false;

    public VocabAdapter(Context context, List<VocabModel> items) {
        this.context = context;
        this.items = items;
        initTextToSpeech();
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.getDefault());
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("VocabAdapter", "TTS: Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu");
                    Toast.makeText(context, "Ngôn ngữ Text-to-Speech không được hỗ trợ", Toast.LENGTH_SHORT).show();
                } else {
                    isTtsInitialized = true;
                }
            } else {
                Log.e("VocabAdapter", "TTS: Khởi tạo thất bại");
                Toast.makeText(context, "Khởi tạo Text-to-Speech thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VocabModel item = items.get(position);

        holder.word.setText(item.getWord());
        holder.pronounce.setText(item.getPronunciation());
        holder.pos.setText(item.getPos());
        holder.meaning.setText(item.getFirstMeaning());

        // Màu nền xen kẽ sử dụng tài nguyên
        int backgroundColor = position % 2 == 0
                ? ContextCompat.getColor(context, R.color.light_blue)
                : ContextCompat.getColor(context, R.color.white);
        holder.itemView.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        shutdown();
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView word, pronounce, pos, meaning;
        ImageButton btnAudio;

        public ViewHolder(@NonNull View itemView, VocabAdapter adapter) {
            super(itemView);
            word = itemView.findViewById(R.id.tvWord);
            pronounce = itemView.findViewById(R.id.tvPronounce);
            pos = itemView.findViewById(R.id.tvPos);
            meaning = itemView.findViewById(R.id.tvMeaning);
            btnAudio = itemView.findViewById(R.id.btnAudio);

            btnAudio.setContentDescription("Phát âm từ");

            btnAudio.setOnClickListener(v -> {
                if (adapter.isTtsInitialized && adapter.textToSpeech != null) {
                    adapter.textToSpeech.speak(
                            adapter.items.get(getAdapterPosition()).getWord(),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                    );
                } else {
                    Toast.makeText(itemView.getContext(), "Text-to-Speech không khả dụng", Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(v -> {
                VocabModel item = adapter.items.get(getAdapterPosition());
                Intent intent = new Intent(itemView.getContext(), ResultActivity.class);
                Gson gson = new Gson();
                String vocabJson = gson.toJson(item);
                Log.d("VocabAdapter", "Sending vocabJson: " + vocabJson); // Gỡ lỗi
                intent.putExtra("vocab", vocabJson);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}