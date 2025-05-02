package com.example.iread;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.iread.Model.UserMinuteModel;
import com.example.iread.Model.UserProfile;
import com.example.iread.Payment.PaymentActivity;
import com.example.iread.Transaction.TransactionActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private AppCompatButton upgradeAccount;
    private TextView btnLogOut, btnPayment, txtCoin, btnTransactionHistory;
    private TextView username, txtUpgradeAccount, txtAccountInfo, txtViewMinuteRead, txtViewMinuteListen;

    private ImageView avatar;

    private IAppApiCaller iAppApiCaller;

    private BarChart barChart;

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
        txtViewMinuteRead = view.findViewById(R.id.viewReadMinute);
        txtViewMinuteListen = view.findViewById(R.id.viewListenMinute);
        txtAccountInfo = view.findViewById(R.id.viewAccountInfo);
        barChart = view.findViewById(R.id.barChart);
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


        //  Nếu chưa đăng nhập, chuyển sang LoginOpenActivity
        if (token.isEmpty()) {
            Intent intent = new Intent(getContext(), LoginOpenActivity.class);
            startActivity(intent);
           requireActivity().finish(); // tránh quay lại màn cũ bằng back
            return view;
        }


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
        String username = sharedPreferences.getString("username", "");

        // Gọi API để load thông tin khác (coin, gói)
        if (!username.isEmpty()) {
            loadUserProfile();

            loadUserMinutesChart(username);
        }
    }

    private void loadUserMinutesChart(String username) {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        iAppApiCaller.getUserMinute(username).enqueue(new Callback<ReponderModel<UserMinuteModel>>() {
           @Override
           public void onResponse(Call<ReponderModel<UserMinuteModel>> call, Response<ReponderModel<UserMinuteModel>> response) {
               if (response.isSuccessful() && response.body() != null) {
                   List<UserMinuteModel> userMinuteModels = response.body().getDataList();

                   if (userMinuteModels != null && !userMinuteModels.isEmpty()) {
                       Log.d("DEBUG_CHART_DATA", "Size: " + userMinuteModels.size());
                       displayBarChart(userMinuteModels);
                       int todayRead = 0;
                       int todayListen = 0;

                       for(UserMinuteModel model : userMinuteModels) {
                           if (model.isToday()) {
                               todayRead = model.getReadMinute();
                               todayListen = model.getListenMinute();
                               break;
                           }
                       }

                       txtViewMinuteRead.setText(String.valueOf(todayRead));
                       txtViewMinuteListen.setText(String.valueOf(todayListen));
                   } else {
                       Log.e("DEBUG_CHART_DATA", "DataList is null or empty");
                       barChart.clear();
                       barChart.setNoDataText("Không có dữ liệu để hiển thị");
                       barChart.invalidate();
                   }

               } else {
                   Log.e("DEBUG_CHART_ERROR", "Response body null hoặc không thành công.");
                   barChart.clear();
                   barChart.setNoDataText("Không có dữ liệu");
                   barChart.invalidate();
               }
           }

           @Override
           public void onFailure(Call<ReponderModel<UserMinuteModel>> call, Throwable t) {

           }
       });

    }

    private void displayBarChart(List<UserMinuteModel> data) {
        List<BarEntry> readEntries = new ArrayList<>();
        List<BarEntry> listenEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> readColors = new ArrayList<>();
        List<Integer> listenColors = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            UserMinuteModel model = data.get(i);
            readEntries.add(new BarEntry(i, model.getReadMinute()));
            listenEntries.add(new BarEntry(i, model.getListenMinute()));

            String dayRaw = model.getDay(); // "2025-05-01T00:00:00Z"
            // Cắt thành dd/MM
            String formattedDate = dayRaw.substring(8, 10) + "/" + dayRaw.substring(5, 7);
            labels.add(model.getDayOfWeekStr() + " (" + formattedDate + ")");

            if (model.isToday()) {
                readColors.add(Color.parseColor("#007BFF"));     // hôm nay - xanh đậm
                listenColors.add(Color.parseColor("#FF5722"));   // hôm nay - cam đậm
            } else {
                readColors.add(Color.parseColor("#00FFAA"));     // đọc - xanh
                listenColors.add(Color.parseColor("#FFD700"));   // nghe - vàng
            }
        }

        BarDataSet readSet = new BarDataSet(readEntries, "Đọc");
        BarDataSet listenSet = new BarDataSet(listenEntries, "Nghe");

        readSet.setColor(Color.parseColor("#00FFAA")); // màu đọc (xanh)
        listenSet.setColor(Color.parseColor("#FFD700")); // màu nghe (vàng)

        readSet.setDrawValues(true);
        listenSet.setDrawValues(true);
        // Ẩn giá trị nếu = 0
        readSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                float value = barEntry.getY();
                return value == 0 ? "" : String.format("%.2f", value);
            }
        });

        listenSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                float value = barEntry.getY();
                return value == 0 ? "" : String.format("%.2f", value);
            }
        });

        readSet.setValueTextColor(Color.WHITE);
        listenSet.setValueTextColor(Color.WHITE);
        readSet.setValueTextSize(12f);
        listenSet.setValueTextSize(12f);
        //Tăng groupSpace tạo khoảng cách giữa các nhóm
        float groupSpace = 0.5f;
        float barSpace = 0.05f;
        float barWidth = 0.2f;// giảm chiều rộng

        BarData barData = new BarData(readSet, listenSet);
        barData.setBarWidth(barWidth);
        barChart.setData(barData);
        float groupWidth = barData.getGroupWidth(groupSpace, barSpace);
        barChart.getXAxis().setAxisMinimum(-0.5f); // Đẩy ngược lại để căn giữa đúng nhãn đầu tiên
        barChart.getXAxis().setAxisMaximum(data.size() - 0.5f + groupWidth); // Đảm bảo đủ width cho nhóm cuối

        barChart.getXAxis().setCenterAxisLabels(true);
        // Nhóm các cột
        barChart.groupBars(0f, groupSpace, barSpace);

        // Trục X
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getXAxis().setTextSize(10f);//phông
        barChart.getXAxis().setLabelRotationAngle(30f);// nghiêng nhãn
        barChart.setExtraBottomOffset(50f);//thêm khoảng cách
        barChart.getXAxis().setLabelCount(labels.size());


        // Trục Y
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);

        // Chú giải
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.SQUARE);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);
        barChart.setExtraBottomOffset(30f); // có thêm không gian hiển thị label

        barChart.invalidate();
    }


    private void loadUserProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usernameStr = sharedPreferences.getString("username", "");
        Log.d("DEBUG_CHART_USERNAME", "Username gửi lên API: " + usernameStr);


        if (usernameStr.isEmpty()) return;

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        iAppApiCaller.getUserProfile(usernameStr).enqueue(new Callback<ReponderModel<UserProfile>>() {
            @Override
            public void onResponse(Call<ReponderModel<UserProfile>> call, Response<ReponderModel<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body().getData();
                    if (userProfile != null) {
                        long coin = userProfile.getClamPoint();
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        txtCoin.setText(formatter.format(coin) + " xu");
                        txtUpgradeAccount.setText(userProfile.getPaymentName());
                        username.setText(userProfile.getFullName());
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
