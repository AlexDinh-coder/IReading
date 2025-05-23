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
import android.widget.ImageView;
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
import java.text.DecimalFormat;
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


    private ImageView btnClose;

    private String username, userId;

    private int bookId;

    private int bookTypeStatus;

    private List<String> unlockedChapterIds;

    private long userCoin;

    private boolean isBookPurchased;

    private int bookPrice;



    public ChapterAdapter(OnChapterClickListener onChapterClickListener, Context context, List<BookChapter> chapterList, int viewId, int bookId, int bookTypeStatus,List<String> unlockedChapterIds, long userCoin, boolean isBookPurchased, int bookPrice) {
        this.onChapterClickListener = onChapterClickListener;
        this.context = context;
        this.chapterList = chapterList;
        this.viewId = viewId;
        this.bookId = bookId;
        this.bookTypeStatus = bookTypeStatus;
        this.unlockedChapterIds = unlockedChapterIds;
        this.userCoin = userCoin;
        this.isBookPurchased = isBookPurchased;
        this.bookPrice = bookPrice;

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
        BookChapter bookChapter = chapterList.get(position);
        holder.chapterTitle.setText(bookChapter.getChapterName());

        int priceToDisplay = (bookTypeStatus == 0)
                ? bookChapter.getPrice()
                : bookChapter.getPriceVoice();

        boolean isPaid = (bookTypeStatus == 0)
                ? bookChapter.isPaidChapter()
                : bookChapter.isPaidVoice();

        if (priceToDisplay == 0) {
            setLabel(holder, "FREE", R.drawable.bg_label_free);
        } else if (isPaid) {
            setLabel(holder, "ĐÃ MUA", R.drawable.bg_label_purchase);
        } else {
            setLabel(holder, formatXu(priceToDisplay), R.drawable.bg_label_paid);
        }
        holder.itemView.setOnClickListener(v -> {
            if (bookChapter == null) return;

            if (username == null || username.isEmpty() || userId == null || userId.isEmpty()) {
                Toast.makeText(context, "Bạn cần đăng nhập để đọc hoặc nghe sách!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu sách có giá toàn bộ
            if (bookPrice > 0 && !isBookPurchased) {
                Toast.makeText(context, "Bạn cần mua sách để truy cập các chương!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra điều kiện mở chương
            boolean isChapterPaid = (bookTypeStatus == 0)
                    ? bookChapter.isPaidChapter()
                    : bookChapter.isPaidVoice();

            int chapterPrice = (bookTypeStatus == 0)
                    ? bookChapter.getPrice()
                    : bookChapter.getPriceVoice();

            if (chapterPrice == 0 || isChapterPaid) {
                openChapter(position, bookChapter);
            } else {
                showUnlockDialog(bookChapter);
            }
        });
    }


    private void openChapter(int position, BookChapter bookChapter) {
       // sendViewStatus(bookChapter, 0, viewId);

        Intent intent;
        if (bookTypeStatus == 1) {
            intent = new Intent(context, AudioActivity.class);
            intent.putExtra("chapterId", bookChapter.getId());
            intent.putExtra("chapterList", new ArrayList<>(chapterList));
            intent.putExtra("bookId", bookId);
        } else {
            ChapterDataHolder.getInstance().setChapterList(chapterList);
            intent = new Intent(context, ActivityBook.class);
            intent.putExtra("selectedIndex", position);
            intent.putExtra("bookTypeStatus", bookTypeStatus);
            intent.putExtra("bookId", bookId);
        }
        context.startActivity(intent);
    }


    private void setLabel(ChapterViewHolder holder, String text, int backgroundResource) {
        holder.chapterLabel.setVisibility(View.VISIBLE);
        holder.chapterLabel.setText(text);
        holder.chapterLabel.setBackgroundResource(backgroundResource);
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
        model.setBookTypeStatus(bookTypeStatus);
        model.setCreateBy(username);
        model.setStatus(status);
        model.setUserId(userId);

        apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<Integer>>() {
            @Override
            public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewId = response.body().getData();
                }
                Log.d("BookTracking", "Đã gửi trạng thái " + status + " cho chương " + chapter.getChapterNumber());
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
        // Khởi tạo Dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        Button btnUnlock = dialogView.findViewById(R.id.btnUnlock);
        btnClose = dialogView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        int price = (bookTypeStatus == 0)
                ? chapter.getPrice()
                : chapter.getPriceVoice();

        tvTitle.setText(formatXu(price) + " để mở khoá chapter!");
        tvContent.setText("Số xu hiện tại của bạn là " + formatXu(userCoin) + ", bạn cần " + formatXu(price) + " để mở khoá chapter này!");

        btnUnlock.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            String userId = sharedPreferences.getString("userId", "");

            apiCaller.checkEnoughCoins(username, price).enqueue(new Callback<ReponderModel<Integer>>() {
                @Override
                public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ReponderModel<Integer> result = response.body();

                        if (!result.isSussess()) {
                            int balance = result.getData(); // Số xu hiện có
                            if (balance < 0) balance = 0;
                            Toast.makeText(context, "Số dư không đủ! Bạn có: " + balance + " xu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Đủ xu → tiếp tục thanh toán
                        UserTranscationBook transcationBook = new UserTranscationBook();
                        transcationBook.setAmount(price);
                        transcationBook.setChapterId(chapter.getId());
                        transcationBook.setBookId(bookId);
                        transcationBook.setUserId(userId);
                        transcationBook.setUsername(username);
                        transcationBook.setType(bookTypeStatus);

                        apiCaller.postPaymentItem(transcationBook).enqueue(new Callback<ReponderModel<String>>() {
                            @Override
                            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                                    dialog.dismiss();
                                    //Trạng thái mua chương
                                    if (bookTypeStatus == 0) {
                                        chapter.setPaidChapter(true);
                                    } else if (bookTypeStatus == 1) {
                                        chapter.setPaidVoice(true);
                                    }
                                    //Cap nhap giao dien chuong  -> đã mua
                                    notifyItemChanged(chapterList.indexOf(chapter));
                                    //Vào đọc sau khi paid
                                    if (onChapterClickListener != null) {
                                        onChapterClickListener.onChapterClick(chapterList.indexOf(chapter));
                                    }

                                } else {
                                    tvContent.setText(response.body().getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                                Log.e("UnlockChapter", "Lỗi khi trừ xu: " + t.getMessage());
                            }
                        });

                    } else {
                        Toast.makeText(context, "Không thể kiểm tra xu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kiểm tra xu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
    private String formatXu(long value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " xu";
    }

    public void updateData(List<BookChapter> newList) {
        this.chapterList = newList;
        notifyDataSetChanged();
    }

}
