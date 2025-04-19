package com.example.iread.Account;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.iread.Model.Account;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private IAppApiCaller iAppApiCaller;

    private EditText txtEmail;

    private AppCompatButton btnSend;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        makeStatusBarTransparent();
        applyTopPadding();
        txtEmail = findViewById(R.id.btnEmail);
        btnSend = findViewById(R.id.btnContinue);

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        btnSend.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            if(email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            Account account = new Account();
            account.setEmail(email);

            iAppApiCaller.forgotPassword(account).enqueue(new Callback<ReponderModel<String>>() {
                @Override
                public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra email để lấy lại mật khẩu", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<String>> call, Throwable t) {

                }
            });

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