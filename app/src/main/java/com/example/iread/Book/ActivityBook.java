package com.example.iread.Book;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.OpenBook.ChapterAdapter;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityBook extends AppCompatActivity {
    IAppApiCaller IAppApiCaller;
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

        IAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);

        // Lấy danh sách chương và vị trí chương được chọn
        chapterList = (List<BookChapter>) getIntent().getSerializableExtra("chapterList");
        int selectedIndex = getIntent().getIntExtra("selectedIndex", 0);
        pageAdapter = new PageAdapter(chapterList);
        viewPagerBook = findViewById(R.id.viewPagerBook);
        viewPagerBook.setAdapter(pageAdapter);
        viewPagerBook.setCurrentItem(selectedIndex); // Hiển thị chương đã click

        Set<Integer> sentChapters = new HashSet<>();

        viewPagerBook.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BookChapter selectedChapter = chapterList.get(position);
                int chapterId = selectedChapter.getChapterId();

                if (!sentChapters.contains(chapterId)) {
                    sendViewStatus(selectedChapter, 0);  // chỉ gửi open nếu chưa từng gửi
                    sentChapters.add(chapterId);
                }
            }
        });


        // Hiển thị menu chương
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showChapterMenu());

        // Hiển thị nội dung chương
        String content = getIntent().getStringExtra("chapterContent");
        BookChapter chapter = new BookChapter();
        chapter.setContent(content);
        //chapterList.add(chapter);
        // Adapter và gán dữ liệu

        //Back lại
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        viewPagerBook.setPageTransformer(new DepthPageTransformer());

    }


    @Override
    public void onBackPressed() {
        int currentPosition = viewPagerBook.getCurrentItem();
        BookChapter currentChaper = chapterList.get(currentPosition);

       // sendCloseView(currentChaper);
        sendViewStatus(currentChaper, 1);
        // Xử lý logic trước khi quay lại
        super.onBackPressed(); // Quay về trang trước
    }

    private void sendViewStatus(BookChapter currentChapter, int status) {
        if (currentChapter == null) {
            return;
        }

        // Validate bookId
        String bookIdStr = currentChapter.getBookId();
        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdStr);
        } catch (NumberFormatException e) {
            return;
        }

        // Tạo model
        BookViewModel model = new BookViewModel();
        model.setBookId(bookId);
        model.setChapterId(String.valueOf(currentChapter.getChapterId()));
        model.setBookTypeStatus(0); // 0: đọc
        model.setStatus(status);    // 0: mở, 1: đóng
        model.setUserId(currentChapter.getUserId());


        // Gửi API
        if (IAppApiCaller == null) {
            IAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        }

        IAppApiCaller.createBookView(model).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                Log.d("BookTracking", "Đã gửi trạng thái " + status + " cho chương " + currentChapter.getChapterId());
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Log.e("BookTracking", "Lỗi gửi trạng thái: " + t.getMessage());
            }
        });
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
