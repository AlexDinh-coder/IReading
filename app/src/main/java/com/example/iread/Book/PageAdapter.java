package com.example.iread.Book;


import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.BookChapter;
import com.example.iread.R;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private List<BookChapter> pageList;

    public PageAdapter(List<BookChapter> pageList) {
        this.pageList = pageList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        BookChapter bookChapter = pageList.get(position);
        if (bookChapter.getContent() != null)
            holder.textPage.setText(Html.fromHtml(bookChapter.getContent(), Html.FROM_HTML_MODE_LEGACY));
        else {
            holder.textPage.setText("Nội dung đang cập nhật...");
        }
    }

    @Override
    public int getItemCount() {
//       return pageList.size();
        return (pageList != null) ? pageList.size() : 0;

    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        TextView textPage;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            textPage = itemView.findViewById(R.id.textPage);
        }
    }
}
