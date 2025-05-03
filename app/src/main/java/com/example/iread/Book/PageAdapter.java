package com.example.iread.Book;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iread.Model.BookChapter;
import com.example.iread.Model.DataPageInBook;
import com.example.iread.R;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

   // private List<BookChapter> pageList;

   private List<DataPageInBook> dataPage;
   private BookChapter bookChapter;
   private List<BookChapter> chapterList;
   private Context context;
   private String username;

    public PageAdapter(List<DataPageInBook> dataPage, Context context,String username) {
        this.dataPage = dataPage;
        this.context = context;
        this.username = username;

    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        ContentPageAdapter contentPageAdapter = new ContentPageAdapter(this.dataPage.get(position).getData(),this.dataPage.get(position).getBookChapter(),context,username);
        holder.rcv.setAdapter(contentPageAdapter);


         // Cho phép chọn văn bản

    }

    @Override
    public int getItemCount() {
        //return (pageList != null) ? pageList.size() : 0;
        return this.dataPage.size();

    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        //TextView textPage;
        RecyclerView rcv;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            rcv = itemView.findViewById(R.id.rcvPageContent);
            rcv.setLayoutManager(new LinearLayoutManager(rcv.getContext() , LinearLayoutManager.VERTICAL , false));
            rcv.setNestedScrollingEnabled(false);

        }
    }
}
