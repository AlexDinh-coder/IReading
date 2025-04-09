package com.example.iread.OpenBook;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.iread.Model.BookViewModel;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private OnChapterClickListener onChapterClickListener;
    private Context context;
    private List<BookChapter> chapterList;

    private IAppApiCaller apiCaller;

    private Integer viewId = 0;

    private String username, userId;
    //private List<BookChapter> chapterList;


    public ChapterAdapter(OnChapterClickListener onChapterClickListener, Context context, List<BookChapter> chapterList, int viewId) {
        this.onChapterClickListener = onChapterClickListener;
        this.context = context;
        this.chapterList = chapterList;
        this.viewId = viewId;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getString("userId", "");
    }

    // Constructor đơn giản, không xử lý click (dùng cho ChapterFragment)
//    public ChapterAdapter(Context context, List<BookChapter> chapterList) {
//        this.context = context;
//        this.chapterList = chapterList;
//    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        setupApiCaller();
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        BookChapter bookChapter= chapterList.get(position);
        holder.chapterTitle.setText(bookChapter.getChapterName());
        holder.chapterLabel.setVisibility(View.VISIBLE);


        holder.itemView.setOnClickListener(v -> {
            if (onChapterClickListener != null) {
                if (position != 0) {
                    sendViewStatus(bookChapter,0,viewId);
                }
                onChapterClickListener.onChapterClick(position);
            }
        });

    }
    private void setupApiCaller() {
       apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
    }


    private void sendViewStatus(BookChapter chapter, int status, int id) {
        if (chapter == null || chapter.getBookId() == null || chapter.getBookId().trim().isEmpty()) return;

        int bookId;
        try {
            bookId = Integer.parseInt(chapter.getBookId());
        } catch (NumberFormatException e) {
            Log.e("BookTracking", "bookId không hợp lệ: " + chapter.getBookId());
            return;
        }

        BookViewModel model = new BookViewModel();
        model.setId(id);
        model.setBookId(bookId);
        model.setChapterId(chapter.getId());
        model.setBookTypeStatus(0);
        model.setCreateBy(username);
        model.setStatus(status);
        model.setUserId(userId);

        apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<Integer>>() {
            @Override
            public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewId = response.body().getData();

                }
                Log.d("BookTracking", "Đã gửi trạng thái " + status + " cho chương " + chapter.getChaperId());
            }

            @Override
            public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                Log.e("BookTracking", "Lỗi gửi trạng thái: " + t.getMessage());
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
