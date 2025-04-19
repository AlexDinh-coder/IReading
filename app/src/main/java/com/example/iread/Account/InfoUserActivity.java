package com.example.iread.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.iread.ChangePasswordActivity;
import com.example.iread.Interface.ImgurApi;
import com.example.iread.MainActivity;
import com.example.iread.Model.Account;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.gson.JsonObject;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoUserActivity extends AppCompatActivity {
    private TextView txtUserName, txtPhoneNumber, txtEmail, txtChangePassword;

    private IAppApiCaller iAppApiCaller;

    private EditText txtFullName;

    private ImageButton btnBack;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private ImageView btnCamera, imgAvatar, btnEditName;

    private Uri selectedImageUri;

    private Button btnSave;

    @SuppressLint({"WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_user);
        makeStatusBarTransparent();
        applyTopPadding();
        txtFullName = findViewById(R.id.txtFullName);
        txtUserName = findViewById(R.id.txtUserName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditName = findViewById(R.id.btnEditName);
        LinearLayout layoutEditBox = findViewById(R.id.layoutEditBox);

        btnEditName.setOnClickListener(v -> {
            txtFullName.setFocusable(true);
            txtFullName.setFocusableInTouchMode(true);
            txtFullName.setClickable(true);
            txtFullName.requestFocus();
            txtFullName.setSelection(txtFullName.getText().length());

            layoutEditBox.removeAllViews();

            // Tạo layout chứa 2 nút
            LinearLayout btnRow = new LinearLayout(this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            btnRow.setGravity(Gravity.CENTER);
            btnRow.setPadding(0, 20, 0, 0);

            // Nút LƯU
            Button btnSave = new Button(this);
            btnSave.setText("LƯU");
            btnSave.setTextColor(Color.WHITE);
            btnSave.setBackground(createRoundedRippleDrawable("#02c18e", 12));
            btnSave.setPadding(40, 20, 40, 20);

            // Nút HỦY
            Button btnCancel = new Button(this);
            btnCancel.setText("HỦY");
            btnCancel.setTextColor(Color.WHITE);
            btnCancel.setBackground(createRoundedRippleDrawable("#444444", 12));
            btnCancel.setPadding(40, 20, 40, 20);

            // Căn lề giữa 2 nút
            LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cancelParams.setMargins(20, 0, 0, 0);

            btnSave.setLayoutParams(saveParams);
            btnCancel.setLayoutParams(cancelParams);

            btnCancel.setOnClickListener(cancelView -> {
                txtFullName.setFocusable(false);
                txtFullName.setFocusableInTouchMode(false);
                txtFullName.setClickable(false);
                layoutEditBox.setVisibility(View.GONE);
            });

            btnSave.setOnClickListener(saveView -> {
                updateUserInfo();
                txtFullName.setFocusable(false);
                txtFullName.setFocusableInTouchMode(false);
                txtFullName.setClickable(false);
                layoutEditBox.setVisibility(View.GONE);
            });

            btnRow.addView(btnSave);
            btnRow.addView(btnCancel);
            layoutEditBox.addView(btnRow);

            // 👇 Animation xuất hiện
            Animation fadeSlide = new AnimationSet(true);
            AlphaAnimation fade = new AlphaAnimation(0f, 1f);
            fade.setDuration(300);

            TranslateAnimation slide = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f);
            slide.setDuration(300);

            ((AnimationSet) fadeSlide).addAnimation(fade);
            ((AnimationSet) fadeSlide).addAnimation(slide);

            layoutEditBox.setVisibility(View.VISIBLE);
            layoutEditBox.startAnimation(fadeSlide);
        });



//
//        btnEditName.setOnClickListener(v -> {
//            txtFullName.setFocusable(true);
//            txtFullName.setFocusableInTouchMode(true);
//            txtFullName.setClickable(true);
//            txtFullName.requestFocus();
//            txtFullName.setSelection(txtFullName.getText().length());
//        });

        btnSave = findViewById(R.id.btnSaveInfo);
        btnSave.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadToImgurAndUpdateProfile(selectedImageUri); // Có ảnh mới
            } else {
                updateUserInfo(); // Không đổi ảnh, chỉ lưu thông tin
            }
        });
        txtChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(InfoUserActivity.this, MainActivity.class);
            intent.putExtra("fragment", "user");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnCamera = findViewById(R.id.imgCamera);
        btnCamera.setOnClickListener(v -> openGalery());


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        if (!username.isEmpty()) {
            loadUserInfor(username);
        }else {
            Toast.makeText(this, "Không tìm thấy tài khoản người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private Drawable createRoundedRippleDrawable(String bgColor, int cornerDp) {
        float radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, cornerDp, getResources().getDisplayMetrics());

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(bgColor));
        shape.setCornerRadius(radius);

        ColorStateList rippleColor = ColorStateList.valueOf(Color.parseColor("#33FFFFFF"));
        return new RippleDrawable(rippleColor, shape, null);
    }

    private void updateUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String avatarUrl = sharedPreferences.getString("avatar", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản người dùng", Toast.LENGTH_SHORT).show();
            return;
        }
        Account account = new Account();
        account.setUsername(username);
        account.setFullName(txtFullName.getText().toString());
        account.setPhoneNumber(txtPhoneNumber.getText().toString());
        account.setEmail(txtEmail.getText().toString());
        account.setAvatar(avatarUrl);


        iAppApiCaller.updateInformation(account).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InfoUserActivity.this, "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InfoUserActivity.this, "Không thể lưu thông tin!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, "Lỗi khi gọi API!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void loadUserInfor(String username) {
        iAppApiCaller.viewAccountInfo(username).enqueue(new Callback<ReponderModel<Account>>() {
            @Override
            public void onResponse(Call<ReponderModel<Account>> call, Response<ReponderModel<Account>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account account = response.body().getData();
                    if (account != null) {
                        txtFullName.setText(account.getFullName());
                        txtUserName.setText(account.getUsername());
                        txtPhoneNumber.setText(account.getPhoneNumber());
                        txtEmail.setText(account.getEmail());

                        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        String newAvatar = sp.getString("avatar", account.getAvatar());

                        Glide.with(InfoUserActivity.this)
                                .load(account.getAvatar())
                                .placeholder(R.drawable.icon_avatar)
                                .skipMemoryCache(true) // ❗ Bỏ cache RAM
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // ❗ Bỏ cache disk
                                .into(imgAvatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Account>> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgAvatar.setImageURI(selectedImageUri);
                uploadToImgurAndUpdateProfile(selectedImageUri);
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
//            if (data.getData() != null) {
//                imgAvatar.setImageURI(data.getData());
//            }
//        }
    }
    private RequestBody getRequestBodyFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return RequestBody.create(MediaType.parse("image/*"), bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void uploadToImgurAndUpdateProfile(Uri selectedImageUri) {
        RequestBody requestBody = getRequestBodyFromUri(selectedImageUri);
        if (requestBody == null) {
            Toast.makeText(this, "Không thể đọc ảnh từ thiết bị", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "avatar.jpg", requestBody);

        ImgurApi imgurApi = RetrofitClient.ImgurClient.getClient();
        imgurApi.uploadImage("Client-ID ade11444bbdac5b", body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imgurLink = response.body().getAsJsonObject("data").get("link").getAsString();
                    updateAvatarToServer(imgurLink);
                } else {
                    Toast.makeText(InfoUserActivity.this, "Upload thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, "Upload ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAvatarToServer(String imgurLink) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putString("avatar", imgurLink).apply(); // lưu local
        selectedImageUri = null;

        Glide.with(this)
                .load(imgurLink + "?t=" + System.currentTimeMillis())
                .placeholder(R.drawable.icon_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imgAvatar);
        // 👉 Gửi avatar mới kèm thông tin lên server
        Account account = new Account();
        account.setUsername(sharedPreferences.getString("username", ""));
        account.setFullName(txtFullName.getText().toString());
        account.setPhoneNumber(txtPhoneNumber.getText().toString());
        account.setEmail(txtEmail.getText().toString());
        account.setAvatar(imgurLink); // rất quan trọng

        iAppApiCaller.updateInformation(account).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InfoUserActivity.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, "Lỗi lưu!", Toast.LENGTH_SHORT).show();
            }
        });

        //updateUserInfo();
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