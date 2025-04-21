package com.example.iread.OpenBook;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.Interface.OnSelectionChangedListener;
import com.example.iread.LibraryFragment;
import com.example.iread.MainActivity;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.ItemHolder>{
    Context context;
    private List<Book> dataBook;

    private  boolean isSelectMode = false;
    private OnSelectionChangedListener selectionListener;
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    //private final Set<Integer> selectedBookId = new HashSet<>();
    public BookDetailAdapter(Context context, List<Book> dataBook) {
        this.context = context;
        this.dataBook = dataBook;
    }
    public void setSelectMode(boolean selectMode) {
        this.isSelectMode = selectMode;
        notifyDataSetChanged();
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

        holder.checkBox.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (!isSelectMode) {
                Intent intent = new Intent(context, OpenBookActivity.class);
                intent.putExtra("bookId", item.getId());
                context.startActivity(intent);
            } else {
                boolean checked = !holder.checkBox.isChecked();
                holder.checkBox.setChecked(checked);

                // 👉 Gọi callback để thông báo có thay đổi checkbox
                if (selectionListener != null) {
                    selectionListener.onSelectionStarted();
                }
            }
        });


        holder.itemView.setOnLongClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    LibraryFragment fragment = (LibraryFragment) ((MainActivity) context)
                            .getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        fragment.showDeleteUI();
                    }
                });
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return dataBook.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView imgBook;
        private TextView txtNameBook ;
        CheckBox checkBox;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.image_characters_in_detail);
            txtNameBook = itemView.findViewById(R.id.book_title_in_detail);
            //checkBox = itemView.findViewById(R.id.checkbox_select_book);
            checkBox = new CheckBox(context);
            checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#02c18e")));
            checkBox.setVisibility(View.GONE);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(60, 60);
            params.gravity = Gravity.TOP | Gravity.END;
            params.setMargins(0, 8, 8, 0);

            ViewGroup parent = (ViewGroup) imgBook.getParent();
            if (parent instanceof FrameLayout) {
                ((FrameLayout) parent).addView(checkBox, params);
            }

        }
    }

}
