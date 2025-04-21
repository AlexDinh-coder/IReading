package com.example.iread.OpenBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Book.ActivityBook;
import com.example.iread.Model.BookChapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);

        initArguments();
        initViews(view);
        initApi();
        fetchChapters();

        return view;
    }

    private void initArguments() {
        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getInt("bookId", -1);
            bookTypeStatus = args.getInt("bookTypeStatus", 0);
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
        if (bookId == -1) return;

        iAppApiCaller.getListByBookId(bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
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
        Log.d("CHAPTER_CHECK", "bookTypeStatus = " + bookTypeStatus);
        chapterList.clear();

        for (BookChapter chapter : allChapters) {
            Log.d("CHAPTER_CHECK", "Chapter: " + chapter.getChapterName() + ", BookType = " + chapter.getBookType());

            // Chỉ lấy chương đã duyệt (type = 1) và thuộc loại FREE hoặc PAID
            if ((chapter.getBookType() == 0 || chapter.getBookType() == 1)) {
                chapterList.add(chapter);
            }

        }

        sortChapterList();
        totalChapters.setText(chapterList.size() + " chương");
        setupRecyclerView();
    }



    private void sortChapterList() {
        if (isAscending) {
            Collections.sort(chapterList, Comparator.comparing(BookChapter::getChaperId));
            sortOrderView.setText("Cũ nhất");
        } else {
            Collections.sort(chapterList, (c1, c2) -> c2.getChaperId() - c1.getChaperId());
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
        chapterAdapter = new ChapterAdapter(position -> {
            Intent intent = new Intent(getContext(), ActivityBook.class);
            intent.putExtra("selectedIndex", position);
            intent.putExtra("chapterList", new ArrayList<>(chapterList));
            intent.putExtra("bookTypeStatus", bookTypeStatus); // truyền để phân biệt khi mở chương
            startActivity(intent);
        }, getContext(), chapterList, bookTypeStatus);
        recyclerView.setAdapter(chapterAdapter);
    }
}
