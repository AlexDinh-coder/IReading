package com.example.iread;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.OpenBook.BookDetailAdapter;

public class DetailActivity extends AppCompatActivity {
    View contentContainer;
    private RecyclerView rcvBookDetail;
    private BookDetailAdapter bookDetailAdapter;
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


        Spinner spinner = findViewById(R.id.spinnerCategory);
        String[] categories = {"Ngôn tình", "Kinh dị", "Trinh thám", "Hài hước"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_category,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        rcvBookDetail = findViewById(R.id.rcv_book_detail);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 cột
        rcvBookDetail.setLayoutManager(layoutManager);
        rcvBookDetail.setAdapter(bookDetailAdapter);
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