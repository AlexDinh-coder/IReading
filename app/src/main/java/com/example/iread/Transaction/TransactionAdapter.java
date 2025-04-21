package com.example.iread.Transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.Transaction;
import com.example.iread.Model.UserTranscationBook;
import com.example.iread.Model.UserTranscationBookModel;
import com.example.iread.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<UserTranscationBookModel> transactionList;

    public TransactionAdapter(List<UserTranscationBookModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        UserTranscationBookModel transaction = transactionList.get(position);
        holder.tvTransactionId.setText(String.valueOf(transaction.getId()));
        holder.tvContent.setText(transaction.getPaymentName());
        holder.tvDate.setText(transaction.getCreateDate());


        int amount = transaction.getPrice();

        UserTranscationBookModel.PaymentNameEnum type = transaction.getPaymentNameEnum();
        if (type == UserTranscationBookModel.PaymentNameEnum.Deposit) {
            // Nạp tiền
            holder.tvAmount.setText("+" + amount + " xu");
            holder.tvAmount.setTextColor(0xFF00C853); // Màu xanh
        } else if (type == UserTranscationBookModel.PaymentNameEnum.Pay) {
            // Mặc định là Pay (trừ tiền)
            holder.tvAmount.setText("-" + amount + " xu");
            holder.tvAmount.setTextColor(0xFFFF5555); // Màu đỏ
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvContent, tvDate, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionIdInTransaction);
            tvContent = itemView.findViewById(R.id.tvContentInTransaction);
            tvDate = itemView.findViewById(R.id.tvDateInTransaction);
            tvAmount = itemView.findViewById(R.id.tvAmountInTransaction);
        }
    }
}

