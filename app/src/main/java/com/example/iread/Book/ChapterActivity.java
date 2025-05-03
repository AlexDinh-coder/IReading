package com.example.iread.Book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.iread.Model.BookChapter;
import com.example.iread.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {
    TabLayout tabLayout;
     int bookid;

    private List<BookChapter> chapterList = new ArrayList<>();

    Intent intent = getIntent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chapter);
        bookid = getIntent().getIntExtra("bookId",0);

        makeStatusBarTransparent();
        applyTopPadding();
        showTabLayout();




//        // Gán dữ liệu vào RecyclerView
//        RecyclerView recyclerView = findViewById(R.id.rcv_chapter_in_read_book);
//        ChapterAdapter adapter = new ChapterAdapter(this, chapterList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    private void showTabLayout() {
        String[] tabTitles = {"Mục lục", "Dấu trang", "Ghi chú", "Bình luận"};
        tabLayout = findViewById(R.id.tabLayout);
        chapterList = (List<BookChapter>) getIntent().getSerializableExtra("chapterList");
        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        replaceFragment(new ChapterListFragment());
                        break;
                    case 1:
//                        replaceFragment(new BookmarkFragment());
                        break;
                    case 2:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("chapterList", new ArrayList<>(chapterList));
                        bundle.putInt("bookId",bookid);

                        NoteFragment fragment = new NoteFragment();
                        fragment.setArguments(bundle);
                        replaceFragment(fragment);
                        break;
                    case 3:
//                        replaceFragment(new CommentFragment());
                        break;
                }
                Toast.makeText(ChapterActivity.this, "Đang chọn: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_menuchapter, fragment)
                .commit();
    }
    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }
    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.read_book_frame);

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