package com.example.iread.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.iread.JWT.JwtUtils;
import com.example.iread.MainActivity;
import com.example.iread.Model.Account;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.tasks.Task;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private AppCompatButton btnLogin;
    private ImageButton gmailLogin;
    private TextView txtIntentRegister, txtForgotPassword;

    private IAppApiCaller iAppApiCaller;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences sharedPreferences;

    private ImageView togglePassword;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        makeStatusBarTransparent();
        applyTopPadding();

        Paper.init(this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);

        initViews();
        setupLogin();
        setupGoogleLogin();
    }

    private void initViews() {
        editUsername = findViewById(R.id.usernameEditText);
        editPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.btn_login_open);
        gmailLogin = findViewById(R.id.gmailLogin);
        txtIntentRegister = findViewById(R.id.btn_Register);
        txtForgotPassword = findViewById(R.id.btnForgotPassword);
        togglePassword = findViewById(R.id.togglePasswordVisibility);
        // Trạng thái hiển thị mật khẩu
        final boolean[] isConfirmPassword = {false};

        // Toggle mật khẩu
        togglePassword.setOnClickListener(v -> {
            isConfirmPassword[0] = !isConfirmPassword[0];
            editPassword.setInputType(isConfirmPassword[0] ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editPassword.setSelection(editPassword.getText().length());
            togglePassword.setImageResource(isConfirmPassword[0] ? R.drawable.ic_show : R.drawable.ic_hide);
        });

        String savedUsername = Paper.book().read("user");
        String savedPassword = Paper.book().read("password");

        if (savedUsername != null) editUsername.setText(savedUsername);
        if (savedPassword != null) editPassword.setText(savedPassword);
    }

    private void setupLogin() {
        btnLogin.setOnClickListener(view -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            login(username, password);
        });

        txtIntentRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }
    //Cấu hình đăng nhập google mail
    private void setupGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))// lấy Client ID từ Google Cloud Console
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        gmailLogin.setOnClickListener(v -> {
            signOutGoogle();
        });
    }

    // 🔑 Handle Google Sign-in Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();

                if (account != null) {
                    String email = account.getEmail();
                    String name = account.getDisplayName();
                    String avatar = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

                    loginWithGoogle(email, name, avatar);
                }
            } else {
                Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginWithGoogle(String email, String name, String avatarUrl) {
        Account account = new Account();
        account.setEmail(email);
        account.setFullName(name);
        account.setAvatar(avatarUrl);

        iAppApiCaller.loginWithGoogle(account).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSussess()) {
                    String token = response.body().getData();
                    String userId = JwtUtils.getUserIdFromToken(token);

                    if (userId != null) {
                        sharedPreferences.edit()
                                .putString("username", name)
                                .putString("email", email)
                                .putString("avatar", avatarUrl)
                                .putString("userId", userId)
                                .putString("token", token)
                                .apply();
                    }

                    Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void signOutGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(this, "Đã đăng xuất Google. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();

            // Sau khi sign out, gọi lại login intent
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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

                    if (userId != null) {
                        sharedPreferences.edit()
                                .putString("username", username)
                                .putString("userId", userId)
                                .putString("token", token)
                                .apply();
                    }

                    Paper.book().write("user", username);
                    Paper.book().write("password", password);

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
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
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
        if (resourceId > 0) result = getResources().getDimensionPixelSize(resourceId);
        return result;
    }
}
