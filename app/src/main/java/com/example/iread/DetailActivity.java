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
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private View contentContainer;
    private RecyclerView rcvBookDetail;
    private Spinner spinner;
    private ImageView btnBack;

    private IAppApiCaller apiCaller;
    private BookByCategoryAdapter BookByCategoryAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Thiết lập màu thanh status/navigation bar
        SetColorBar();

        // Toàn màn hình + trong suốt status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        // Gán layout container để tính padding cho status bar
        contentContainer = findViewById(R.id.content_detail_container);
        int statusBarHeight = getStatusBarHeight();
        contentContainer.setPadding(0, statusBarHeight, 0, 0);

        // Xử lý nút quay lại
        btnBack = findViewById(R.id.imageView2);
        btnBack.setOnClickListener(v -> finish());
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        // Ánh xạ Spinner và RecyclerView
        spinner = findViewById(R.id.spinnerCategory);
        rcvBookDetail = findViewById(R.id.rcv_book_detail);
        rcvBookDetail.setLayoutManager(new GridLayoutManager(this, 2)); // Hiển thị 2 cột sách

        // Xử lý logic khi có thể loại được truyền từ màn trước
        String selectedCategory = getIntent().getStringExtra("selectedCategory");
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            getListBookByCategory(selectedCategory);
        }

        // Load toàn bộ thể loại vào Spinner
        getCategory();
    }


     // Hàm gọi API để lấy danh sách thể loại, sau đó setup spinner và sự kiện chọn
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

                    // Adapter gán vào Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            DetailActivity.this,
                            R.layout.spinner_item_category,
                            categoryNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // Nếu có category được truyền từ màn trước thì chọn sẵn
                    String selectedCategory = getIntent().getStringExtra("selectedCategory");
                    if (selectedCategory != null && !selectedCategory.isEmpty()) {
                        for (int i = 0; i < categoryNames.size(); i++) {
                            if (categoryNames.get(i).equalsIgnoreCase(selectedCategory)) {
                                spinner.setSelection(i);
                                break;
                            }
                        }

                    } else if (!categoryNames.isEmpty()) {
                        spinner.setSelection(0); // chọn thể loại đầu tiên nếu không có selectedCategory
                    }

                    // Bắt sự kiện khi người dùng chọn lại category khác từ Spinner
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


     //Gọi API để lấy danh sách sách theo thể loại và hiển thị lên RecyclerView
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


     //Thiết lập màu thanh trạng thái và thanh điều hướng
    public void SetColorBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_background_primary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_background_primary));
        }
    }


     //Trả về chiều cao của thanh trạng thái hệ thống
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
