package com.example.iread.MenuBarInHome;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Interface.CategoryClickListener;
import com.example.iread.Model.Category;
import com.example.iread.R;

import java.util.List;

public class CateAdapter extends RecyclerView.Adapter<CateAdapter.CategoryViewHolder>{

    private List<Category> categories;
    private int selectedPosition = 0;

    private CategoryClickListener listener;


    public CateAdapter(List<Category> categories, CategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cate_bar, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategory.setText(category.getName());

        if (selectedPosition == position) {
            holder.tvCategory.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_selected);
//            holder.tvCategory.setTextColor(Color.BLACK);
        } else {
            holder.tvCategory.setTextColor(Color.parseColor("#AAAAAA"));
//            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_unselected);
//            holder.tvCategory.setTextColor(Color.WHITE);
        }

        holder.tvCategory.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(category); // dòng này để callback
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategoryInBar);
        }
    }
}
