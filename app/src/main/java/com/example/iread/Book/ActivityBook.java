package com.example.iread.Book;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import com.example.iread.Model.DataBook;
import com.example.iread.Model.DataPageInBook;
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

    private Integer viewId = 0;

    private String username, userId;

    private boolean clickView = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getString("userId", "");
        clickView = getIntent().getBooleanExtra("isView", false);

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

        sendViewStatus(currentChapter, 1,viewId); // Đánh dấu chương đã đóng
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

        List<DataPageInBook> pageDataList = new ArrayList<>();
        for (int i = 0; i <chapterList.size(); i++) {
            BookChapter chapter = chapterList.get(i);
            String htmlContent = chapter.getContent();

            List<DataBook> dataBookList = new ArrayList<>();

            // Tách thẻ <img> và <p> ra khỏi content
            String[] segments = htmlContent.split("(?=<img)|(?=<p)|(?<=</p>)");
            for (String segment : segments) {
                segment = segment.trim();
                if (segment.contains("<img")) {
                    String imgUrl = extractImgSrc(segment);
                    Log.d("BOOK_CONTENT", "Extracted IMG URL: " + imgUrl);
                    if(imgUrl != null && !imgUrl.isEmpty()) {
                        DataBook imgItem = new DataBook(imgUrl, false);
                        imgItem.setType(false);
                        dataBookList.add(imgItem);
                    }
                } else {
                    //Xu li noi dung
                    String textContent = Html.fromHtml(segment, Html.FROM_HTML_MODE_LEGACY).toString().trim();
                    Log.d("BOOK_CONTENT", "Extracted TEXT: " + textContent);
                    if (!textContent.isEmpty()) {
                        DataBook textItem = new DataBook(textContent, true);
                        textItem.setType(true); // is text
                        dataBookList.add(textItem);
                    }
                }
            }
            DataPageInBook dataPageInBook = new DataPageInBook(i, dataBookList,chapterList.get(i));
            pageDataList.add(dataPageInBook);
        }

        pageAdapter = new PageAdapter(pageDataList, this,username);
        viewPagerBook = findViewById(R.id.viewPagerBook);
        viewPagerBook.setAdapter(pageAdapter);
        viewPagerBook.setCurrentItem(selectedIndex);
    }
    private String extractImgSrc(String segment) {
        int srcIndex = segment.indexOf("src=");
        if (srcIndex == -1) return null;
        int start = segment.indexOf("\"", srcIndex) + 1;
        int end = segment.indexOf("\"", start);

        if (start > 0 && end > start) {
            return segment.substring(start,end);
        }
        return null;
    }

    // Cấu hình ViewPager để gửi view khi chuyển chương
    private void setupViewPager() {
        viewPagerBook.setPageTransformer(new DepthPageTransformer());

        viewPagerBook.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BookChapter selectedChapter = chapterList.get(position);
                sendViewStatus(selectedChapter,0,viewId);

            }
        });
    }

    // Gửi trạng thái đọc chương (mở hoặc đóng)
    private void sendViewStatus(BookChapter chapter, int status, int id) {
        if (chapter == null) return;

        int bookId = chapter.getBookId();

        BookViewModel model = new BookViewModel();
        model.setId(id);
        model.setBookId(bookId);
        model.setChapterId(chapter.getId());
        model.setBookTypeStatus(0);
        model.setCreateBy(username);
        model.setStatus(status);
        model.setUserId(userId);

        apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<Integer>>() {
            @Override
            public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                     viewId = response.body().getData();

                }
                Log.d("BookTracking", "Đã gửi trạng thái " + status + " cho chương " + chapter.getChaperId());
            }

            @Override
            public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                Log.e("BookTracking", "Lỗi gửi trạng thái: " + t.getMessage());
            }
        });
    }

    // Thiết lập nút quay lại
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            int currentPosition = viewPagerBook.getCurrentItem();
            BookChapter currentChapter = chapterList.get(currentPosition);
            sendViewStatus(currentChapter, 1,viewId); // Đánh dấu chương đã đóng
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
                }, this, chapterList, viewId
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
