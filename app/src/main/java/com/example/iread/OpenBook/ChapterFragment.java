package com.example.iread.OpenBook;

import static android.content.Intent.getIntent;

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
    RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;

    TextView totalChapters, sortOrderView;
    boolean isAscending = true;

    private List<BookChapter> chapterList = new ArrayList<>();


    IAppApiCaller iAppApiCaller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);

        Bundle args = getArguments();
        int bookId = -1;
        if (args != null) {
            bookId = args.getInt("bookId", -1);
        }

        // Gán dữ liệu vào RecyclerView
        recyclerView = view.findViewById(R.id.rcv_chapter_in_open_book);
        getBookChaper(bookId);


        totalChapters = view.findViewById(R.id.totalChapters);

        //Sort view list chapter
        sortOrderView = view.findViewById(R.id.sortOlderView);
        sortOrderView.setOnClickListener(v -> {
            if (chapterList != null && chapterList.size() > 1) {
                isAscending = !isAscending; // Đảo hướng sắp xếp

                if (isAscending) {
                    sortOrderView.setText("Cũ nhất");
                    Collections.sort(chapterList, Comparator.comparing(BookChapter::getChaperId));
                } else {
                    sortOrderView.setText("Mới nhất");
                    Collections.sort(chapterList, (c1, c2) -> c2.getChaperId() - c1.getChaperId());
                }

                chapterAdapter.updateData(chapterList); // Cập nhật lại adapter
            }
        });


        return view;
    }

    private void getBookChaper(int bookId) {
        if (!isAdded()) return; // đảm bảo Fragment đã được attach
        requireContext();
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        iAppApiCaller.getListByBookId(bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDataList() != null) {

                    chapterList = response.body().getDataList(); // dùng biến toàn cục

                    Collections.sort(chapterList, Comparator.comparing(BookChapter::getChaperId));

                    totalChapters.setText(chapterList.size() + " chương");

                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    chapterAdapter = new ChapterAdapter((int position) -> {
                        Intent intent = new Intent(getContext(), ActivityBook.class);
                        intent.putExtra("selectedIndex", position);
                        intent.putExtra("chapterList", new ArrayList<>(chapterList)); // truyền danh sách chương
                        startActivity(intent);



                    }, getContext(), chapterList, 0);
                    recyclerView.setAdapter(chapterAdapter);

                    // Log để kiểm tra dữ liệu
                    for (BookChapter chapter : chapterList) {
                        Log.d("BookChapter", "Chapter name: " + chapter.getChapterName());
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



}
