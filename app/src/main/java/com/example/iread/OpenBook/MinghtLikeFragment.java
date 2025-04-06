package com.example.iread.OpenBook;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.Book;
import com.example.iread.Model.Category;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinghtLikeFragment extends Fragment {
    IAppApiCaller iAppApiCaller;
    private List<Book> recommendedBook = new ArrayList<>();
    private RecyclerView rcvBookDetail;
    private BookDetailAdapter bookDetailAdapter;

    private Set<Integer> addBookIds = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_might_like, container, false);
        rcvBookDetail = view.findViewById(R.id.rcv_book_in_open);
        rcvBookDetail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));





        Bundle bundle = getArguments();
        if (bundle != null) {
            int bookId = bundle.getInt("bookId", -1);

            iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);

            iAppApiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
                @Override
                public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Book currentBook = response.body().getData();

                        if (currentBook != null && currentBook.getListCategories() != null && !currentBook.getListCategories().isEmpty()) {
                            fetchBooksByCategory(bookId, currentBook.getListCategories());
                        } else {
                            Log.e("MinghtLikeFragment", "Không có thể loại cho sách hiện tại");
                        }
                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                    Log.e("MinghtLikeFragment", "Lỗi gọi API getBookById: " + t.getMessage());
                }
            });
        }
        

        return view;
    }



    private void fetchBooksByCategory(int bookId, List<Category> categoryNames) {
        recommendedBook.clear(); // xóa cũ trước
        addBookIds.clear(); //
        //Set adapter mot lần
        bookDetailAdapter = new BookDetailAdapter(requireContext(), recommendedBook);
        rcvBookDetail.setAdapter(bookDetailAdapter);
        for (Category category : categoryNames) {
            String categoryName = category.getName();
            iAppApiCaller.getBookByCategory(categoryName).enqueue(new Callback<ReponderModel<Book>>() {
                @Override
                public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Book> booksFromCategory = response.body().getDataList();

                        for (Book book : booksFromCategory) {
                            int id = book.getId();
                            // Không trùng sách hiện tại và không trùng trong danh sách
                            if (id != bookId && !addBookIds.contains(id)) {
                                addBookIds.add(id);
                                recommendedBook.add(book);
                            }
                        }
                        // Cập nhật lại adapter
                        bookDetailAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                    Log.e("MinghtLikeFragment", "Lỗi gọi API getBookByCategory: " + t.getMessage());
                }
            });
        }
    }

}
