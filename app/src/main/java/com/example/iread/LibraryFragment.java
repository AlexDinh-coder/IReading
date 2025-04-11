package com.example.iread;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.LibraryCateAdapter;
import com.example.iread.Model.Book;
import com.example.iread.OpenBook.BookDetailAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerBookView;
    private BookDetailAdapter bookAdapter;
    private List<Book> bookList;

    TextView tvUserName, tvFavorite, tvContinue, tvPurchased;

    TextView viewAccount;

    private enum LibraryTab {
        FAVORITE, CONTINUE, PURCHASED
    }
    private LibraryTab currentTab = LibraryTab.FAVORITE;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library , container , false);
        makeStatusBarTransparent();
        applyTopPadding(view);
        recyclerBookView = view.findViewById(R.id.rcv_book_library);
        RecyclerView recyclerView = view.findViewById(R.id.rcv_cate_library);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvFavorite = view.findViewById(R.id.tvFavorite);
        tvContinue = view.findViewById(R.id.tvContinue);
        tvPurchased = view.findViewById(R.id.tvBuy);
        viewAccount = view.findViewById(R.id.viewAccount);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");

        tvUserName.setText(username);


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // ============================================================
        GridLayoutManager layoutManagerBook = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerBookView.setLayoutManager(layoutManagerBook);
        tvFavorite.setOnClickListener(v -> {
            currentTab = LibraryTab.FAVORITE;
            loadSampleData();
            updateTabUI();
        });

        tvContinue.setOnClickListener(v -> {
            currentTab = LibraryTab.CONTINUE;
            loadContinueBooks();
            updateTabUI();

        });

        tvPurchased.setOnClickListener(v -> {
            currentTab = LibraryTab.PURCHASED;
            loadPurchasedBooks();
            updateTabUI();
        });

        viewAccount.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.nav_user);
        });

        return view;
    }



    private void loadPurchasedBooks() {
        bookList = new ArrayList<>();
        SharedPreferences prefs = requireContext().getSharedPreferences("PurchasedBooks", Context.MODE_PRIVATE);
        Map<String, ?> all = prefs.getAll();

        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String[] parts = entry.getValue().toString().split("\\|");
            if (parts.length >= 3) {
                try {
                    int bookId = Integer.parseInt(entry.getKey());
                    Book book = new Book();
                    book.setId(bookId);
                    book.setName(parts[0]);
                    book.setCreateBy(parts[1]);
                    book.setPoster(parts[2]);
                    bookList.add(book);
                } catch (NumberFormatException e) {
                    Log.e("LibraryFragment", "Lỗi bookId không hợp lệ: " + entry.getKey());
                }
            }
        }

        bookAdapter = new BookDetailAdapter(getContext(), bookList);
        recyclerBookView.setAdapter(bookAdapter);
    }

    private void loadContinueBooks() {
        bookList = new ArrayList<>();
        SharedPreferences prefs = requireContext().getSharedPreferences("ContinueBooks", Context.MODE_PRIVATE);
        Map<String, ?> all = prefs.getAll();

        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String[] parts = entry.getValue().toString().split("\\|");
            if (parts.length >= 3) {
                try {
                    int bookId = Integer.parseInt(entry.getKey());
                    Book book = new Book();
                    book.setId(bookId);
                    book.setName(parts[0]);
                    book.setCreateBy(parts[1]);
                    book.setPoster(parts[2]);
                    bookList.add(book);
                } catch (NumberFormatException e) {
                    Log.e("LibraryFragment", "Lỗi bookId không hợp lệ: " + entry.getKey());
                }
            }
        }

        bookAdapter = new BookDetailAdapter(getContext(), bookList);
        recyclerBookView.setAdapter(bookAdapter);
    }


    private void loadSampleData() {
        // Khởi tạo danh sách để chứa các sách yêu thích
        bookList = new ArrayList<>();

        // Lấy SharedPreferences nơi lưu thông tin sách yêu thích
        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences("FavoriteBooks", Context.MODE_PRIVATE);

        // Lấy toàn bộ dữ liệu sách yêu thích dưới dạng Map (key: bookId, value: "name|author|poster")
        Map<String, ?> favoriteEntries = sharedPreferences.getAll();

        // Duyệt qua từng sách đã lưu
        for (Map.Entry<String, ?> entry : favoriteEntries.entrySet()) {
            String bookIdString = entry.getKey();                 // Lấy bookId
            String rawBookData = entry.getValue().toString();     // Lấy chuỗi dữ liệu đã lưu

            // Tách dữ liệu thành mảng: [tên sách, tác giả, poster]
            String[] bookInfoParts = rawBookData.split("\\|");

            // Kiểm tra xem dữ liệu có hợp lệ không (ít nhất 3 phần)
            if (bookInfoParts.length >= 3) {
                try {
                    int bookId = Integer.parseInt(bookIdString);
                    String bookName = bookInfoParts[0];
                    String authorName = bookInfoParts[1];
                    String posterUrl = bookInfoParts[2];

                    // Tạo đối tượng Book và gán dữ liệu
                    Book book = new Book();
                    book.setId(bookId);
                    book.setName(bookName);
                    book.setCreateBy(authorName);
                    book.setPoster(posterUrl);

                    // Thêm vào danh sách hiển thị
                    bookList.add(book);

                } catch (NumberFormatException e) {
                    Log.e("LibraryFragment", "Lỗi chuyển đổi bookId sang số nguyên: " + bookIdString);
                }
            }
        }

        // Gắn dữ liệu vào adapter và hiển thị lên RecyclerView
        bookAdapter = new BookDetailAdapter(getContext(), bookList);
        recyclerBookView.setAdapter(bookAdapter);

    }
    // Gọi lại load danh sách mỗi lần quay lại màn
    @Override
    public void onResume() {
        super.onResume();
        switch (currentTab) {
            case FAVORITE:
                loadSampleData();
                break;
            case CONTINUE:
                loadContinueBooks();
                break;
            case PURCHASED:
                loadPurchasedBooks();
                break;
        }
        updateTabUI();
    }
    private void updateTabUI() {
        tvFavorite.setTextColor(currentTab == LibraryTab.FAVORITE ? Color.WHITE : Color.parseColor("#8C8A8A"));
        tvContinue.setTextColor(currentTab == LibraryTab.CONTINUE ? Color.WHITE : Color.parseColor("#8C8A8A"));
        tvPurchased.setTextColor(currentTab == LibraryTab.PURCHASED ? Color.WHITE : Color.parseColor("#8C8A8A"));
    }
    private void makeStatusBarTransparent() {
        Window window = requireActivity().getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }


    private void applyTopPadding(View view) {
        View contentContainer = view.findViewById(R.id.fragment_library);

        if (contentContainer != null) {
            int statusBarHeight = getStatusBarHeight();
            contentContainer.setPadding(0, statusBarHeight, 0, 0);
        }
    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
