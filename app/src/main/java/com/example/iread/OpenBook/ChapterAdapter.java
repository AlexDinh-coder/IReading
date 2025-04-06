package com.example.iread.OpenBook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.BookChapter;
import com.example.iread.R;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<BookChapter> chapterList;
    //private List<BookChapter> chapterList;


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
       // Log.d("ChapterAdapter", "Binding chương: " + bookChapter.getChapterName());

        //holder.chapterNumber.setText(String.valueOf(position + 1));
        holder.chapterTitle.setText(bookChapter.getChapterName());


        // Hiện hoặc ẩn label FREE (ở đây mặc định luôn hiện, bạn có thể thêm điều kiện)
        holder.chapterLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle, chapterLabel;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
          //  chapterNumber = itemView.findViewById(R.id.chapter_number);
            chapterTitle = itemView.findViewById(R.id.chapter_title);
            chapterLabel = itemView.findViewById(R.id.chapter_label);
        }
    }
    public void updateData(List<BookChapter> newList) {
        this.chapterList = newList;
        notifyDataSetChanged();
    }

}
