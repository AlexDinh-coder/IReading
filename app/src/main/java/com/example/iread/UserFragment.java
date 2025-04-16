package com.example.iread;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.iread.Account.LoginOpenActivity;
import com.example.iread.Payment.PaymentActivity;

public class UserFragment extends Fragment {
    private AppCompatButton upgradeAccount;
    private TextView btnLogOut, btnPayment;
    private TextView username;

    private ImageView avtar;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        btnLogOut = view.findViewById(R.id.btnLogout);
        username = view.findViewById(R.id.username);
        btnPayment = view.findViewById(R.id.btnPayment);
        upgradeAccount = view.findViewById(R.id.upgradeAccount);
        avtar = view.findViewById(R.id.avatar);

        btnPayment.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PaymentActivity.class);
            startActivity(intent);
        });

        upgradeAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SubscriptionActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usernameValue = sharedPreferences.getString("username", "");
        String token = sharedPreferences.getString("token", "");

        String avatarUrl = sharedPreferences.getString("avatar", "");
        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.error_image) // ảnh mặc định nếu lỗi
                    .into(avtar);
        }

        //  Nếu chưa đăng nhập, chuyển sang LoginOpenActivity
        if (token.isEmpty()) {
            Intent intent = new Intent(getContext(), LoginOpenActivity.class);
            startActivity(intent);
           requireActivity().finish(); // tránh quay lại màn cũ bằng back
            return view;
        }

        // Nếu đã đăng nhập
        username.setText(usernameValue);
        btnLogOut.setVisibility(VISIBLE);

        // Đăng xuất
        btnLogOut.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply(); // Xóa toàn bộ dữ liệu
            Toast.makeText(requireContext(), "Bạn đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
