package com.example.iread.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iread.Account.LoginOpenActivity;
import com.example.iread.MainActivity;
import com.example.iread.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_main);

        // Khởi tạo thư viện Paper
        Paper.init(this);

        new Thread(() -> {
            try {
                Thread.sleep(2000); // Delay 2s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
//                String savedUser = Paper.book().read("user");
//                String savedPassword = Paper.book().read("password");
//                if (savedUser != null && savedPassword != null) {
//                    // Nếu đã đăng nhập rồi thì vào MainActivity
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                } else {
//                    // Nếu chưa có thông tin đăng nhập
//                    startActivity(new Intent(SplashActivity.this, LoginOpenActivity.class));
//                }
//                finish();
                // Luôn chạy LoginActivity (không check user)
                Intent intent = new Intent(SplashActivity.this, LoginOpenActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}
