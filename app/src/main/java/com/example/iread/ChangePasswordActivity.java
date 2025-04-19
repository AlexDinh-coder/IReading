package com.example.iread;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iread.Account.InfoUserActivity;
import com.example.iread.Account.LoginActivity;
import com.example.iread.Model.Account;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private IAppApiCaller iAppApiCaller;
    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private AppCompatButton btnChangePassword;
    private ImageView toggleOldPasswordVisibility, toggleNewPasswordVisibility, togglePasswordVisibility,btnBack;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        editOldPassword = findViewById(R.id.edtOldPassword);
        editNewPassword = findViewById(R.id.edtNewPassword);
        editConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePasswordActivity.this, InfoUserActivity.class);
            startActivity(intent);
        });

        toggleOldPasswordVisibility = findViewById(R.id.toggleOldPasswordVisibility);
        toggleNewPasswordVisibility = findViewById(R.id.toggleNewPasswordVisibility);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        // Trạng thái hiển thị mật khẩu
        final boolean[] isOldPasswordVisible = {false};
        final boolean[] isNewPasswordVisible = {false};
        final boolean[] isConfirmPasswordVisible = {false};

       // Toggle mật khẩu cũ
        toggleOldPasswordVisibility.setOnClickListener(v -> {
            isOldPasswordVisible[0] = !isOldPasswordVisible[0];
            editOldPassword.setInputType(isOldPasswordVisible[0] ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editOldPassword.setSelection(editOldPassword.getText().length());
            toggleOldPasswordVisibility.setImageResource(isOldPasswordVisible[0] ? R.drawable.ic_show : R.drawable.ic_hide);
        });

        // Toggle mật khẩu mới
        toggleNewPasswordVisibility.setOnClickListener(v -> {
            isNewPasswordVisible[0] = !isNewPasswordVisible[0];
            editNewPassword.setInputType(isNewPasswordVisible[0] ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editNewPassword.setSelection(editNewPassword.getText().length());
            toggleNewPasswordVisibility.setImageResource(isNewPasswordVisible[0] ? R.drawable.ic_show : R.drawable.ic_hide);
        });

        // Toggle nhập lại mật khẩu
        togglePasswordVisibility.setOnClickListener(v -> {
            isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];
            editConfirmPassword.setInputType(isConfirmPasswordVisible[0] ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editConfirmPassword.setSelection(editConfirmPassword.getText().length());
            togglePasswordVisibility.setImageResource(isConfirmPasswordVisible[0] ? R.drawable.ic_show : R.drawable.ic_hide);
        });

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);


        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = editOldPassword.getText().toString();
            String newPassword = editNewPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (oldPassword.equals(newPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không được giống mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:\";'<>?,./]).+$")) {
                Toast.makeText(this, "Mật khẩu phải bao gồm chữ thường, chữ in hoa, số và ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không khớp với nhau", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", "");


            Account account = new Account();
            account.setUsername(username);
            account.setOldPassword(oldPassword);
            account.setPassword(newPassword);
            account.setPasswordConfirm(confirmPassword);

            iAppApiCaller.changePassword(account).enqueue(new Callback<ReponderModel<String>>() {
                @Override
                public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ReponderModel<String> result = response.body();

                        if (result.isSussess()) {
                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                            // Xoá dữ liệu đăng nhập
                            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();

                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // Khi mật khẩu cũ không đúng hoặc lỗi logic phía server
                            Toast.makeText(ChangePasswordActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<ReponderModel<String>> call, Throwable t) {

                }
            });

        });
    }
}