package com.example.iread.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.iread.Home.HomeFragment;
import com.example.iread.JWT.JwtUtils;
import com.example.iread.MainActivity;
import com.example.iread.Model.Account;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;


import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editUsername, editPassword;

    AppCompatButton btnLogin;

    private IAppApiCaller iAppApiCaller;
    private TextView txtIntentRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        makeStatusBarTransparent();
        applyTopPadding();
        Paper.init(this);

        editUsername = findViewById(R.id.usernameEditText);
        editPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.btn_login_open);

        // 🔥 Load username nếu đã lưu trước đó
        String savedUsername = Paper.book().read("user");
        String savedPassword = Paper.book().read("password");

        if (savedUsername != null) {
            editUsername.setText(savedUsername);
        }
        if (savedPassword != null) {
            editPassword.setText(savedPassword);
        }


        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        btnLogin.setOnClickListener(view -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng đăng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            login(username, password);
        });



        txtIntentRegister = findViewById(R.id.txt_intent_register);
        txtIntentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);

        iAppApiCaller.login(account).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                    String token = response.body().getData();
                    String userId = JwtUtils.getUserIdFromToken(token);
                    if (userId != null){
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        sharedPreferences.edit()
                                .putString("username", username)
                                .putString("userId", userId)
                                .putString("token", token)
                                .apply();
                    }
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    //Lưu thông tin username và password
                    Paper.book().write("user", account.getUsername());
                    Paper.book().write("password", account.getPassword());


                     startActivity(new Intent(LoginActivity.this, MainActivity.class));
                     finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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