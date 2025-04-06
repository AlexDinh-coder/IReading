package com.example.iread.Book;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iread.Model.BookChapter;
import com.example.iread.OpenBook.ChapterAdapter;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ActivityBook extends AppCompatActivity {
    private IAppApiCaller IAppApiCaller;
    private ViewPager2 viewPagerBook;
    private PageAdapter pageAdapter;
    private List<BookChapter> chapterList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        makeStatusBarTransparent(); // làm trong suốt status bar
        applyTopPadding();          // tránh đè nội dung lên status bar nếu cần


        // Lấy danh sách chương và vị trí chương được chọn
        chapterList = (List<BookChapter>) getIntent().getSerializableExtra("chapterList");
        int selectedIndex = getIntent().getIntExtra("selectedIndex", 0);
        pageAdapter = new PageAdapter(chapterList);
        viewPagerBook = findViewById(R.id.viewPagerBook);
        viewPagerBook.setAdapter(pageAdapter);
        viewPagerBook.setCurrentItem(selectedIndex); // Hiển thị chương đã click

        // Hiển thị menu chương
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showChapterMenu());

        // Hiển thị nội dung chương
        String content = getIntent().getStringExtra("chapterContent");
        BookChapter chapter = new BookChapter();
        chapter.setContent(content);
        chapterList.add(chapter);
        // Adapter và gán dữ liệu

        //Back lại
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });


        viewPagerBook.setPageTransformer(new DepthPageTransformer());



    }

    private void showChapterMenu() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_chapters, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Truyền listener vào
        ChapterAdapter menuAdapter = new ChapterAdapter(
                (int position) -> {
                    viewPagerBook.setCurrentItem(position);
                    dialog.dismiss(); // Ẩn menu sau khi chọn
                },
                this,
                chapterList
        );

        recyclerView.setAdapter(menuAdapter);
        dialog.setContentView(view);
        dialog.show();


    }


    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }


    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.box_open_book_activity);

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
