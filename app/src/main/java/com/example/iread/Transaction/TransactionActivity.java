package com.example.iread.Transaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.Transaction;
import com.example.iread.Model.UserTranscationBookModel;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends AppCompatActivity {
    private RecyclerView rcvTransactions;
    private IAppApiCaller iAppApiCaller;
    private TransactionAdapter transactionAdapter;
    private ImageView btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);
        rcvTransactions = findViewById(R.id.rcvTransactions);
        rcvTransactions.setLayoutManager(new LinearLayoutManager(this));
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        loadTransactionHistory(username);


    }

    private void loadTransactionHistory(String username) {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.getHistoryPaymentItem(username).enqueue(new Callback<ReponderModel<UserTranscationBookModel>>() {
            @Override
            public void onResponse(Call<ReponderModel<UserTranscationBookModel>> call, Response<ReponderModel<UserTranscationBookModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserTranscationBookModel> transactionBook = response.body().getDataList();

                    // Sắp xếp theo ngày giảm dần
                    Collections.sort(transactionBook, new Comparator<UserTranscationBookModel>() {
                        @Override
                        public int compare(UserTranscationBookModel o1, UserTranscationBookModel o2) {
                            try {
                                // Giả định format ngày là: "HH:mm dd-MM-yyyy"
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                                Date d1 = sdf.parse(o1.getCreateDate());
                                Date d2 = sdf.parse(o2.getCreateDate());
                                return d2.compareTo(d1); // giảm dần
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                    });

                    transactionAdapter = new TransactionAdapter(transactionBook);
                    rcvTransactions.setAdapter(transactionAdapter);
                } else {
                    Toast.makeText(TransactionActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<UserTranscationBookModel>> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}