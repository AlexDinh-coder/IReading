package com.example.iread;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.iread.Model.CommentModel;
import com.example.iread.OpenBook.OpenBookActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    private EditText editText;
    private View btnSend;
    private IAppApiCaller iAppApiCaller;

    private ImageView[] ratingStar = new ImageView[5];

    ImageView imageView;
    TextView bookTitle, bookAuthor;

    private int rating = 5;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        makeStatusBarTransparent();
        applyTopPadding();

        imageView = findViewById(R.id.bookImage);
        bookTitle = findViewById(R.id.txtBookTitle);
        bookAuthor = findViewById(R.id.txtBookAuthor);

        int bookId = getIntent().getIntExtra("bookId", -1);
        String title = getIntent().getStringExtra("bookTitle");
        String author = getIntent().getStringExtra("bookAuthor");
        String image = getIntent().getStringExtra("bookImage");
        bookTitle.setText(title);
        bookAuthor.setText(author);
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.loading_placeholder) // ảnh tạm nếu chưa có
                .error(R.drawable.error_image)       // ảnh lỗi
                .into(imageView);



        editText = findViewById(R.id.editTextReview);
        btnSend = findViewById(R.id.btnSend);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String username = sharedPreferences.getString("username", "User");


        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);

        ratingStar[0] = findViewById(R.id.Idstar1);
        ratingStar[1] = findViewById(R.id.Idstar2);
        ratingStar[2] = findViewById(R.id.Idstar3);
        ratingStar[3] = findViewById(R.id.Idstar4);
        ratingStar[4] = findViewById(R.id.Idstar5);

         //Gắn click từng star
        for (int i = 0; i < ratingStar.length; i++) {
            final int index = i;
            ratingStar[i].setOnClickListener(v -> {
                rating = index + 1;
                updateStarUI(rating);
            });
        }

        //Xu li phan send
        btnSend.setOnClickListener(v -> {
            String content = editText.getText().toString().trim();
            if (content.isEmpty()){
                Toast.makeText(this, "Hãy cho chúng mình một vài nhận xét & đóng góp ý kiến nhé !", Toast.LENGTH_SHORT).show();
                return;
            }



            CommentModel commentModel = new CommentModel();
            commentModel.setBookId(bookId);
            commentModel.setContent(content);
            commentModel.setRating(rating);
            commentModel.setCreateBy(username);
            commentModel.setUserId(userId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String currentDate = sdf.format(new Date());
            commentModel.setCreateDate(currentDate);

            postComment(commentModel);
        });
    }

    private void updateStarUI(int rating) {
        for (int i =0; i < ratingStar.length; i++) {
            if (i < rating) {
                ratingStar[i].setImageResource(R.drawable.ic_star);
            } else {
                ratingStar[i].setImageResource(R.drawable.ic_star_border);
            }
        }
    }

    private void postComment(CommentModel commentModel) {
        iAppApiCaller.commentBook(commentModel).enqueue(new Callback<ReponderModel<Object>>() {
            @Override
            public void onResponse(Call<ReponderModel<Object>> call, Response<ReponderModel<Object>> response) {
                // Lấy nội dung phản hồi từ server
                ReponderModel<Object> body = response.body();

                if (body != null && body.isSussess()) {
                    // Nếu thành công: thông báo, set kết quả OK và đóng màn
                    Toast.makeText(CommentActivity.this, "Cảm ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    // Nếu thất bại: hiện lỗi (nếu có) hoặc thông báo chung
                    String errorMsg;
                    if (body != null && body.getMessage() != null) {
                        errorMsg = body.getMessage();
                    } else {
                        errorMsg = "Gửi đánh giá thất bại, vui lòng thử lại!";
                    }

                    Toast.makeText(CommentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Object>> call, Throwable t) {
                // Nếu không kết nối được server
                Toast.makeText(CommentActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
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