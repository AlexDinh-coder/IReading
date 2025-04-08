package com.example.iread;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.iread.Account.LoginOpenActivity;

public class UserFragment extends Fragment {

    private TextView btnLogOut;
    private TextView username;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        btnLogOut = view.findViewById(R.id.btnLogout);
        username = view.findViewById(R.id.username);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usernameValue = sharedPreferences.getString("username", "");
        String token = sharedPreferences.getString("token", "");

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
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
