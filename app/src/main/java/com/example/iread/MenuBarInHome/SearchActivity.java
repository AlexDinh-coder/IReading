package com.example.iread.MenuBarInHome;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.Book;
import com.example.iread.Model.BookSearch;
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

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerViewBooks;
    BookAdapter bookAdapter;
    List<Book> bookList = new ArrayList<>();

    EditText edtSearch;

    private IAppApiCaller iAppApiCaller;


    ImageView btnBack, btnSearch;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        makeStatusBarTransparent();
        applyTopPadding();
        recyclerViewBooks = findViewById(R.id.recyclerSearch);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        edtSearch = findViewById(R.id.etSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keyword = charSequence.toString().trim();
                if (!keyword.isEmpty()) {
                    searchBook(keyword);
                }else {
                   bookList.clear();
                   bookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        edtSearch.setOnEditorActionListener((v, actionId, even) -> {
//            String keyword = edtSearch.getText().toString().trim();
//            if (!keyword.isEmpty()) {
//                searchBook(keyword);
//            }
//            return true;
//        });
//        btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(v -> {
//            String keyword = edtSearch.getText().toString().trim();
//            if (!keyword.isEmpty()) {
//                searchBook(keyword);
//            }
//        });

        // Gắn adapter
        bookAdapter = new BookAdapter(bookList, this);
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void searchBook(String query) {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.searchBook(query).enqueue(new Callback<ReponderModel<BookSearch>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookSearch>> call, Response<ReponderModel<BookSearch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookSearch> bookSearchList = response.body().getDataList();
                    if (bookSearchList != null) {
                        bookList.clear();
                        for (BookSearch search : bookSearchList) {
                            Book book = new Book();
                            book.setId(search.getId());
                            book.setName(search.getName());
                            book.setPoster(search.getPoster());
                            book.setSummary(search.getSummary());
                            book.setBookType(search.getBookType());
                            book.setStatus(search.getStatus());
                            book.setAgeLimitType(search.getAgeLimitType());
                            book.setCreateDate(search.getCreateDate());
                            book.setModifyDate(search.getModifyDate());
                            book.setCreateBy(search.getCreateBy());
                            book.setUserId(search.getUserId());
                            book.setListCategories(search.getListCategory());
                            bookList.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookSearch>> call, Throwable t) {
                Log.e("SearchAPI", "Lỗi tìm kiếm: " + t.getMessage());
            }
        });
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }
    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.barContainer);
        if (contentContainer != null) {
            int statusBarHeight = getStatusBarHeight();
            contentContainer.setPadding(0, statusBarHeight, 0, 0);
        }
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}