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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityBook extends AppCompatActivity {

    private IAppApiCaller apiCaller;
    private ViewPager2 viewPagerBook;
    private PageAdapter pageAdapter;
    private List<BookChapter> chapterList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        setupUI();
        setupApiCaller();
        loadChapterData();
        setupViewPager();
        setupBackButton();
        setupMenuButton();
    }

    @Override
    public void onBackPressed() {
        int currentPosition = viewPagerBook.getCurrentItem();
        BookChapter currentChapter = chapterList.get(currentPosition);
        sendViewStatus(currentChapter, 1); // Đánh dấu chương đã đóng
        super.onBackPressed();
    }

    // Khởi tạo API Caller
    private void setupApiCaller() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
    }

    // Thiết lập các thành phần UI
    private void setupUI() {
        makeStatusBarTransparent();
        applyTopPadding();
    }

    // Lấy dữ liệu chương từ intent và gán vào ViewPager
    private void loadChapterData() {
        chapterList = (List<BookChapter>) getIntent().getSerializableExtra("chapterList");
        int selectedIndex = getIntent().getIntExtra("selectedIndex", 0);

        pageAdapter = new PageAdapter(chapterList);
        viewPagerBook = findViewById(R.id.viewPagerBook);
        viewPagerBook.setAdapter(pageAdapter);
        viewPagerBook.setCurrentItem(selectedIndex);
    }

    // Cấu hình ViewPager để gửi view khi chuyển chương
    private void setupViewPager() {
        viewPagerBook.setPageTransformer(new DepthPageTransformer());

        viewPagerBook.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BookChapter selectedChapter = chapterList.get(position);
                sendViewStatus(selectedChapter, 0);
            }
        });
    }

    // Gửi trạng thái đọc chương (mở hoặc đóng)
    private void sendViewStatus(BookChapter chapter, int status) {
        if (chapter == null || chapter.getBookId() == null || chapter.getBookId().trim().isEmpty()) return;

        int bookId;
        try {
            bookId = Integer.parseInt(chapter.getBookId());
        } catch (NumberFormatException e) {
            Log.e("BookTracking", "bookId không hợp lệ: " + chapter.getBookId());
            return;
        }

        BookViewModel model = new BookViewModel();
        model.setBookId(bookId);
        model.setChapterId(String.valueOf(chapter.getChapterId()));
        model.setBookTypeStatus(0);
        model.setStatus(status);
        model.setUserId(chapter.getUserId());

        apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                Log.d("BookTracking", "Đã gửi trạng thái " + status + " cho chương " + chapter.getChapterId());
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Log.e("BookTracking", "Lỗi gửi trạng thái: " + t.getMessage());
            }
        });
    }

    // Thiết lập nút quay lại
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    // Thiết lập nút menu chương
    private void setupMenuButton() {
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showChapterMenu());
    }

    // Hiển thị danh sách chương dưới dạng BottomSheet
    private void showChapterMenu() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_chapters, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChapterAdapter menuAdapter = new ChapterAdapter(
                position -> {
                    viewPagerBook.setCurrentItem(position);
                    dialog.dismiss();
                }, this, chapterList
        );

        recyclerView.setAdapter(menuAdapter);
        dialog.setContentView(view);
        dialog.show();
    }

    // Làm trong suốt thanh trạng thái
    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    // Đẩy phần nội dung xuống dưới status bar
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

    private boolean hasReadChapter(int chapterId) {
        return getSharedPreferences("read_chapters", MODE_PRIVATE)
                .getBoolean("chapter_" + chapterId, false);
    }

    private void markChapterAsRead(int chapterId) {
        getSharedPreferences("read_chapters", MODE_PRIVATE)
                .edit()
                .putBoolean("chapter_" + chapterId, true)
                .apply();
    }
}
