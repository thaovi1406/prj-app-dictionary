

package com.example.app_dictionary_ev;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VocabAdapter extends RecyclerView.Adapter<VocabHisHolder> {

    private final Context context;
    private final List<VocabHisModal> items;
    private TextToSpeech textToSpeech;
    private List<VocabHisModal> selectedItems = new ArrayList<>();
    private OnItemCheckListener onItemCheckListener;
    public interface OnItemCheckListener {
        void onItemCheck(VocabHisModal item);
        void onItemUncheck(VocabHisModal item);
    }
    public VocabAdapter(Context context, List<VocabHisModal> items) {
        this.context = context;
        this.items = items;
        initTextToSpeech();
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("VocabAdapter", "TTS: Language not supported or missing data");
                }
            } else {
                Log.e("VocabAdapter", "TTS: Initialization failed");
            }
        });
    }

    @NonNull
    @Override
    public VocabHisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VocabHisHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VocabHisHolder holder, int position) {
        VocabHisModal item = items.get(position);

        holder.word.setText(item.getWord());
        holder.pronounce.setText(item.getPronounce());
        holder.pos.setText(item.getPos());
        holder.meaning.setText(item.getMeaning());

        // Màu nền xen kẽ
        holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#E3F2FD") : Color.WHITE);

        holder.checkBox.setChecked(selectedItems.contains(item));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(item);
                    if (onItemCheckListener != null) {
                        onItemCheckListener.onItemCheck(item);
                    }
                } else {
                    selectedItems.remove(item);
                    if (onItemCheckListener != null) {
                        onItemCheckListener.onItemUncheck(item);
                    }
                }
            }
        });

        // Click mở ResultActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra("word", item.getWord());
            intent.putExtra("pronounce", item.getPronounce());
            intent.putExtra("pos", item.getPos());
            intent.putExtra("meaning", item.getMeaning());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Phát âm từ
        holder.btnAudio.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(item.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }
    public List<VocabHisModal> getSelectedItems() {
        return selectedItems;
    }
    // Thêm phương thức để xóa tất cả các lựa chọn
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged(); // Cập nhật giao diện để bỏ chọn tất cả checkbox
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    public void setOnItemCheckListener(OnItemCheckListener listener) {
        this.onItemCheckListener = listener;
    }
}
