package com.example.iread.Comment;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.CommentModel;
import com.example.iread.Model.Review;
import com.example.iread.OpenBook.OpenBookActivity;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    List<CommentModel> reviewList;
    ImageView btnBack;

    TextView totalRating, ratingSummary;
    IAppApiCaller iAppApiCaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        makeStatusBarTransparent();
        applyTopPadding();

        recyclerView = findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        int bookId = getIntent().getIntExtra("bookId", -1);
        if (bookId != -1) {
            getBookReview(bookId);

        }
        ratingSummary = findViewById(R.id.txtRatingSummary);
        totalRating = findViewById(R.id.txtTotalReviews);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại màn trước đó
        });
    }

    private void getBookReview(int bookId) {
        iAppApiCaller.listCommentBook(bookId).enqueue(new Callback<ReponderModel<CommentModel>>() {
            @Override
            public void onResponse(Call<ReponderModel<CommentModel>> call, Response<ReponderModel<CommentModel>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDataList() != null) {
                    List<CommentModel> commentModel = response.body().getDataList();
                    Log.d("ReviewAPI", "Số lượng review nhận được: " + commentModel.size());

                    totalRating.setText(commentModel.size() + " đánh giá");
                    reviewAdapter.updateData(commentModel);

                    //Lấy tổng điểm trung bình số sao
                    float totalStar = 0;
                    for (CommentModel comment : commentModel) {
                        totalStar += comment.getRating();
                    }
                    float averageRating = commentModel.size() > 0 ? totalStar / commentModel.size() : 0f;// tổng số sao chia cho số lượng đánh giá
                    ratingSummary.setText(String.format("%.1f", averageRating));// làm tròn tới số thập phân

                    updateStarIcon(averageRating);

                } else {
                    Log.d("ReviewAPI", "Không có review hoặc response không hợp lệ");
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<CommentModel>> call, Throwable t) {
                Log.e("ReviewAPI", "Lỗi khi lấy đánh giá: " + t.getMessage(), t);
            }
        });
    }

    private void updateStarIcon(float averageRating) {
        ImageView[] star = new ImageView[]{
                findViewById(R.id.star11),
                findViewById(R.id.star22),
                findViewById(R.id.star33),
                findViewById(R.id.star44),
                findViewById(R.id.star55)
        };
        int fullStar = (int) averageRating;
        boolean halfStar = (averageRating - fullStar) >= 0.5;

        for (int i =0; i < star.length; i++){
            if ( i < fullStar){
                star[i].setImageResource(R.drawable.ic_star); // hiển thi nguyên sao
            } else if (i == fullStar && halfStar) {
                star[i].setImageResource(R.drawable.ic_star_half);
            } else {
                star[i].setImageResource(R.drawable.ic_star_border);
            }
        }

    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }
    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.fragment_container);

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
