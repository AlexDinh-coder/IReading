package com.example.iread;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.iread.Account.InfoUserActivity;
import com.example.iread.Account.LoginOpenActivity;
import com.example.iread.Model.UserProfile;
import com.example.iread.Payment.PaymentActivity;
import com.example.iread.Transaction.TransactionActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private AppCompatButton upgradeAccount;
    private TextView btnLogOut, btnPayment, txtCoin, btnTransactionHistory;
    private TextView username, txtUpgradeAccount, txtAccountInfo;

    private ImageView avatar;

    private IAppApiCaller iAppApiCaller;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        btnLogOut = view.findViewById(R.id.btnLogout);
        username = view.findViewById(R.id.username);
        btnPayment = view.findViewById(R.id.btnPayment);
        upgradeAccount = view.findViewById(R.id.upgradeAccount);
        avatar = view.findViewById(R.id.avatar);
        txtCoin = view.findViewById(R.id.txtCoin);
        txtUpgradeAccount = view.findViewById(R.id.btnUpdateAccount);
        txtAccountInfo = view.findViewById(R.id.viewAccountInfo);
        txtAccountInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InfoUserActivity.class);
            startActivity(intent);
        });

        btnTransactionHistory = view.findViewById(R.id.HistoryTransaction);
        btnTransactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            startActivity(intent);
        });

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
                    .into(avatar);
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
    @Override
    public void onResume() {
        // Bổ sung đoạn này để load avatar mới nhất ngay khi quay lại
        super.onResume();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String avatarUrl = sharedPreferences.getString("avatar", "");
        Log.d("DEBUG_AVATAR_URL", "Avatar from SharedPreferences: " + avatarUrl);
        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl + "?t=" + System.currentTimeMillis())
                    .placeholder(R.drawable.icon_avatar)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate() // <- thêm dòng này
                    .into(avatar);
        }

        // Gọi API để load thông tin khác (coin, gói)
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (username.isEmpty()) return;

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        iAppApiCaller.getUserProfile(username).enqueue(new Callback<ReponderModel<UserProfile>>() {
            @Override
            public void onResponse(Call<ReponderModel<UserProfile>> call, Response<ReponderModel<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body().getData();
                    if (userProfile != null) {
                       // txtCoin.setText(userProfile.getClamPoint() + " xu");
                        long coin = userProfile.getClamPoint();
                        txtCoin.setText(coin + " xu");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("coin", (int) coin);
                        editor.apply();
                        txtUpgradeAccount.setText(userProfile.getPaymentName());

                        String newAvatar = userProfile.getAvatar();
                        String currentAvatar = sharedPreferences.getString("avatar", "");

                        if (newAvatar != null && !newAvatar.equals(currentAvatar)) {
                          sharedPreferences.edit().putString("avatar", newAvatar).apply();
                        }

                        Glide.with(requireContext())
                                .load(userProfile.getAvatar())
                                .placeholder(R.drawable.icon_avatar)
                                .into(avatar);
                    }

                }
            }

            @Override
            public void onFailure(Call<ReponderModel<UserProfile>> call, Throwable t) {

            }
        });

    }
}
