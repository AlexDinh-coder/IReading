package com.example.iread.MenuBarInHome;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.DetailActivity;
import com.example.iread.Model.Category;
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


public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CateAdapter itemAdapter;
    private ImageView imgSearch;
    private IAppApiCaller iAppApiCaller;

    ImageView btnBack;

    private List<Category> categoryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        makeStatusBarTransparent();
        applyTopPadding();

        imgSearch = findViewById(R.id.imgSearchInBar);
        imgSearch.setOnClickListener(view -> {
            Intent intent = new Intent(this , SearchActivity.class);
            startActivity(intent);
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->finish());
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        // ============================= Cate ==========
        recyclerView = findViewById(R.id.rcvCategoryInBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        int spaceInPx = (int) (getResources().getDisplayMetrics().density * 12); // ~12dp
        recyclerView.addItemDecoration(new VerticalSpacingItemDecoration(spaceInPx));
        // ============================================
        RecyclerView recyclerGenres;
        recyclerGenres = findViewById(R.id.recyclerGenres);
        recyclerGenres.setLayoutManager(new LinearLayoutManager(this));

        getListCategory();


    }

    private void getListCategory() {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getDataList();

                    // Truyền vào adapter
                    itemAdapter = new CateAdapter(categories, category -> {
                        // Xử lý khi click vào category
                        Intent intent = new Intent(CategoryActivity.this, DetailActivity.class);
                        intent.putExtra("selectedCategory", category.getName());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(itemAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.e("API", "Lỗi lấy danh sách category: " + t.getMessage());
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