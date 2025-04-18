package com.example.iread.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iread.MainActivity;
import com.example.iread.Model.Account;
import com.example.iread.Model.Author;
import com.example.iread.Model.Book;
import com.example.iread.OpenBook.BookByCategoryAdapter;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, Email, FullName, NumberPhone, Password, ConfirmPassword;
    private Button btn_Register, btnCancel;

    private Account account;
    private IAppApiCaller apiCaller;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.usernameEditText);
        Email = findViewById(R.id.EmailEditText);
        FullName = findViewById(R.id.FullNameEditText);
        NumberPhone = findViewById(R.id.NumberPhoneEditText);
        Password = findViewById(R.id.PassWordEditText);
        ConfirmPassword = findViewById(R.id.ComEditText);


        btn_Register = findViewById(R.id.btn_Register);
        btn_Register.setOnClickListener(v -> Register());

        makeStatusBarTransparent();
        applyTopPadding();
    }

    private void Register() {
        String user = username.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String name = FullName.getText().toString().trim();
        String phone = NumberPhone.getText().toString().trim();
        String pass = Password.getText().toString();
        String confirmPass = ConfirmPassword.getText().toString();

        //Account account = new Account(user, name, email, phone, pass, confirmPass, 0);
        Account account = new Account();
        account.setUsername(user);
        account.setFullName(name);
        account.setEmail(email);
        account.setPhoneNumber(phone);
        account.setPassword(pass);
        account.setPasswordConfirm(confirmPass);
        account.setGrantPerMission(0);
        account.setRegisterType(0); // 0: user

        if (user.isEmpty() || email.isEmpty() || name.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }


        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        apiCaller.Register(account).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    int status = response.body().getStatusCode();

                    if (status ==200) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        // Có thể chuyển qua màn hình đăng nhập hoặc trang chủ ở đây
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Log.e("API Category", "Lỗi khi gọi API category: " + t.getMessage());
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