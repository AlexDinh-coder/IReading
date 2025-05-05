package com.example.iread.OpenBook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.example.iread.Audio.AudioActivity;
import com.example.iread.Book.ActivityBook;
import com.example.iread.Comment.ReviewActivity;
import com.example.iread.CommentActivity;
import com.example.iread.DetailActivity;
import com.example.iread.Interface.ParameterInterface;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.Category;
import com.example.iread.Model.CommentModel;
import com.example.iread.Model.UserTranscationBook;
import com.example.iread.R;
import com.example.iread.SubscriptionActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
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
    private TextView ratingTextStarTop, ratingTextStarBottom, txtNewChapter,txtChapterName;
    MaterialButton btnBookRead, btnBookListen;

    ImageView iconShow, iconLove, imagePoster;
    AppCompatButton btnActionBook, btnUpgrade;
    ImageView[] ratingStarBottom = new ImageView[5];
    private ActivityResultLauncher<Intent> commentLauncher;
    private ImageView btnBack, btnNote;
    private FrameLayout contentFrame;

    private AppCompatButton btnRead;
    private boolean isFavorite = false;
    private int bookId;

    private String username;

    private String bookTitle;
    private String currentAuthorName;
    private String currentPosterUrl;

    private boolean isPurchase;

    private int currentBookTypeStatus = 0;
    private int bookPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);
        // Khởi tạo dữ liệu
        initViews();
        setupApiCaller();
        currentBookTypeStatus = getIntent().getIntExtra("bookTypeStatus", 0);
        //Cấu hình giao diện
        setupUI();
        setupRecyclerView();
        setupTabs();
        setupCommentLauncher();
        loadBookDetails();
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
        imagePoster = findViewById(R.id.imageView3);
        txtNewChapter = findViewById(R.id.newChapter);
        txtChapterName = findViewById(R.id.chapterName);
        btnRead = findViewById(R.id.btnRead);
        btnNote = findViewById(R.id.btnNote);
        btnTextReview.setVisibility(View.GONE);
        iconLove.setVisibility(View.GONE);

        bookId = getIntent().getIntExtra("bookId",0);
        btnNote.setOnClickListener(v -> {
            Intent intent = new Intent(OpenBookActivity.this, NoteActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });

        //Load dữ liệu user info
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        iconLove.setOnClickListener(v -> {
            if (!isUserLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để yêu thích sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleFavoriteBook(bookId, username);
        });

        ratingStarBottom[0] = findViewById(R.id.star1);
        ratingStarBottom[1] = findViewById(R.id.star2);
        ratingStarBottom[2] = findViewById(R.id.star3);
        ratingStarBottom[3] = findViewById(R.id.star4);
        ratingStarBottom[4] = findViewById(R.id.star5);

        btnActionBook.setOnClickListener(v -> {
            if (!isUserLoggedIn()) {
                Toast.makeText(this, "Bạn cần đăng nhập để thực hiện", Toast.LENGTH_SHORT).show();
                return;
            }
            if (bookPrice > 0 && !isPurchase) {
                showPaymentDialog(bookTitle, bookPrice);
                return;
            }
            if (currentBookTypeStatus == 1) {
                openFirstAudioChapter();  // sách NGHE thì mở AudioActivity
            } else {
                openFirstChapter();       // sách ĐỌC thì mở ActivityBook
            }
        });
        btnBookRead.setOnClickListener(v -> applyBookReadMode());
        btnBookListen.setOnClickListener(v -> applyBookListenMode());
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        return userId != null && !userId.isEmpty();
    }

    private void openFirstAudioChapter() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (bookId == -1) return;

        apiCaller.getListBookChapterByUsername(username, bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();

                    // Tạo danh sách chỉ chứa các chương có file audio
                    List<BookChapter> audioChapters = new ArrayList<>();
                    for (BookChapter chapter : chapters) {
                        if (chapter.getBookType() == 2 || (chapter.getAudioUrl() != null && !chapter.getAudioUrl().isEmpty())) {
                            audioChapters.add(chapter);
                        }
                    }

                    if (!audioChapters.isEmpty()) {
                        BookChapter firstAudio = audioChapters.get(0);
                        Intent intent = new Intent(OpenBookActivity.this, AudioActivity.class);
                        intent.putExtra("chapterId", firstAudio.getId());
                        intent.putExtra("bookId", bookId);
                        intent.putExtra("chapterList", new ArrayList<>(audioChapters)); // TRUYỀN DANH SÁCH
                        startActivity(intent);
                    } else {
                        Toast.makeText(OpenBookActivity.this, "Không có chương nào có audio!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                Toast.makeText(OpenBookActivity.this, "Lỗi tải danh sách chương: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavoriteBook(int bookId, String username) {
        apiCaller.addOrRemoveFavoriteBook(bookId, username).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                    // Toggle trạng thái local
                    isFavorite = !isFavorite;
                    iconLove.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_love);

                    String msg = isFavorite ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích";
                    Toast.makeText(OpenBookActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OpenBookActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(OpenBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openNewPublishedChapter(String chapterId) {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Bạn cần đăng nhập để đọc chương mới nhất!", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (bookId == -1) return;
        apiCaller.getListBookChapterByUsername(username,bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();
                    if (chapters != null && !chapters.isEmpty()) {
                        for (int i = 0; i < chapters.size(); i++) {
                            if (chapters.get(i).getId().equals(chapterId)) {
                                ChapterDataHolder.getInstance().setChapterList(chapters);
                                Intent intent = new Intent(OpenBookActivity.this, ActivityBook.class);
                                intent.putExtra("selectedIndex", i);
                                intent.putExtra("bookId", bookId);
                                startActivity(intent);
                                return;
                            }
                        }
                        Toast.makeText(OpenBookActivity.this, "Không tìm thấy chương mới nhất!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                Toast.makeText(OpenBookActivity.this, "Lỗi khi tải chương mới: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        bookTitle = book.getName();
                        bookPrice = book.getPrice();
                        currentAuthorName = book.getCreateBy();
                        currentPosterUrl = book.getPoster();
                        iconLove.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_love); //cập nhật icon
                        showBookDetailUI(book);
                        checkReadingEnoughToEnableRating();
                        btnTextReview.setOnClickListener(v -> {
                            if (!isUserLoggedIn()) {
                                Toast.makeText(OpenBookActivity.this, "Vui lòng đăng nhập để đánh giá sách!", Toast.LENGTH_SHORT).show();
                                return;
                            }
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
    }

    private void checkReadingEnoughToEnableRating() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        if (TextUtils.isEmpty(username) || bookId == -1) {
            Log.e("CheckAccess", "Username hoặc BookId bị thiếu");
            return;
        }

        apiCaller.checkReadingEnough(username, bookId).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean canRate = response.body().isSussess();
                    btnTextReview.setVisibility(canRate ? View.VISIBLE : View.GONE);
                    iconLove.setVisibility(canRate ? View.VISIBLE : View.GONE);
                    Log.d("CheckAccess", "Người dùng đã đọc? " + canRate);
                    if (canRate) {
                        btnTextReview.setOnClickListener(v -> {
                            Intent intent = new Intent(OpenBookActivity.this, CommentActivity.class);
                            intent.putExtra("bookId", bookId);
                            intent.putExtra("bookTitle", bookTitle);
                            intent.putExtra("bookAuthor",  currentAuthorName);
                            intent.putExtra("bookImage", currentPosterUrl);
                            commentLauncher.launch(intent);
                        });
                    }

                } else {
                    btnTextReview.setVisibility(View.GONE);
                    iconLove.setVisibility(View.GONE);
                    Log.e("CheckAccess", "Lỗi API: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Log.e("CheckAccess", "Lỗi kết nối API: " + t.getMessage());
                btnTextReview.setVisibility(View.GONE);
                iconLove.setVisibility(View.GONE);
            }
        });
    }

    //Xử lí phần khi click đọs sach nhảy sang nội dung của chương
    private void openFirstChapter() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (bookId == -1) return;
        apiCaller.getListBookChapterByUsername(username,bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();
                    if (chapters != null && !chapters.isEmpty()) {
                        // Lấy nội dung của chương sách đầu tiên
                        Collections.sort(chapters, Comparator.comparing(BookChapter::getChapterNumber));
                        ChapterDataHolder.getInstance().setChapterList(chapters);
                        Intent intent = new Intent(OpenBookActivity.this, ActivityBook.class);
                        intent.putExtra("bookId", bookId);
                        intent.putExtra("selectedIndex", 0);
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
        currentBookTypeStatus = 1;
        iconShow.setImageResource(R.drawable.ic_headphone);
        btnBookListen.setTextColor(Color.WHITE);
        btnBookRead.setTextColor(Color.GRAY);
        btnBookListen.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnBookRead.setBackgroundTintList(getColorStateList(R.color.dark_gray));
        btnActionBook.setText("NGHE THỬ");
        if (bookPrice > 0 && !isPurchase) {
            btnActionBook.setOnClickListener(v -> {
                Toast.makeText(this, "Bạn cần mua sách mới có thể nghe audio", Toast.LENGTH_SHORT).show();

            });
        } else {
            btnActionBook.setOnClickListener(v -> {
                if (!isUserLoggedIn()) {
                    Toast.makeText(this, "Bạn cần đăng nhập để nghe sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                openFirstAudioChapter();
            });
        }

        btnUpgrade.setVisibility(View.VISIBLE);
        btnUpgrade.setOnClickListener(v -> {
            if (!isUserLoggedIn()) {
                Toast.makeText(this, "Bạn cần đăng nhập để nâng cấp tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(OpenBookActivity.this, SubscriptionActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });
        // Ẩn phần chương mới nhất nếu đang ở chế độ sách nói
        LinearLayout layoutNewChapter = findViewById(R.id.layoutNewChapter);
        layoutNewChapter.setVisibility(View.GONE);
    }

    //Xử lí phần sách nghe
    private void applyBookReadMode() {
        currentBookTypeStatus = 0;
        iconShow.setImageResource(R.drawable.ic_show);
        btnBookRead.setTextColor(Color.WHITE);
        btnBookListen.setTextColor(Color.GRAY);
        btnBookRead.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnBookListen.setBackgroundTintList(getColorStateList(R.color.dark_gray));

        if (bookPrice > 0 && !isPurchase) {
            btnActionBook.setText("MUA SÁCH");
            btnActionBook.setOnClickListener(v -> {
                if (!isUserLoggedIn()) {
                    Toast.makeText(this, "Bạn cần đăng nhập để mua sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                showPaymentDialog(bookTitle, bookPrice);
            });
            //btnActionBook.setOnClickListener(v -> showPaymentDialog(bookTitle, bookPrice));
            btnRead.setVisibility(View.GONE);
        } else {
            btnActionBook.setText("ĐỌC SÁCH");
            btnActionBook.setOnClickListener(v -> {
                if (!isUserLoggedIn()) {
                    Toast.makeText(this, "Bạn cần đăng nhập để đọc sách!", Toast.LENGTH_SHORT).show();
                    return;
                }
                openFirstChapter();
            });
            //btnActionBook.setOnClickListener(v -> openFirstChapter());
            btnRead.setVisibility(View.VISIBLE);
        }

        btnUpgrade.setVisibility(View.GONE);
        LinearLayout layoutNewChapter = findViewById(R.id.layoutNewChapter);
        layoutNewChapter.setVisibility(View.VISIBLE);
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
       // bundle.putInt("bookTypeStatus", bookTypeStatus); // 0: đọc, 1: nghe
        bundle.putInt("bookTypeStatus", currentBookTypeStatus);
        bundle.putString("bookTitle", bookTitle);
        bundle.putInt("bookPrice", bookPrice);
        bundle.putBoolean("isPurchase", isPurchase);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
    }

    //Hàm để khởi tạo các tab ấn click vô mục chương, có thể bạn thích
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Chương"));
        tabLayout.addTab(tabLayout.newTab().setText("Có thể bạn thích"));
        if (isPurchase || getIntent().getBooleanExtra("isFreeBook", false)) {
            loadFragmentWithBookId(new ChapterFragment(), 0);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    loadFragmentWithBookId(new ChapterFragment(), currentBookTypeStatus);
                } else if (tab.getPosition() == 1) {
                    loadFragmentWithBookId(new MinghtLikeFragment());
                }
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
                        Log.d("DEBUG_AVATAR", "Tên: " + comment.getFullName() + ", Avatar: " + comment.getAvatar());
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
                .error(R.drawable.error_image)
                .into(imgBook);

        Glide.with(this).load(book.getPoster())
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_image)
                .into(imagePoster);

        TextView txtPriceOnPoster = findViewById(R.id.txtPriceOnPoster);
        if (book.getPrice() > 0) {
            txtPriceOnPoster.setText(String.valueOf(book.getPrice()));
            txtPriceOnPoster.setVisibility(View.VISIBLE);
        } else {
            txtPriceOnPoster.setVisibility(View.GONE);
        }

        if (currentBookTypeStatus == 1) {
            applyBookListenMode();
        } else {
            if (book.getPrice() > 0) {
                if (isPurchase) {
                    btnActionBook.setText("ĐỌC SÁCH");
                    btnRead.setVisibility(View.VISIBLE);
                } else {
                    btnActionBook.setText("MUA SÁCH");
                    btnActionBook.setOnClickListener(v -> showPaymentDialog(book.getName(), book.getPrice()));
                    btnRead.setVisibility(View.GONE);
                }
            } else {
                applyBookReadMode();
            }
            btnUpgrade.setVisibility(currentBookTypeStatus == 1 ? View.VISIBLE : View.GONE);
        }

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

        LinearLayout layoutNewChapter = findViewById(R.id.layoutNewChapter);
       TextView txtNewChapter = findViewById(R.id.newChapter);
        TextView txtChapterName = findViewById(R.id.chapterName);
        AppCompatButton btnRead = findViewById(R.id.btnRead);

        if (currentBookTypeStatus == 0 && book.isNewPublishedChapter() && book.getNewPublishedChapter() != null) {
            String chapterName = book.getNewPublishedChapter().getName();
            String publishTime = book.getNewPublishedChapter().getNewPublishedDateTime();
            String chapterId = book.getNewPublishedChapter().getId();

            layoutNewChapter.setVisibility(View.VISIBLE);
            txtChapterName.setText(chapterName + " | " + publishTime);

            btnRead.setOnClickListener(v -> {
                if (!isUserLoggedIn()) {
                    Toast.makeText(this, "Bạn cần đăng nhập để đọc chương mới nhất", Toast.LENGTH_SHORT).show();
                    return;
                }
                openNewPublishedChapter(chapterId);
            });
        } else {
            layoutNewChapter.setVisibility(View.GONE);
        }

        setupBookSummary(book);
        setupBookCategories(book);
    }


    private void showPaymentDialog(String bookTitle, int price) {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Bạn cần đăng nhập để mua sách!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextView tvTitle = view.findViewById(R.id.tvPaymentTitle);
        TextView tvContent = view.findViewById(R.id.tvPaymentContent);
        TextView tvPrice = view.findViewById(R.id.tvPaymentPrice);
        TextView tvBalance = view.findViewById(R.id.tvCurrentBalance);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        tvTitle.setText("THANH TOÁN");
        tvContent.setText("Bạn đang mua: " + bookTitle + " qua kênh thanh toán App.");
        tvPrice.setText("Đơn giá: " + formatXu(price));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String userId = preferences.getString("userId", "");

        //  Gọi API để lấy xu từ getUserProfile
        apiCaller.getUserProfile(username).enqueue(new Callback<ReponderModel<com.example.iread.Model.UserProfile>>() {
            @Override
            public void onResponse(Call<ReponderModel<com.example.iread.Model.UserProfile>> call, Response<ReponderModel<com.example.iread.Model.UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    long currentCoin = response.body().getData().getClamPoint();
                    tvBalance.setText("Xu hiện tại: " + formatXu(currentCoin));
                    if (currentCoin < price) {
                        tvBalance.setTextColor(Color.RED);
                    } else {
                        tvBalance.setTextColor(Color.parseColor("#4CAF50"));
                    }

                    btnConfirm.setOnClickListener(v -> {
                        if (currentCoin < price) {
                            Toast.makeText(OpenBookActivity.this, "Bạn không đủ xu để mua sách!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Gửi yêu cầu thanh toán
                        UserTranscationBook payment = new UserTranscationBook();
                        payment.setUsername(username);
                        payment.setUserId(userId);
                        payment.setBookId(bookId);
                        payment.setAmount(price);

                        apiCaller.postPaymentItem(payment).enqueue(new Callback<ReponderModel<String>>() {
                            @Override
                            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                                    Toast.makeText(OpenBookActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                                    // Cập nhật trạng thái mua sách
                                    isPurchase = true;

                                    // Gọi lại kiểm tra mua từ server để đồng bộ chính xác
                                    checkBookPurchasedFromServer(bookId, username, bookTitle, () -> {
                                        applyBookReadMode(); // Cập nhật lại UI đọc sách
                                        loadFragmentWithBookId(new ChapterFragment(), 0); // load lại fragment chương
                                    });

                                    // Ẩn dialog
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(OpenBookActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                                Toast.makeText(OpenBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                } else {
                    Toast.makeText(OpenBookActivity.this, "Lỗi lấy thông tin xu!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<com.example.iread.Model.UserProfile>> call, Throwable t) {
                Toast.makeText(OpenBookActivity.this, "Lỗi kết nối API xu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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

    private String formatXu(long value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " xu";
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
        if (bookId != -1) {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            username = preferences.getString("username", "");

            apiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
                @Override
                public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Book book = response.body().getData();
                        if (book != null) {
                            bookTitle = book.getName();
                            bookPrice = book.getPrice();
                            checkReadingEnoughToEnableRating();

                            // Gọi lại UI theo loại sách hiện tại
                            checkBookPurchasedFromServer(bookId, username, bookTitle, () -> {
                                runOnUiThread(() -> {
                                    showBookDetailUI(book);
                                    loadFragmentWithBookId(new ChapterFragment(), currentBookTypeStatus);
                                });
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                    Log.e("BookAPI", "Lỗi gọi GetBookById: " + t.getMessage());
                }
            });

            getBookTotalReview(bookId);
        }
    }




    @Override
    protected void onRestart() {
        super.onRestart();
        if (bookId != -1) getBookTotalReview(bookId);
    }

    private void checkBookPurchasedFromServer(int bookId, String username, String bookTitle, Runnable onFinished) {
        apiCaller.getListBookChapterByUsername(username, bookId).enqueue(new Callback<ReponderModel<BookChapter>>() {
            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                isPurchase = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<BookChapter> chapters = response.body().getDataList();
                    if (chapters != null && !chapters.isEmpty()) {
                        isPurchase = true;
                        for (BookChapter chapter : chapters) {
                            if (!chapter.isPaidChapter()) {
                                isPurchase = false;
                                break;
                            }
                        }
                    }
                }

                if (onFinished != null) {
                    onFinished.run(); // Sau khi kiểm tra xong thì load UI
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {
                Log.e("CHECK_PURCHASE", "Lỗi khi kiểm tra mua sách: " + t.getMessage());
                if (onFinished != null) onFinished.run();
            }
        });
    }

}
