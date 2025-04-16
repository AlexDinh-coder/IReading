package com.example.iread;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iread.Model.PaymentRequestModel;
import com.example.iread.Payment.PaymentItemModel;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.PaymentItem;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {
    private TextView tvNotice, txtName1, txtName2, txtName3;

    TextView txtPrice1, txtPrice2, txtPrice3, txtDescription1, txtDescription2, txtDescription3;

    RelativeLayout btnBuy1, btnBuy2, btnBuy3;
    private IAppApiCaller iAppApiCaller;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        initViews();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        tvNotice = findViewById(R.id.tvNotice);
        tvNotice.setText("• Lưu ý: Thanh toán trực tiếp khi chưa Đăng nhập.\\n\"" +
                "        \"Bạn sẽ chỉ được sử dụng gói Hội Viên trên thiết bị này. Nếu đã có tài khoản, vui lòng đăng nhập.\\n\\n\" +\n" +
                "        \"• Các gói Hội viên đã bao gồm phí kênh thanh toán, chi tiết tại điều khoản sử dụng dịch vụ.\\n\\n\" +\n" +
                "        \"• Thanh toán sẽ được tính cho QR theo tài khoản của bạn khi xác thực mua hàng.");

        getMembershipPackage();
    }


    private void initViews() {
        txtName1 = findViewById(R.id.title_12m);
        txtName2 = findViewById(R.id.title_6m);
        txtName3 = findViewById(R.id.title_3m);
        txtDescription1 = findViewById(R.id.textDescription1);
        txtDescription2 = findViewById(R.id.textDescription2);
        txtDescription3 = findViewById(R.id.textDescription3);
        txtPrice1 = findViewById(R.id.textPrice1);
        txtPrice2 = findViewById(R.id.textPrice2);
        txtPrice3 = findViewById(R.id.textPrice3);
        btnBuy1 = findViewById(R.id.buy_12m);
        btnBuy2 = findViewById(R.id.buy_6m);
        btnBuy3 = findViewById(R.id.buy_3m);
    }

    private void getMembershipPackage() {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.getListPayment(1).enqueue(new Callback<ReponderModel<PaymentItemModel>>() {
            @Override
            public void onResponse(Call<ReponderModel<PaymentItemModel>> call, Response<ReponderModel<PaymentItemModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PaymentItemModel> paymentItemModels = response.body().getDataList();
                    if (paymentItemModels.size() >= 3) {
                        PaymentItemModel item1 = paymentItemModels.get(0);
                        PaymentItemModel item2 = paymentItemModels.get(1);
                        PaymentItemModel item3 = paymentItemModels.get(2);

                        txtName1.setText(item1.getPaymentName());
                        txtDescription1.setText(item1.getDescription().replace("\\n", "\n"));
                        txtPrice1.setText(item1.getAmountMoney() + " VNĐ");

                        txtName2.setText(item2.getPaymentName());
                        txtDescription2.setText(item2.getDescription().replace("\\n", "\n"));
                        txtPrice2.setText(item2.getAmountMoney() + " VNĐ");

                        txtName3.setText(item3.getPaymentName());
                        txtDescription3.setText(item3.getDescription().replace("\\n", "\n"));
                        txtPrice3.setText(item3.getAmountMoney() + " VNĐ");

                        btnBuy1.setOnClickListener(v -> createPayment(item1));
                        btnBuy2.setOnClickListener(v -> createPayment(item2));
                        btnBuy3.setOnClickListener(v -> createPayment(item3));


                    }

                }

            }

            @Override
            public void onFailure(Call<ReponderModel<PaymentItemModel>> call, Throwable t) {
                Log.e("API_FAILURE", "Lỗi gọi API: " + t.getMessage());
            }
        });
    }

    private void createPayment(PaymentItemModel item) {
        PaymentRequestModel request = new PaymentRequestModel();
        request.setAmount(item.getAmountMoney());
        request.setDescription(item.getPaymentName());
        request.setDomain("");
        request.setBuyerEmail(username);
        request.setType(1);
        request.setPaymentKey(String.valueOf(item.getId()));

        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setName(item.getPaymentName());
        paymentItem.setPrice(item.getAmountMoney());
        paymentItem.setQuantity(1);


        request.getItems().add(paymentItem);

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.createPaymentLink(request).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentLink = response.body().getData();

                    if (paymentLink != null && !paymentLink.isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink)));
                    } else {
                        Log.e("PAYMENT", "Link thanh toán không tồn tại!");
                    }

                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {

            }
        });
    }
}