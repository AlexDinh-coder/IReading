package com.example.iread.OpenBook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.Book.ActivityBook;
import com.example.iread.Comment.ReviewActivity;
import com.example.iread.CommentActivity;
import com.example.iread.DetailActivity;
import com.example.iread.Interface.ParameterInterface;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.Category;
import com.example.iread.Model.CommentModel;
import com.example.iread.R;
import com.example.iread.SubscriptionActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenBookActivity extends AppCompatActivity implements ParameterInterface<Integer> {

    private RecyclerView rcv;
    private IAppApiCaller apiCaller;
    private ReviewAdapter reviewAdapter;
    private TabLayout tabLayout;
    private TextView totalRating, btnTotalReview, btnTextReview, totalReadView,totalListenView;

    private TextView ratingTextStarTop, ratingTextStarBottom;

    MaterialButton btnBookRead, btnBookListen;

    ImageView iconShow, iconLove;

    AppCompatButton btnActionBook, btnUpgrade;

    ImageView[] ratingStarBottom = new ImageView[5];
    private ActivityResultLauncher<Intent> commentLauncher;
    private ImageView btnBack;
    private FrameLayout contentFrame;

    private boolean isFavorite = false;
    private int bookId;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);
        // Khởi tạo dữ liệu
        initViews();
        setupApiCaller();
        //Cấu hình giao diện
        setupUI();
        setupRecyclerView();
        setupTabs();
        setupCommentLauncher();
        //Load lại chi tiết sách và dữ liệu
        loadBookDetails();
        applyBookReadMode();
    }

    //Hàm ánh xạ tới các view từ layout xml
    private void initViews() {
        btnTotalReview = findViewById(R.id.btn_see_all_reviews);
        totalRating = findViewById(R.id.totalRating);
        btnTextReview = findViewById(R.id.btnReview);
        totalReadView = findViewById(R.id.totalView);
        btnBack = findViewById(R.id.imageView2);
        tabLayout = findViewById(R.id.tabLayout);
        contentFrame = findViewById(R.id.contentFrame);
        rcv = findViewById(R.id.rcv_review_in_open_book);
        bookId = getIntent().getIntExtra("bookId", -1);
        ratingTextStarTop = findViewById(R.id.textViewTop);
        ratingTextStarBottom =findViewById(R.id.textViewBottom);
        btnBookRead = findViewById(R.id.btnBookRead);
        btnBookListen = findViewById(R.id.btnBookListen);
        iconShow = findViewById(R.id.iconShow);
        btnActionBook = findViewById(R.id.btnActionBook);
        btnUpgrade = findViewById(R.id.btnUpgrade);
        iconLove = findViewById(R.id.iconLove);


        //Load dữ liệu user info
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        //Load favorite book
        String key = username + "_book_" + bookId;
        SharedPreferences prefs = getSharedPreferences("FavoritePrefs", MODE_PRIVATE);
        isFavorite = prefs.getBoolean(key, false); // Load trạng thái yêu thích

        iconLove.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_love);
        iconLove.setOnClickListener(v -> {
            if (!isFavorite) {
                addBookToFavorite(bookId, username);
            } else {
                removeFromFavorite(bookId, username); // chỉ xử lý local, không gọi API
            }
        });


        ratingStarBottom[0] = findViewById(R.id.star1);
        ratingStarBottom[1] = findViewById(R.id.star2);
        ratingStarBottom[2] = findViewById(R.id.star3);
        ratingStarBottom[3] = findViewById(R.id.star4);
        ratingStarBottom[4] = findViewById(R.id.star5);

        btnActionBook.setOnClickListener(v -> {
            if (btnActionBook.getText().toString().equals("ĐỌC SÁCH")){
                openFirstChapter(); // gọi API lấy chương đầu tiên
            }
        });
        btnBookRead.setOnClickListener(v -> applyBookReadMode());
        btnBookListen.setOnClickListener(v -> applyBookListenMode());
    }

    //Khởi tạo retrofit API thực hiện các request tới server
    private void setupApiCaller() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
    }
    //Hàm để xử lí load các thông tin sách
    private void loadBookDetails() {
        if (bookId == -1) return;

        apiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
            @Override
            public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body().getData();
                    if (book != null) {
                        showBookDetailUI(book);
                        btnTextReview.setOnClickListener(v -> {
                            Intent intent = new Intent(OpenBookActivity.this, CommentActivity.class);
                            intent.putExtra("bookId", book.getId());
                            intent.putExtra("bookTitle", book.getName());
                            intent.putExtra("bookAuthor", book.getCreateBy());
                            intent.putExtra("bookImage", book.getPoster());
                            commentLauncher.launch(intent);
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                Log.e("BookAPI", "Lỗi gọi API GetBookById: " + t.getMessage());
            }
        });

        getBookReview(bookId);
        getBookTotalReview(bookId);
        //getBookListenView(bookId);
    }

    private void removeFromFavorite(int bookId, String username) {
        isFavorite = false;
        iconLove.setImageResource(R.drawable.ic_love);
        saveFavoriteStatus(false);
        removeBookFromPref(bookId);
        Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
    }

    private void removeBookFromPref(int bookId) {
        SharedPreferences prefs = getSharedPreferences("FavoriteBooks", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(String.valueOf(bookId));
        editor.apply();
    }

    private void addBookToFavorite(int bookId, String username) {
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }

        apiCaller.addFavoriteBook(bookId, username).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                        isFavorite = true;
                        iconLove.setImageResource(R.drawable.ic_heart_filled);
                        saveFavoriteStatus(true);
                        Toast.makeText(OpenBookActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        // Lưu sách yêu thích vào SharedPreferences
                        saveBookInfo(bookId);
                } else {
                    Toast.makeText(OpenBookActivity.this, "Thêm vào yêu thích thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(OpenBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBookInfo(int bookId) {
        apiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
            @Override
            public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body().getData();
                    if (book != null) {
                        saveFavoriteBookToPrefs(book);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                Log.e("API", "Lỗi lấy thông tin sách để lưu local: " + t.getMessage());
            }
        });
    }

    private void saveFavoriteBookToPrefs(Book book) {
        SharedPreferences prefs = getSharedPreferences("FavoriteBooks", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String bookData = book.getName() + "|" + book.getCreateBy() + "|" + book.getPoster();
        editor.putString(String.valueOf(book.getId()), bookData);
        editor.apply();
    }

    private void saveFavoriteStatus(boolean status) {
            SharedPreferences.Editor editor = getSharedPreferences("FavoritePrefs", MODE_PRIVATE).edit();
            String key = username + "_book_" + bookId;
            editor.putBoolean(key, status);
            editor.apply();
    }


    //Xử lí phần khi click đọs sach nhảy sang nội dung của chương
    private void openFirstChapter() {
        apiCaller.getListByBookId(bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();
                    if (chapters != null && !chapters.isEmpty()) {
                        // Lấy nội dung của chương sách đầu tiên
                        Collections.sort(chapters, Comparator.comparing(BookChapter::getChaperId));

                        Intent intent = new Intent(OpenBookActivity.this, ActivityBook.class);
                        intent.putExtra("selectedIndex", 0);
                        intent.putExtra("chapterList", new ArrayList<>(chapters));
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                Log.e("API", "Lỗi lấy chương đầu: " + t.getMessage());
            }
        });
    }

    //Xử lí phần sách nghe
    private void applyBookListenMode() {
        iconShow.setImageResource(R.drawable.ic_headphone);
        btnBookListen.setTextColor(Color.WHITE);
        btnBookRead.setTextColor(Color.GRAY);
        btnBookListen.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnBookRead.setBackgroundTintList(getColorStateList(R.color.dark_gray));
        btnActionBook.setText("NGHE THỬ");
        btnUpgrade.setVisibility(View.VISIBLE);
        btnUpgrade.setOnClickListener(v -> {
            Intent intent = new Intent(OpenBookActivity.this, SubscriptionActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });
    }

    ////Xử lí phần sách nghe
    private void applyBookReadMode() {
        iconShow.setImageResource(R.drawable.ic_show);
        btnBookRead.setTextColor(Color.WHITE);
        btnBookListen.setTextColor(Color.GRAY);
        btnBookRead.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnBookListen.setBackgroundTintList(getColorStateList(R.color.dark_gray));
        btnActionBook.setText("ĐỌC SÁCH");
        btnUpgrade.setVisibility(View.GONE);
    }


    //
    private void setupUI() {
        makeStatusBarTransparent();
        applyTopPadding();
        //Xử lí nút back
        btnBack.setOnClickListener(v -> finish());
        //Xem tổng đánh giá
        if (bookId != -1) {
            btnTotalReview.setOnClickListener(v -> {
                Intent intent = new Intent(OpenBookActivity.this, ReviewActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            });
        }
        btnBookListen.setOnClickListener(v -> {
            applyBookListenMode();
            loadFragmentWithBookId(new ChapterFragment(), 1);
        });
        btnBookRead.setOnClickListener(v -> {
            applyBookReadMode();
            loadFragmentWithBookId(new ChapterFragment(), 0);
        });

    }

    private void setupRecyclerView() {
        rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rcv.setAdapter(reviewAdapter);
    }
    private void loadFragmentWithBookId(Fragment fragment, int bookTypeStatus) {
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", bookId);
        bundle.putInt("bookTypeStatus", bookTypeStatus); // 0: đọc, 1: nghe
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
    }

    //Hàm để khởi tạo các tab ấn click vô mục chương, có thể bạn thích
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Chương"));
        tabLayout.addTab(tabLayout.newTab().setText("Có thể bạn thích"));

        loadFragmentWithBookId(new ChapterFragment(), 0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    loadFragmentWithBookId(new ChapterFragment());
                } else if (tab.getPosition() == 1) {
                    loadFragmentWithBookId(new MinghtLikeFragment());
                }
//                Fragment fragment = tab.getPosition() == 0 ? new ChapterFragment() : new MinghtLikeFragment();
//                loadFragmentWithBookId(fragment);TabLayout
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    private void setupCommentLauncher() {
        commentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && bookId != -1) {
                        getBookReview(bookId);
                    }
                });
    }

    //Hàm để lấy list review của users
    private void getBookReview(int bookId) {
        apiCaller.listCommentBook(bookId).enqueue(new Callback<ReponderModel<CommentModel>>() {
            @Override
            public void onResponse(Call<ReponderModel<CommentModel>> call, Response<ReponderModel<CommentModel>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDataList() != null) {
                    List<CommentModel> comments = response.body().getDataList();
                    totalRating.setText(comments.size() + " đánh giá");
                    reviewAdapter.updateData(comments);

                    float totalStar = 0f;
                    for (CommentModel comment : comments) {
                        totalStar += comment.getRating();
                    }
                    float averageRating = comments.size() > 0 ? totalStar / comments.size() : 0f;
                    String ratingStar = String.format("%.1f", averageRating);
                    ratingTextStarTop.setText(ratingStar + "/5");
                    ratingTextStarBottom.setText(ratingStar);

                    updateStarIcon(averageRating);
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<CommentModel>> call, Throwable t) {
                Log.e("ReviewAPI", "Lỗi khi lấy đánh giá: " + t.getMessage(), t);
            }
        });
    }

    private void updateStarIcon(float averageRating) {
        int fullStar = (int) averageRating;
        boolean halfStar = (averageRating - fullStar) >= 0.5;

        for (int i = 0; i < ratingStarBottom.length; i++) {
            if (i < fullStar) {
                ratingStarBottom[i].setImageResource(R.drawable.ic_star);
            } else if (i == fullStar && halfStar) {
                ratingStarBottom[i].setImageResource(R.drawable.ic_star_half);
            } else {
                ratingStarBottom[i].setImageResource(R.drawable.ic_star_border);
            }
        }
    }

    //Hàm để hiện thị tổng số lượt người đọc
    private void getBookTotalReview(int bookId) {
        apiCaller.totalViewBook(bookId, 0).enqueue(new Callback<ReponderModel<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<ReponderModel<Integer>> call, @NonNull Response<ReponderModel<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int totalView = response.body().getData();
                    Log.d("API_VIEW", "Tổng lượt xem: " + totalView);
                    totalReadView.setText(String.valueOf(totalView));
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
                Log.e("APIView", "Lỗi API GetViewNo: " + t.getMessage());
            }
        });
    }
    private void showBookDetailUI(Book book) {
        ImageView imgBook = findViewById(R.id.image_characters_in_detail);
        Glide.with(this).load(book.getPoster())
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_image).into(imgBook);

        TextView tvName = findViewById(R.id.book_title_in_detail);
        if (tvName != null) {
            tvName.setText(book.getName());
            tvName.setTextSize(23);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        TextView authorName = findViewById(R.id.author_name);
        if (authorName != null) {
            authorName.setText(book.getCreateBy());
        }

        setupBookSummary(book);
        setupBookCategories(book);
    }
    //Hàm này hiện thị phần xem thêm hoặc thu gọn ở phần giới thiệu sách
    private void setupBookSummary(Book book) {
        TextView tvSummary = findViewById(R.id.descriptionTextView);
        TextView tvToggle = findViewById(R.id.tvToggleSummary);

        if (tvSummary == null || tvToggle == null) return;

        tvSummary.setText(book.getSummary());
        tvSummary.setMaxLines(2);
        tvSummary.setEllipsize(TextUtils.TruncateAt.END);

        tvToggle.setText("Xem thêm");

        tvToggle.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    tvSummary.setMaxLines(2);
                    tvSummary.setEllipsize(TextUtils.TruncateAt.END);
                    tvToggle.setText("Xem thêm");
                } else {
                    tvSummary.setMaxLines(Integer.MAX_VALUE);
                    tvSummary.setEllipsize(null);
                    tvToggle.setText("Thu gọn");
                }
                isExpanded = !isExpanded;
            }
        });
    }
    //Hiển thị các thể loại của sách dưới dạng tag trong FlexboxLayout
    private void setupBookCategories(Book book) {
        FlexboxLayout tagContainer = findViewById(R.id.tagContainer);
        tagContainer.removeAllViews();

        for (Category category : book.getListCategories()) {
            TextView tagView = new TextView(this);
            tagView.setText(category.getName());
            tagView.setTextColor(Color.WHITE);
            tagView.setTextSize(14);

            tagView.setOnClickListener(v -> {
                Intent intent = new Intent(OpenBookActivity.this, DetailActivity.class);
                intent.putExtra("selectedCategory", category.getName()); // Truyền tên thể loại sang DetailActivity
                startActivity(intent);
            });

            tagView.setBackgroundResource(R.drawable.button_rounded_border_in_open_book);
            tagView.setPadding(24, 12, 24, 12);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            tagView.setLayoutParams(params);

            tagView.setMaxWidth(500);
            tagView.setSingleLine(true);

            tagContainer.addView(tagView);
        }
    }

    private void loadFragmentWithBookId(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", bookId);
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFrame, fragment).commit();
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

    @Override
    public void onSuccess(Integer data) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (bookId != -1) getBookTotalReview(bookId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (bookId != -1) getBookTotalReview(bookId);
    }
}
