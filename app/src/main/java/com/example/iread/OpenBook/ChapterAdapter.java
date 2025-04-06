package com.example.iread.OpenBook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Book.ActivityBook;
import com.example.iread.Interface.OnChapterClickListener;
import com.example.iread.Model.BookChapter;
import com.example.iread.R;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private OnChapterClickListener onChapterClickListener;
    private Context context;
    private List<BookChapter> chapterList;
    //private List<BookChapter> chapterList;


    public ChapterAdapter(OnChapterClickListener onChapterClickListener, Context context, List<BookChapter> chapterList) {
        this.onChapterClickListener = onChapterClickListener;
        this.context = context;
        this.chapterList = chapterList;
    }

    // Constructor đơn giản, không xử lý click (dùng cho ChapterFragment)
    public ChapterAdapter(Context context, List<BookChapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        BookChapter bookChapter= chapterList.get(position);
        holder.chapterTitle.setText(bookChapter.getChapterName());
        holder.chapterLabel.setVisibility(View.VISIBLE);
        // Đặt sự kiện click ở đây
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ActivityBook.class);
//            intent.putExtra("selectedIndex", position);
//            intent.putExtra("chapterContent", bookChapter.getContent());
//            context.startActivity(intent);
//        });


        holder.itemView.setOnClickListener(v -> {
            if (onChapterClickListener != null) {
                onChapterClickListener.onChapterClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle, chapterLabel;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.chapter_title);
            chapterLabel = itemView.findViewById(R.id.chapter_label);
        }
    }
    public void updateData(List<BookChapter> newList) {
        this.chapterList = newList;
        notifyDataSetChanged();
    }

}
