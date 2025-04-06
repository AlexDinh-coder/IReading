package com.example.iread.Home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iread.Home.Banner.CarouselTransformer;
import com.example.iread.Home.Banner.ImageSliderAdapter;
import com.example.iread.Model.Book;
import com.example.iread.Model.Category;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.example.iread.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private IAppApiCaller apiCaller;

    private final Map<Integer, View> categorySectionMap = new LinkedHashMap<>();

    private LinearLayout bookSectionContainer;

    private ScrollView scrollView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private CategoryAdapter categoryAdapter;
    private List<Category> dataList = new ArrayList<>();
    private ViewPager2 viewPager2;
    private View backgroundView;
    private final Handler sliderHandler = new Handler();
    private LinearLayout contentScrollLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        //reload dữ liệu mới từ api
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //Hiện loading xoay
            swipeRefreshLayout.setRefreshing(true);
            //xoa du lieu cu
            dataList.clear();
            categoryAdapter.notifyDataSetChanged();
            clearOldBookSections();

            //gọi lại API
            getListCategory();
            getListBookByCategory();
            //Loading xong sau 2s
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });

        bookSectionContainer = view.findViewById(R.id.book_section_container);

        scrollView = view.findViewById(R.id.scrollView2);
        contentScrollLayout = view.findViewById(R.id.content_scroll);

        RecyclerView recyclerView = view.findViewById(R.id.rcv_main_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryAdapter = new CategoryAdapter(dataList);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(category -> {
            View sectionView = categorySectionMap.get(category.getId());
            if (sectionView != null) {
                scrollView.post(() -> {
                    int y = sectionView.getTop(); // vị trí bắt đầu của section trong ScrollView
                    scrollView.smoothScrollTo(0, y);
                });
            }
        });


        View contentContainer = view.findViewById(R.id.content_container);
        int statusBarHeight = getStatusBarHeight();
        contentContainer.setPadding(0, statusBarHeight, 0, 0);

        viewPager2 = view.findViewById(R.id.viewPager);
        backgroundView = view.findViewById(R.id.background_view);

        getListCategory();
        getListBookByCategory();

        List<Integer> imageList = Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3
        );

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        int pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        int pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        viewPager2.setPageTransformer((page, position) -> {
            float offset = position * -(2 * pageOffset + pageMargin);
            if (viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setTranslationX(offset);
            } else {
                page.setTranslationY(offset);
            }
        });

        viewPager2.setPageTransformer(new CarouselTransformer());
        viewPager2.setAdapter(new ImageSliderAdapter(imageList));
        viewPager2.setCurrentItem(Integer.MAX_VALUE / 2);

        updateBackgroundColor(imageList.get(viewPager2.getCurrentItem() % imageList.size()));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
                updateBackgroundColor(imageList.get(position % imageList.size()));
            }
        });

        return view;
    }

    private void clearOldBookSections() {
        int childCount = contentScrollLayout.getChildCount();
        for (int i = childCount - 1; i >= 2; i--) {
            contentScrollLayout.removeViewAt(i);
        }
    }

    private void getListBookByCategory() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        apiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getDataList();
                    //sap xep category theo id
                    Collections.sort(categories, (c1, c2) -> Integer.compare(c1.getId(), c2.getId()));


                    for (Category category : categories) {
                        addCategorySelection(category);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.e("API Category", "Lỗi khi gọi API category: " + t.getMessage());
            }
        });

    }

    private void addCategorySelection(Category category) {

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View sectionView = inflater.inflate(R.layout.item_category_section_xml, contentScrollLayout, false);

        TextView tvCategoryTitle = sectionView.findViewById(R.id.tv_category_title);
        RecyclerView rcvBooks = sectionView.findViewById(R.id.rcv_books_by_category);

        categorySectionMap.put(category.getId(), sectionView); // Gán view cho category ID

        tvCategoryTitle.setText(category.getName());
        rcvBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        apiCaller.getBookByCategory(String.valueOf(category.getName()))
                .enqueue(new Callback<ReponderModel<Book>>() {
                    @Override
                    public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Book> books = response.body().getDataList();

                            // Lọc trùng sách theo ID
                            Map<Integer, Book> uniqueBooksMap = new LinkedHashMap<>();
                            for (Book book : books) {
                                uniqueBooksMap.put(book.getId(), book);
                            }

                            List<Book> uniqueBooks = new ArrayList<>(uniqueBooksMap.values());

                            Log.d("BookAPI", "Category: " + category.getName() + " | Số sách sau khi lọc: " + uniqueBooks.size());

                            if (!uniqueBooks.isEmpty()) {
                                if (uniqueBooks.size() > 10) {
                                    uniqueBooks = uniqueBooks.subList(0, 10);
                                }
                                BookAdapter adapter = new BookAdapter(requireContext(), uniqueBooks);
                                rcvBooks.setAdapter(adapter);
                            } else {
                                Log.d("BookAPI", "Không có sách cho thể loại: " + category.getName());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                        Log.e("BookAPI", "Lỗi gọi sách: " + t.getMessage());
                    }
                });
        contentScrollLayout.addView(sectionView);

    }


    private void getListCategory() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        apiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dataList.clear();
                    dataList.addAll(response.body().getDataList());
                    //Sap xep thu tu category theo id
                    Collections.sort(dataList, (c1, c2) -> Integer.compare(c1.getId(), c2.getId()));
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.d("API Response", "Thất bại khi gọi API: " + t.getMessage());
            }
        });
    }

    private final Runnable sliderRunnable = () -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void updateBackgroundColor(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int defaultColor = ContextCompat.getColor(requireContext(), android.R.color.black);
                int dominantColor = palette.getDominantColor(defaultColor);

                int startColor = addAlpha(dominantColor, 200);
                int endColor = Color.parseColor("#101318");

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{startColor, endColor}
                );
                gradientDrawable.setCornerRadius(0f);
                backgroundView.setBackground(gradientDrawable);
            }
        });
    }

    private int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private int getAverageColor(Bitmap bitmap) {
        long redBucket = 0, greenBucket = 0, blueBucket = 0, pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y += 10) {
            for (int x = 0; x < bitmap.getWidth(); x += 10) {
                int c = bitmap.getPixel(x, y);
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                pixelCount++;
            }
        }

        return Color.rgb((int) (redBucket / pixelCount),
                (int) (greenBucket / pixelCount),
                (int) (blueBucket / pixelCount));
    }
}
