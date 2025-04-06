package com.example.iread.Comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.CommentModel;
import com.example.iread.Model.Review;
import com.example.iread.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<CommentModel> reviewList;

    public ReviewAdapter(List<CommentModel> reviewList) {
        this.reviewList = reviewList;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtReview, txtTime;
        ImageView[] stars = new ImageView[5];

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtName);
            txtReview = itemView.findViewById(R.id.txtComment);
            txtTime = itemView.findViewById(R.id.txtTime);
            stars[0] = itemView.findViewById(R.id.star111);
            stars[1] = itemView.findViewById(R.id.star112);
            stars[2] = itemView.findViewById(R.id.star113);
            stars[3] = itemView.findViewById(R.id.star114);
            stars[4] = itemView.findViewById(R.id.star115);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_list, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        CommentModel listComment = reviewList.get(position);
        if (listComment == null) return;
        Log.d("ReviewAdapter", "Đang bind comment: " + listComment.getFullName());

        holder.txtUsername.setText(listComment.getFullName());
        holder.txtReview.setText(listComment.getContent());
        holder.txtTime.setText(listComment.getCreateDate());
        // Hiển thị số sao tương ứng với rating
        for (int i = 0; i < 5; i++) {
            if (i < listComment.getRating()) {
                holder.stars[i].setImageResource(R.drawable.ic_star); // sao đầy
            } else {
                holder.stars[i].setImageResource(R.drawable.ic_star_border); // sao rỗng
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateData(List<CommentModel> newData) {
        this.reviewList.clear();
        this.reviewList.addAll(newData);
        notifyDataSetChanged();
    }
}
