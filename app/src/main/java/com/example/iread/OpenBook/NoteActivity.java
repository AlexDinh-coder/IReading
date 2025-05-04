package com.example.iread.OpenBook;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.iread.Book.NoteFragment;
import com.example.iread.Model.BookChapter;
import com.example.iread.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    TabLayout tabLayout;
    int bookid;

    private ImageView btnBack;

    private List<BookChapter> chapterList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        bookid = getIntent().getIntExtra("bookId",0);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        makeStatusBarTransparent();
        applyTopPadding();
        showTabLayout();
    }
    private void showTabLayout() {

        Bundle bundle = new Bundle();
        bundle.putSerializable("chapterList", new ArrayList<>(chapterList));
        bundle.putInt("bookId", bookid);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment);

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