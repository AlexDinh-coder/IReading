package com.example.iread.OpenBook;

import static android.content.Intent.getIntent;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.iread.Interface.ParameterInterface;
import com.example.iread.Model.Book;
import com.example.iread.Model.Category;
import com.example.iread.Model.Review;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenBookActivity extends AppCompatActivity implements ParameterInterface<Integer> {
    private RecyclerView rcv;
    IAppApiCaller iAppApiCaller;
    private ReviewAdapter reviewAdapter;
    TabLayout tabLayout;

    private SwipeRefreshLayout swipeRefreshLayout;

    ImageView btnBack;
    FrameLayout contentFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        int bookId = getIntent().getIntExtra("bookId", -1);

        makeStatusBarTransparent(); // làm trong suốt status bar
        applyTopPadding();          // tránh đè nội dung lên status bar nếu cần

        rcv= findViewById(R.id.rcv_review_in_open_book);
        rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        btnBack = findViewById(R.id.imageView2);
        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại màn trước đó
        });







        List<Review> reviewList = new ArrayList<>();

        // Tạo 5 đối tượng Review
        Review review1 = new Review("Linh", "Truyện rất hay, cốt truyện hấp dẫn!");
        Review review2 = new Review("Minh", "Tác giả xây dựng nhân vật rất tốt.");
        Review review3 = new Review("Trang", "Mình đọc một mạch không dừng lại được.");
        Review review4 = new Review("Huy", "Nội dung ổn nhưng đoạn kết hơi vội.");
        Review review5 = new Review("Lan", "Tình tiết nhẹ nhàng, rất phù hợp để thư giãn.");

        // Thêm vào danh sách
        reviewList.add(review1);
        reviewList.add(review2);
        reviewList.add(review3);
        reviewList.add(review4);
        reviewList.add(review5);

        reviewAdapter = new ReviewAdapter(reviewList);
        rcv.setAdapter(reviewAdapter);

        // =========================================
        tabLayout = findViewById(R.id.tabLayout);
        contentFrame = findViewById(R.id.contentFrame);

        // Thêm 2 tab
        tabLayout.addTab(tabLayout.newTab().setText("Chương"));
        tabLayout.addTab(tabLayout.newTab().setText("Có thể bạn thích"));

        // Load tab đầu tiên mặc định
        ChapterFragment chapterFragment = new ChapterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", bookId);
        chapterFragment.setArguments(bundle);
        loadFragment(chapterFragment);



        // Lắng nghe sự kiện chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    ChapterFragment chapterFragment = new ChapterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("bookId", bookId);
                    chapterFragment.setArguments(bundle);
                    loadFragment(chapterFragment);
                } else if (tab.getPosition() == 1) {
                    MinghtLikeFragment minghtLikeFragment = new MinghtLikeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("bookId", bookId);
                    minghtLikeFragment.setArguments(bundle);
                    loadFragment(minghtLikeFragment);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        Log.d("OpenBookActivity", "Received bookId: " + bookId);
        if (bookId != -1) {
            iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);

            iAppApiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
                @Override
                public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Book book = response.body().getData();  // đây mới đúng
                        if (book != null) {
                            showBookDetailUI(book);
                        } else {
                            Log.e("OpenBookActivity", "Book is null trong data");
                        }
                    }

                }

                @Override
                public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                    Log.e("BookAPI", "Lỗi gọi API GetBookById: " + t.getMessage());
                }
            });
        }

    }
    

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
    }


    private void showBookDetailUI(Book book) {
        // 1. Hiển thị ảnh bìa sách
        ImageView imgBook = findViewById(R.id.image_characters_in_detail);
        Glide.with(this)
                .load(book.getPoster())
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_image)
                .into(imgBook);

        // 2. Hiển thị tên sách
        TextView tvName = findViewById(R.id.book_title_in_detail);
        if (tvName != null) {
            tvName.setText(book.getName());
            tvName.setTextSize(23);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        // 3. Hiển thị tên tác giả (đã thêm id vào XML là author_name)
        TextView authorName = findViewById(R.id.author_name);
        if (authorName != null) {
            authorName.setText(book.getCreateBy());
        }

        // 4. Hiển thị mô tả sách
        TextView tvSummary = findViewById(R.id.descriptionTextView);
        TextView tvToggle = findViewById(R.id.tvToggleSummary);

        //Kiểm tra 2 textview trên có tồn tại không
        if (tvSummary != null && tvToggle != null) {
            //Gắn cho summary của sách
            tvSummary.setText(book.getSummary());
            //Giới hạn hiện thị 2 dòng, phần còn lại sẽ hiện ...
            tvSummary.setMaxLines(2);
            tvSummary.setEllipsize(TextUtils.TruncateAt.END);

            tvToggle.setText("Xem thêm");
            //Xử lí phần click hiện nút "xem thêm" và "rút gọn"
            tvToggle.setOnClickListener(new View.OnClickListener() {
                boolean isExpanded = false; //false: thu gọn, true = mở rộng

                @Override
                public void onClick(View v) {
                    if (isExpanded) {
                        //kiểm tra xem nếu đầy đủ => thu gọn
                        tvSummary.setMaxLines(2);
                        tvSummary.setEllipsize(TextUtils.TruncateAt.END);
                        tvToggle.setText("Xem thêm");
                    } else {
                        //nếu thu gọn => hiện đầy đủ
                        tvSummary.setMaxLines(Integer.MAX_VALUE);
                        tvSummary.setEllipsize(null);
                        tvToggle.setText("Thu gọn");
                    }
                    //Đổi trạng thái khi mỗi lần click
                    isExpanded = !isExpanded;
                }
            });
        }

        // 5. Hiển thị thể loại đầu tiên của sách
        FlexboxLayout tagContainer = findViewById(R.id.tagContainer);
        tagContainer.removeAllViews(); // Xóa tag cũ (nếu có)

        for (Category category : book.getListCategories()) {
            TextView tagView = new TextView(this);
            tagView.setText(category.getName());
            tagView.setTextColor(Color.WHITE);
            tagView.setTextSize(14);
            tagView.setBackgroundResource(R.drawable.button_rounded_border_in_open_book);
            tagView.setPadding(24, 12, 24, 12);

            // Thiết lập layout params cho từng tag trong FlexboxLayout
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,// Chiều rộng theo nội dung
                    LinearLayout.LayoutParams.WRAP_CONTENT // Chiều cao theo nội dung
            );
            // Thêm khoảng cách giữa các tag (margin)
            params.setMargins(10, 10, 10, 10);
            tagView.setLayoutParams(params);

            tagView.setMaxWidth(500); // Giới hạn chiều rộng để không bị wrap đứng
            tagView.setSingleLine(true); // Không xuống dòng
//            tagView.setEllipsize(TextUtils.TruncateAt.END); // Nếu dài thì hiển thị "..."

            tagContainer.addView(tagView);
        }




    }



    private TextView createTextView(String text, int sizeSp, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(sizeSp);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(16, 8, 16, 8);
        if (bold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
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

    @Override
    public void onSuccess(Integer data) {


    }
}
