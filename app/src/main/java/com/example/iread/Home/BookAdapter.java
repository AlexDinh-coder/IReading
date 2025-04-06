package com.example.iread.Home;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.Model.Book;
import com.example.iread.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ItemHolder>{
    Context context;
    private List<Book> dataBook;

    public BookAdapter(Context context, List<Book> dataBook) {
        this.context = context;
        this.dataBook = dataBook;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_book , parent , false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Book book = dataBook.get(position);
        holder.txtNameBook.setText(dataBook.get(position).getName());
        Log.d("BookAdapter", "Bind book: " + book.getName());
        Glide.with(holder.imgBook.getContext())
                .load(dataBook.get(position).getPoster())
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_image)
                .into(holder.imgBook);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.iread.OpenBook.OpenBookActivity.class);
            intent.putExtra("bookId", book.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataBook.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView imgBook;
        private TextView txtNameBook;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.image_characters);
            txtNameBook = itemView.findViewById(R.id.book_title);
        }
    }
}
