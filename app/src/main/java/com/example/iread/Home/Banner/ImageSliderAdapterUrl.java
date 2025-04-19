package com.example.iread.Home.Banner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.R;

import java.util.List;

public class ImageSliderAdapterUrl extends RecyclerView.Adapter<ImageSliderAdapterUrl.SliderViewHolder> {
    private List<String> imageUrls;
    private OnBannerClickListener listener;

    public ImageSliderAdapterUrl(List<String> imageUrls, OnBannerClickListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int realPosition = position % imageUrls.size();
        String imageUrl = imageUrls.get(position % imageUrls.size());
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(realPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}

