package com.example.iread.OpenBook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iread.Model.CommentModel;
import com.example.iread.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemHolder> {
    private List<CommentModel> commentList;

    public ReviewAdapter(List<CommentModel> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_review , parent  , false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        CommentModel comment = commentList.get(position);
        if (comment == null) return;
        Log.d("ReviewAdapter", "Đang bind comment: " + comment.getFullName());

        holder.txtUsername.setText(comment.getFullName());
        holder.txtReview.setText(comment.getContent());
        Log.d("ReviewAdapter", "Nội dung comment: " + comment.getContent());

        holder.txtTime.setText(comment.getCreateDate());
        // Hiển thị số sao tương ứng với rating
        for (int i = 0; i < 5; i++) {
            if (i < comment.getRating()) {
                holder.stars[i].setImageResource(R.drawable.ic_star); // sao đầy
            } else {
                holder.stars[i].setImageResource(R.drawable.ic_star_border); // sao rỗng
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.commentList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername;
        private TextView txtReview, txtTime;

        ImageView[] stars = new ImageView[5];

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.username_in_open_book);
            txtReview = itemView.findViewById(R.id.review_in_open_book);
            txtTime = itemView.findViewById(R.id.time_text);
            stars[0] = itemView.findViewById(R.id.star1);
            stars[1] = itemView.findViewById(R.id.star2);
            stars[2] = itemView.findViewById(R.id.star3);
            stars[3] = itemView.findViewById(R.id.star4);
            stars[4] = itemView.findViewById(R.id.star5);

        }
    }
    public void updateData(List<CommentModel> newData) {
        this.commentList.clear();
        this.commentList.addAll(newData);
        notifyDataSetChanged();
    }

}
