package com.example.iread.OpenBook;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.R;

import java.util.List;

public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.ItemHolder>{
    Context context;
    private List<Book> dataBook;

    List<BookChapter> bookChapter;

    public BookDetailAdapter(Context context, List<Book> dataBook) {
        this.context = context;
        this.dataBook = dataBook;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_book_in_detail , parent , false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Book item = dataBook.get(position);
        holder.txtNameBook.setText(item.getName() +"");
        Glide.with(holder.imgBook.getContext())
                .load(dataBook.get(position).getPoster())
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_image)
                .into(holder.imgBook);
        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OpenBookActivity.class);
            intent.putExtra("bookId", item.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataBook.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView imgBook;
        private TextView txtNameBook ;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.image_characters_in_detail);
            txtNameBook = itemView.findViewById(R.id.book_title_in_detail);

        }
    }
}
