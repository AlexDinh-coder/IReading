package com.example.iread.OpenBook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Audio.AudioActivity;
import com.example.iread.Book.ActivityBook;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.UserProfile;
import com.example.iread.Model.UserTranscationBookModel;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private TextView totalChapters, sortOrderView;
    private boolean isAscending = true;
    private List<BookChapter> chapterList = new ArrayList<>();
    private IAppApiCaller iAppApiCaller;
    private int bookTypeStatus = 0; // 0 - đọc, 1 - nghe
    private int bookId = -1;
    private String bookTitle ="";

    private List<String> unlockedChapterIds = new ArrayList<>();
    private long userCoin;

    private int bookPrice;
    private boolean isBookPurchased = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);


        initArguments();
        initViews(view);
        initApi();
       // checkBookPurchasedFromServer(this::fetchChapters);
        checkBookPurchasedFromServer(() -> {
            Log.d("CheckPurchased", "Đã kiểm tra xong, giá trị isBookPurchased = " + isBookPurchased);
            fetchChapters();
        });


        return view;
    }

    private void initArguments() {
        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getInt("bookId", -1);
            bookTypeStatus = args.getInt("bookTypeStatus", 0);
            bookTitle = args.getString("bookTitle", "");
            bookPrice = args.getInt("bookPrice",0);
            isBookPurchased = args.getBoolean("isPurchase", false);
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rcv_chapter_in_open_book);
        totalChapters = view.findViewById(R.id.totalChapters);
        sortOrderView = view.findViewById(R.id.sortOlderView);

        sortOrderView.setOnClickListener(v -> {
            toggleSortOrder();
        });
    }

    private void initApi() {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
    }

    private void fetchChapters() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (bookId == -1) return;

        iAppApiCaller.getListBookChapterByUsername(username,bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> allChapters = response.body().getDataList();

                    if (allChapters != null) {
                        filterAndShowChapters(allChapters);
                    }

                } else {
                    Log.e("ChapterFragment", "Lỗi response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                Log.e("API Book Chapter", "Lỗi khi gọi API book chapter: " + t.getMessage());
            }
        });
    }

    private void filterAndShowChapters(List<BookChapter> allChapters) {
        if (!isAdded()) return;

        chapterList.clear();

        if (bookPrice > 0 && !isBookPurchased) {
            // Ẩn toàn bộ nếu chưa mua sách
            totalChapters.setText("0 chương");
            chapterList.clear();
            setupRecyclerView();
            return;
        }

        if (bookTypeStatus == 1) {
            for (BookChapter chapter : allChapters) {
                if (chapter.getAudioUrl() != null && chapter.getAudioUrl().equals("Audio")) {
                    chapterList.add(chapter);
                }
            }
        } else {
            chapterList = allChapters;
        }

        sortChapterList();
        totalChapters.setText(chapterList.size() + " chương");
        setupRecyclerView();
    }


    private void sortChapterList() {
        if (isAscending) {
            Collections.sort(chapterList, Comparator.comparing(BookChapter::getChapterNumber));
            sortOrderView.setText("Cũ nhất");
        } else {
            Collections.sort(chapterList, (c1, c2) -> c2.getChapterNumber() - c1.getChapterNumber());
            sortOrderView.setText("Mới nhất");
        }
    }

    private void toggleSortOrder() {
        isAscending = !isAscending;
        sortChapterList();
        chapterAdapter.updateData(chapterList);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("CHECK_ADAPTER", "bookPrice: " + bookPrice + ", isBookPurchased: " + isBookPurchased);

        chapterAdapter = new ChapterAdapter(
                position -> {
                    BookChapter chapter = chapterList.get(position);
                    if (bookTypeStatus == 0) {
                        ChapterDataHolder.getInstance().setChapterList(chapterList);
                        Intent intent = new Intent(getContext(), ActivityBook.class);
                        intent.putExtra("selectedIndex", position);
                        intent.putExtra("bookTypeStatus", bookTypeStatus);
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                    } else if (bookTypeStatus == 1) {
                        Intent intent = new Intent(getContext(), AudioActivity.class);
                        intent.putExtra("chapterId", chapter.getId());
                        intent.putExtra("chapterList", new ArrayList<>(chapterList));
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                    }
                },
                requireContext(),
                chapterList,
                0,
                bookId,
                bookTypeStatus,
                unlockedChapterIds,
                userCoin,
                isBookPurchased,
                bookPrice
        );

        recyclerView.setAdapter(chapterAdapter);
    }


    private void checkBookPurchasedFromServer(Runnable onDone) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");

        iAppApiCaller.getListBookChapterByUsername(username, bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();
                    // nếu tất cả chương đều là paidChapter = true => đã mua trọn bộ
                    isBookPurchased = !chapters.isEmpty();
                    for (BookChapter chapter : chapters) {
                        if (!chapter.isPaidChapter()) {
                            isBookPurchased = false;
                            break;
                        }
                    }
                }

                iAppApiCaller.getHistoryPaymentItem(username).enqueue(new Callback<ReponderModel<UserTranscationBookModel>>() {
                    @Override
                    public void onResponse(Call<ReponderModel<UserTranscationBookModel>> call, Response<ReponderModel<UserTranscationBookModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (UserTranscationBookModel item : response.body().getDataList()) {
                                String paymentName = item.getPaymentName().toLowerCase();
                                if (paymentName.startsWith("mở khóa sách") && paymentName.contains(bookTitle.toLowerCase())) {
                                    isBookPurchased = true;
                                    break;
                                }
                            }
                        }

                        iAppApiCaller.getUserProfile(username).enqueue(new Callback<ReponderModel<UserProfile>>() {
                            @Override
                            public void onResponse(Call<ReponderModel<UserProfile>> call, Response<ReponderModel<UserProfile>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    userCoin = response.body().getData().getClamPoint();
                                    Log.d("USER_PROFILE", "Số xu nhận được: " + userCoin);
                                }
                                if (onDone != null) onDone.run();
                            }

                            @Override
                            public void onFailure(Call<ReponderModel<UserProfile>> call, Throwable t) {
                                if (onDone != null) onDone.run();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ReponderModel<UserTranscationBookModel>> call, Throwable t) {
                        if (onDone != null) onDone.run();
                    }
                });
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                if (onDone != null) onDone.run();
            }
        });
    }

}
