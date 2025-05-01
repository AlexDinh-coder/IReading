package com.example.iread.Book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.Model.DataBook;
import com.example.iread.Model.DataPageInBook;
import com.example.iread.OpenBook.ChapterAdapter;
import com.example.iread.OpenBook.ChapterDataHolder;
import com.example.iread.OpenBook.OpenBookActivity;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> unlockedChapterIds = new ArrayList<>();

    private int userCoin;

    private int bookPrice;

    private String currentChapterId = null;


    private Map<String, Integer> viewIdMap = new HashMap<>();




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

        setupBackButton();
        setupMenuButton();
    }


    @Override
    public void onBackPressed() {
        if (currentChapterId != null) {
            BookChapter current = findChapterById(currentChapterId);
            if (current != null) {
                Integer savedViewId = viewIdMap.getOrDefault(current.getId(), 0);
                Log.d("BookTracking", "📤 Back: Đóng chương [" + current.getChapterName() + "] với viewId = " + savedViewId);

                BookViewModel model = new BookViewModel();
                model.setId(savedViewId);
                model.setBookId(current.getBookId());
                model.setChapterId(current.getId());
                model.setBookTypeStatus(0); // đọc
                model.setCreateBy(username);
                model.setStatus(1); // đóng
                model.setUserId(userId);

                apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<Integer>>() {
                    @Override
                    public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                        Log.d("BookTracking", "Đã gửi đóng chương khi back, viewId = " + model.getId());
                        backToOpenBookActivity();
                    }

                    @Override
                    public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                        Log.e("BookTracking", "Lỗi gửi đóng chương khi back: " + t.getMessage());
                        backToOpenBookActivity();
                    }
                });
                return; // tránh gọi `backToOpenBookActivity()` hai lần
            }
        }
        // fallback nếu không có chương hiện tại
        backToOpenBookActivity();
    }


    private void backToOpenBookActivity() {
        Intent intent = new Intent(ActivityBook.this, OpenBookActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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
        chapterList = ChapterDataHolder.getInstance().getChapterList();
        int selectedIndex = getIntent().getIntExtra("selectedIndex", 0);

        if (chapterList == null || chapterList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu chương sách để hiển thị!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<DataPageInBook> pageDataList = new ArrayList<>();
        if (pageAdapter != null) {
            pageAdapter.clearPages(); // clear nếu đã có dữ liệu adapter
        }
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

        setupViewPager();
        BookChapter initialChapter = chapterList.get(selectedIndex);
        currentChapterId = initialChapter.getId();
        if (!viewIdMap.containsKey(initialChapter.getId())) {
            sendViewStatus(initialChapter, 0);
        } else {
            Log.d("BookTracking", "Không mở lại chương đầu tiên vì đã có viewId");
        }
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

            private int pendingPosition = -1;

            @Override
            public void onPageSelected(int position) {
                pendingPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && pendingPosition != -1) {
                    BookChapter selectedChapter = chapterList.get(pendingPosition);

                    // Nếu khác chương hiện tại thì xử lý
                    if (currentChapterId == null || !currentChapterId.equals(selectedChapter.getId())) {
                        // Gửi trạng thái đóng chương cũ
                        if (currentChapterId != null) {
                            BookChapter previousChapter = findChapterById(currentChapterId);
                            if (previousChapter != null) {
                                sendViewStatus(previousChapter, 1);
                            }
                        }

                        // Gửi trạng thái mở chương mới
                        currentChapterId = selectedChapter.getId();
                        sendViewStatus(selectedChapter, 0);
                    }

                    markChapterAsRead(selectedChapter.getChapterNumber());
                }
            }
        });
    }


    private BookChapter findChapterById(String currentChapterId) {
        for (BookChapter c : chapterList) {
            if (c.getId().equals(currentChapterId)) return c;
        }
        return null;
    }

    // Gửi trạng thái đọc chương (mở hoặc đóng)
    private void sendViewStatus(BookChapter chapter, int status) {
        if (chapter == null) return;

        int bookId = chapter.getBookId();

        if (status == 0 && viewIdMap.containsKey(chapter.getId())) {
            Log.d("BookTracking", "Đã từng mở chương [" + chapter.getChapterName() + "] → không mở lại");
            return;
        }

        BookViewModel model = new BookViewModel();

        // Nếu status là 1 (close), gán id đã lưu
        if (status == 1) {
            int existingId = viewIdMap.getOrDefault(chapter.getId(), 0);
            if (existingId == 0) {
                Log.w("BookTracking", "Không gửi đóng chương vì chưa có viewId!");
                return; // bỏ qua nếu chưa có viewId
            }
            model.setId(existingId);
        }


        model.setBookId(bookId);
        model.setChapterId(chapter.getId());
        model.setBookTypeStatus(0); // 0: đọc
        model.setCreateBy(username);
        model.setStatus(status);
        model.setUserId(userId);

        if (status == 1) {
            int existingId = viewIdMap.getOrDefault(chapter.getId(), 0);
            model.setId(existingId);
            Log.d("BookTracking", "Đóng chương [" + chapter.getChapterName() + "] -> ID = " + existingId);
        } else {
            model.setId(0); // tạo mới
            Log.d("BookTracking", "Mở chương [" + chapter.getChapterName() + "]");
        }

        apiCaller.createBookView(model).enqueue(new Callback<ReponderModel<Integer>>() {
            @Override
            public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (status == 0) {
                        int returnedId = response.body().getData();
                        viewIdMap.put(chapter.getId(), returnedId); // lưu lại để dùng khi đóng chương
                        Log.d("BookTracking", "✔ Mở chương - viewId được lưu: " + returnedId);
                    } else {
                        Log.d("BookTracking", "✔ Đóng chương đã gửi với viewId: " + model.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                Log.e("BookTracking", " Lỗi gọi API CreateViewBook: " + t.getMessage());
            }
        });
    }


    // Thiết lập nút quay lại
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            int currentPosition = viewPagerBook.getCurrentItem();
            BookChapter currentChapter = chapterList.get(currentPosition);
            sendViewStatus(currentChapter, 1); // Đánh dấu chương đã đóng
            backToOpenBookActivity();

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
        int bookId = chapterList.isEmpty() ? -1 : chapterList.get(0).getBookId();
        ChapterAdapter menuAdapter = new ChapterAdapter(
                position -> {
                    viewPagerBook.setCurrentItem(position);
                    dialog.dismiss();
                }, this, chapterList, viewId,bookId,0, unlockedChapterIds, userCoin, true, bookPrice
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
