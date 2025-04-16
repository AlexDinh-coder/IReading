package com.example.iread.Payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.iread.Model.PaymentRequestModel;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.PaymentItem;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentChooseActivity extends AppCompatActivity {
    private ImageView btnClose;

    private TextView textMessage;

    private IAppApiCaller iAppApiCaller;

    private AppCompatButton btnItem1, btnItem2, btnItem3, btnItem4;

    String username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_choose);
        makeStatusBarTransparent();
        applyTopPadding();

        textMessage = findViewById(R.id.welcomeText);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        textMessage.setText("Xin chào " + username + ", vui lòng chọn gói thanh toán mà bạn muốn mua");

        btnItem1 = findViewById(R.id.btnItem1);
        btnItem2 = findViewById(R.id.btnItem2);
        btnItem3 = findViewById(R.id.btnItem3);
        btnItem4 = findViewById(R.id.btnItem4);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            finish();
        });

        loadPaymentItems();

    }


    private void loadPaymentItems() {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.getListPayment(0).enqueue(new Callback<ReponderModel<PaymentItemModel>>() {
            @Override
            public void onResponse(Call<ReponderModel<PaymentItemModel>> call, Response<ReponderModel<PaymentItemModel>> response) {
               if (response.isSuccessful() && response.body() != null){
                   List<PaymentItemModel> itemModels = response.body().getDataList();

                   if(itemModels.size() >=4) {
                       setButtonWithItem(btnItem1, itemModels.get(0));
                       setButtonWithItem(btnItem2, itemModels.get(1));
                       setButtonWithItem(btnItem3, itemModels.get(2));
                       setButtonWithItem(btnItem4, itemModels.get(3));

                   }
               }
            }

            @Override
            public void onFailure(Call<ReponderModel<PaymentItemModel>> call, Throwable t) {
                Log.e("API", "Lỗi gọi API thanh toán: " + t.getMessage());
            }
        });
    }

    private void setButtonWithItem(AppCompatButton button, PaymentItemModel item) {
        button.setText(formatAmount(item.getAmountMoney()) + " 🐤");

        button.setOnClickListener(v -> {
            PaymentRequestModel request = new PaymentRequestModel();
            request.setAmount(item.getAmountMoney());
            request.setDescription(item.getPaymentName());
            request.setDomain("");
            request.setBuyerEmail(username);
            request.setType(0);
            request.setPaymentKey(String.valueOf(item.getId()));

            // tạo item con (bên trong list "items")
            PaymentItem paymentItem = new PaymentItem();
            paymentItem.setName(item.getPaymentName());
            paymentItem.setPrice(item.getAmountMoney());
            paymentItem.setQuantity(1);

            request.getItems().add(paymentItem);

            sendPaymentRequest(request);
        });
    }

    private String formatAmount(int amount) {
        return String.format("%,d", amount).replace(",", ".");
    }


    private void sendPaymentRequest(PaymentRequestModel requestModel) {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.createPaymentLink(requestModel).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentLink = response.body().getData();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink));
                    startActivity(browserIntent);
                } else {
                    Log.e("API", "Lỗi gọi API thanh toán: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {

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
        View contentContainer = findViewById(R.id.choose_pay);

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