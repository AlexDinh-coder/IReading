package com.example.iread.OpenBook;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Audio.AudioActivity;
import com.example.iread.Book.ActivityBook;
import com.example.iread.Interface.OnChapterClickListener;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.Model.UserTranscationBook;
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

    private boolean isBookPurchased;

    private String username, userId;

    private int bookId;
    //private List<BookChapter> chapterList;


    public ChapterAdapter(OnChapterClickListener onChapterClickListener, Context context, List<BookChapter> chapterList, int viewId, int bookId) {
        this.onChapterClickListener = onChapterClickListener;
        this.context = context;
        this.chapterList = chapterList;
        this.viewId = viewId;
        this.bookId = bookId;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getString("userId", "");
        //bookId = chapterList.isEmpty() ? -1 : chapterList.get(0).getBookId();
        isBookPurchased = sharedPreferences.getBoolean("isPurchase_" + bookId, false);
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
            holder.chapterLabel.setText(String.valueOf(bookChapter.getPrice()));
            holder.chapterLabel.setBackgroundResource(R.drawable.bg_label_paid);
        } else {
            holder.chapterLabel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (bookChapter == null) return;

            if (!isBookPurchased) {
                Toast.makeText(context, "Bạn cần mua sách trước khi đọc chương!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (viewId == 1) {
                // Sách nghe → mở AudioActivity
                Intent intent = new Intent(context, AudioActivity.class);
                intent.putExtra("bookId", bookChapter.getBookId());
                intent.putExtra("chapterId", bookChapter.getId());
                intent.putExtra("chapterList", (Serializable) chapterList);
                intent.putExtra("selectedIndex", position);
                context.startActivity(intent);
            } else {
                if (bookChapter.getBookType() == 1 && !isChapterUnlocked(bookChapter.getId())) {
                    // Nếu chương chưa mở khoá → hiển thị dialog
                    showUnlockDialog(bookChapter);
                } else {
                    // Nếu chương miễn phí hoặc đã mở khoá → mở luôn
                    if (onChapterClickListener != null) {
                        if (position != 0) {
                            sendViewStatus(bookChapter, 0, viewId);
                        }
                        onChapterClickListener.onChapterClick(position);
                    }
                }

            }
        });


    }

    private boolean isChapterUnlocked(String chapterId) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean(chapterId, false);

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
    private void showUnlockDialog(BookChapter chapter) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.overlay, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        Button btnUnlock = dialogView.findViewById(R.id.btnUnlock);

        int price = chapter.getPrice();
        // Lấy số xu hiện tại từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int currentCoin = prefs.getInt("coin", 0);

        tvTitle.setText(price + " xu để mở khoá chapter!");
        tvContent.setText("Số xu hiện tại của bạn là " + currentCoin + " bạn cần " + price + " xu để mở khoá chapter này!");

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnUnlock.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", "");
            String username = sharedPreferences.getString("username", "");

            UserTranscationBook transcationBook = new UserTranscationBook();
            transcationBook.setAmount(chapter.getPrice());
            transcationBook.setChapterId(chapter.getId());
            transcationBook.setBookId(chapter.getBookId());
            transcationBook.setUserId(userId);
            transcationBook.setUsername(username);

            apiCaller.postPaymentItem(transcationBook).enqueue(new Callback<ReponderModel<String>>() {
                @Override
                public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                        saveUnlockedChapter(chapter.getId());
                        dialog.dismiss();

                        //Mở chương sách khi mở khoá thành công
                        if (onChapterClickListener != null) {
                            onChapterClickListener.onChapterClick(chapterList.indexOf(chapter));
                        }

                        //Trừ xu hiển thị trong sharePreference
                        int newCoin = sharedPreferences.getInt("coin", 0) - chapter.getPrice();
                        sharedPreferences.edit().putInt("coin", newCoin).apply();
                    } else {
                        tvContent.setText("Không thể mở khoá chương.Vui lòng thử lại");
                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                    Log.e("UnlockChapter", "Lỗi khi trừ xu: " + t.getMessage());
                }
            });

        });

        dialog.show();
    }

    private void saveUnlockedChapter(String chapterId) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(chapterId, true).apply();

    }

    public void updateData(List<BookChapter> newList) {
        this.chapterList = newList;
        notifyDataSetChanged();
    }

}
