//package com.example.app_dictionary_ev;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class VocabAdapter extends RecyclerView.Adapter<VocabHisHolder> {
//
//    Context context;
//    List<VocabHisModal> items;
//
//    public VocabAdapter(Context context, List<VocabHisModal> items) {
//        this.context = context;
//        this.items = items;
//    }
//
//    @NonNull
//    @Override
//    public VocabHisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new VocabHisHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false) );
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VocabHisHolder holder, int position) {
//        VocabHisModal item = items.get(position);
//
//        holder.word.setText(items.get(position).getWord());
//        holder.pronounce.setText(items.get(position).getPronounce());
//        holder.pos.setText(items.get(position).getPos());
//        holder.meaning.setText(items.get(position).getMeaning());
//
//        if (position % 2 == 0) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD"));
//        } else {
//            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        }
//        holder.itemView.setOnClickListener(v -> {
//            Context context = v.getContext();
//
//            if (context instanceof Activity) {
//                Intent intent = new Intent(context, ResultActivity.class);
//                intent.putExtra("word", item.getWord());
//                intent.putExtra("pronounce", item.getPronounce());
//                intent.putExtra("pos", item.getPos());
//                intent.putExtra("meaning", item.getMeaning());
//                context.startActivity(intent);
//            } else {
//                Intent intent = new Intent(context, ResultActivity.class);
//                intent.putExtra("word", item.getWord());
//                intent.putExtra("pronounce", item.getPronounce());
//                intent.putExtra("pos", item.getPos());
//                intent.putExtra("meaning", item.getMeaning());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//}
//
//package com.example.app_dictionary_ev;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.speech.tts.TextToSpeech;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//import java.util.Locale;
//
//public class VocabAdapter extends RecyclerView.Adapter<VocabHisHolder> {
//
//    private final Context context;
//    private final List<VocabHisModal> items;
//    private TextToSpeech textToSpeech;
//
//    public VocabAdapter(Context context, List<VocabHisModal> items) {
//        this.context = context;
//        this.items = items;
//        initTextToSpeech(); // Khởi tạo TTS 1 lần duy nhất
//    }
//
//    private void initTextToSpeech() {
//        textToSpeech = new TextToSpeech(context, status -> {
//            if (status == TextToSpeech.SUCCESS) {
//                int langResult = textToSpeech.setLanguage(Locale.US);
//                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("VocabAdapter", "TTS: Language not supported or missing data");
//                }
//            } else {
//                Log.e("VocabAdapter", "TTS: Initialization failed");
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public VocabHisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new VocabHisHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VocabHisHolder holder, int position) {
//        VocabHisModal item = items.get(position);
//
//        holder.word.setText(item.getWord());
//        holder.pronounce.setText(item.getPronounce());
//        holder.pos.setText(item.getPos());
//        holder.meaning.setText(item.getMeaning());
//
//        // Thay đổi màu nền xen kẽ
//        holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#E3F2FD") : Color.WHITE);
//
//        // Click item mở ResultActivity
//        holder.itemView.setOnClickListener(v -> {
//            Log.d("VocabAdapter", "Clicked item: " + item.getWord());
//            Intent intent = new Intent(context, ResultActivity.class);
//            intent.putExtra("word", item.getWord());
//            intent.putExtra("pronounce", item.getPronounce());
//            intent.putExtra("pos", item.getPos());
//            intent.putExtra("meaning", item.getMeaning());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent); // Không cần ép kiểu Activity
//        });
//
//        // Phát âm từ
//        holder.btnAudio.setOnClickListener(v -> {
//            if (textToSpeech != null) {
//                textToSpeech.speak(item.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    // Giải phóng TTS khi không còn dùng adapter
//    public void shutdown() {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//            textToSpeech = null;
//        }
//    }
//}
//
//
package com.example.app_dictionary_ev;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class VocabAdapter extends RecyclerView.Adapter<VocabHisHolder> {

    private final Context context;
    private final List<VocabHisModal> items;
    private TextToSpeech textToSpeech;

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
}
