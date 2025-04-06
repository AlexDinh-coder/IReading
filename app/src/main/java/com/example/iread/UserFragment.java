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

import com.example.iread.Account.LoginActivity;
import com.example.iread.Account.LoginOpenActivity;


import java.util.ArrayList;

public class UserFragment extends Fragment {
    Button logOut1, login;
    TextView username;


    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        username = view.findViewById(R.id.logout);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username1 = sharedPreferences.getString("username", "");
        username.setText(username1);
        login = view.findViewById(R.id.btnlogin);
        logOut1 = view.findViewById(R.id.btnLogout);
        logOut1.setOnClickListener(category -> {
          //  SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString("username", "")
                    .putString("userId", "")
                    .putString("token", "")
                    .apply();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);



        });
        login.setOnClickListener(category -> {
            Intent intent = new Intent(getContext(), LoginOpenActivity.class);
            startActivity(intent);



        });
        if(!username1.isEmpty()){
            logOut1.setVisibility(VISIBLE);
            login.setVisibility(GONE);
        } else {
            logOut1.setVisibility(GONE);
            login.setVisibility(VISIBLE);
        }
        return view;
    }
}