package com.example.iread;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.Book;
import com.example.iread.Model.Category;
import com.example.iread.OpenBook.BookByCategoryAdapter;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    View contentContainer;
    private RecyclerView rcvBookDetail;
    private IAppApiCaller apiCaller;
    private  Spinner spinner;
    private BookByCategoryAdapter BookByCategoryAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SetColorBar();
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.setStatusBarColor(Color.TRANSPARENT);
        // Gán view cần set padding
        contentContainer = findViewById(R.id.content_detail_container);

        // Tính chiều cao status bar và set padding
        int statusBarHeight = getStatusBarHeight();
        contentContainer.setPadding(0, statusBarHeight, 0, 0);


         spinner = findViewById(R.id.spinnerCategory);
        getCategory();
//        String[] categories = {"Ngôn tình", "Kinh dị", "Trinh thám", "Hài hước"};
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                R.layout.spinner_item_category,
//                categories
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        rcvBookDetail = findViewById(R.id.rcv_book_detail);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 cột
        rcvBookDetail.setLayoutManager(layoutManager);

    }
    private void getCategory() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        apiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getDataList();

                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            DetailActivity.this,
                            R.layout.spinner_item_category,
                            categoryNames // Use the list of names, not Category objects.
                    );

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            getListBookByCategory(selectedItem);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });


                }
            }

                    @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.e("API Category", "Lỗi khi gọi API category: " + t.getMessage());
            }
        });

    }
    private void getListBookByCategory(String category) {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        apiCaller.getBookByCategory(category).enqueue(new Callback<ReponderModel<Book>>() {
            @Override
            public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getDataList();

                    BookByCategoryAdapter = new BookByCategoryAdapter(DetailActivity.this, books);
                    rcvBookDetail.setAdapter(BookByCategoryAdapter);

                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                Log.e("API Category", "Lỗi khi gọi API category: " + t.getMessage());
            }
        });

    }


            public void SetColorBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_background_primary));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.color_background_primary));
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