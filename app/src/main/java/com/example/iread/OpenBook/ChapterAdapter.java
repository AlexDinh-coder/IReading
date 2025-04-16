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

import com.example.iread.Audio.AudioActivity;
import com.example.iread.Book.ActivityBook;
import com.example.iread.Interface.OnChapterClickListener;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.io.Serializable;
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
        //holder.chapterLabel.setVisibility(View.VISIBLE);
        int bookType = bookChapter.getBookType();
        if (bookType == 0) {
            holder.chapterLabel.setVisibility(View.VISIBLE);
            holder.chapterLabel.setText("FREE");
            holder.chapterLabel.setBackgroundResource(R.drawable.bg_label_free);
        } else if (bookType == 1) {
            holder.chapterLabel.setVisibility(View.VISIBLE);
            holder.chapterLabel.setText("PAID");
            holder.chapterLabel.setBackgroundResource(R.drawable.bg_label_paid);
        } else {
            // Nếu là Pending (2) hoặc Declined (3) thì ẩn nhãn
            holder.chapterLabel.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            if (bookChapter == null) return;

            if (viewId == 1) {
                // Sách nghe → mở AudioActivity
                Intent intent = new Intent(context, AudioActivity.class);
                intent.putExtra("bookId", bookChapter.getBookId());
                intent.putExtra("chapterId", bookChapter.getId());
                intent.putExtra("chapterList", (Serializable) chapterList);
                intent.putExtra("selectedIndex", position);
                context.startActivity(intent);
            } else {
                // Sách đọc → tracking + callback
                if (onChapterClickListener != null) {
                    if (position != 0) {
                        sendViewStatus(bookChapter, 0, viewId);
                    }
                    onChapterClickListener.onChapterClick(position);
                }
            }
        });


    }
    private void setupApiCaller() {
       apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
    }


    private void sendViewStatus(BookChapter chapter, int status, int id) {
        if (chapter == null) return;

        int bookId = chapter.getBookId();

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
