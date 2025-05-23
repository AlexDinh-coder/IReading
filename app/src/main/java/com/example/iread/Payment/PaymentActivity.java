package com.example.iread.Payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iread.MainActivity;
import com.example.iread.R;
import com.example.iread.UserFragment;

public class PaymentActivity extends AppCompatActivity {
    private LinearLayout btnPay;
    private ImageView btnClose;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        makeStatusBarTransparent();
        applyTopPadding();

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v->{
            Intent intent = new Intent(PaymentActivity.this , MainActivity.class);
            intent.putExtra("UserFragment", true);
            startActivity(intent);
            finish();
        });

        btnPay = findViewById(R.id.paymentBox1);
        btnPay.setOnClickListener(v->{
        Intent intent = new Intent(PaymentActivity.this , PaymentChooseActivity.class);
        startActivity(intent);
        finish();
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
        View contentContainer = findViewById(R.id.NapKhoaicontainer);
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