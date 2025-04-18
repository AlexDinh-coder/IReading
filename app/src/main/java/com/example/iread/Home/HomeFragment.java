// HomeFragment.java
package com.example.iread.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
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

import com.example.iread.DetailActivity;
import com.example.iread.Home.Banner.CarouselTransformer;
import com.example.iread.Home.Banner.ImageSliderAdapter;
import com.example.iread.MenuBarInHome.CategoryActivity;
import com.example.iread.MenuBarInHome.SearchActivity;
import com.example.iread.Model.Book;
import com.example.iread.Model.Category;
import com.example.iread.SubscriptionActivity;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.example.iread.R;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private IAppApiCaller apiCaller;
    private CategoryAdapter categoryAdapter;
    private final List<Category> dataList = new ArrayList<>();
    private final Map<Integer, View> categorySectionMap = new LinkedHashMap<>();

    private TextView btnPay;

    private ImageView imgBar, btnSearch;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout contentScrollLayout;
    private ScrollView scrollView;
    private ViewPager2 viewPager2;
    private View backgroundView;
    private final Handler sliderHandler = new Handler();

    // Inflate fragment layout and setup UI
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupViews(view);
        setupSwipeToRefresh();
        setupCategoryRecycler(view);
        setupBannerSlider(view);

        // Initial data fetch
        getListCategory();
        getListBookByCategory();

        return view;
    }

    // Setup references to views and layout padding
    private void setupViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        scrollView = view.findViewById(R.id.scrollView2);
        contentScrollLayout = view.findViewById(R.id.content_scroll);
        backgroundView = view.findViewById(R.id.background_view);

        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        btnPay = view.findViewById(R.id.btnPlan);
        btnPay.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), SubscriptionActivity.class);
                startActivity(intent);
            }
        });
        // bar
        imgBar = view.findViewById(R.id.imgBarInHome);
        imgBar.setOnClickListener(v->{
            if(requireContext() != null){
                Intent intent = new Intent(requireContext() , CategoryActivity.class);
                startActivity(intent);
            }
        });

        View contentContainer = view.findViewById(R.id.content_container);
        contentContainer.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    // Setup pull-to-refresh logic
    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            dataList.clear();
            categoryAdapter.notifyDataSetChanged();
            clearOldBookSections();
            getListCategory();
            getListBookByCategory();
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });
    }

    // Setup horizontal category list
    private void setupCategoryRecycler(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rcv_main_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(dataList);
        recyclerView.setAdapter(categoryAdapter);

        // Click: Scroll to section or open DetailActivity
        categoryAdapter.setClickListener(category -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("selectedCategory", category.getName());
                startActivity(intent);
            }
        });
    }

    // Setup banner image carousel
    private void setupBannerSlider(View view) {
        viewPager2 = view.findViewById(R.id.viewPager);
        List<Integer> imageList = Arrays.asList(R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.book5);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
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
    }

    // Clear old book sections when refreshing
    private void clearOldBookSections() {
        int childCount = contentScrollLayout.getChildCount();
        for (int i = childCount - 1; i >= 2; i--) {
            contentScrollLayout.removeViewAt(i);
        }
    }

    // Fetch and display categories
    private void getListCategory() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        apiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dataList.clear();
                    dataList.addAll(response.body().getDataList());
                    Collections.sort(dataList, Comparator.comparingInt(Category::getId));
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.d("API Response", "Thất bại khi gọi API: " + t.getMessage());
            }
        });
    }

    // Fetch book list for each category and add sections dynamically
    private void getListBookByCategory() {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, requireContext()).create(IAppApiCaller.class);
        apiCaller.getCategories().enqueue(new Callback<ReponderModel<Category>>() {
            @Override
            public void onResponse(Call<ReponderModel<Category>> call, Response<ReponderModel<Category>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getDataList();
                    Collections.sort(categories, Comparator.comparingInt(Category::getId));
                    for (Category category : categories) {
                        addCategorySection(category);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Category>> call, Throwable t) {
                Log.e("API Category", "Lỗi khi gọi API category: " + t.getMessage());
            }
        });
    }

    // Add UI section for a single category with books
    private void addCategorySection(Category category) {
        if (!isAdded()) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View sectionView = inflater.inflate(R.layout.item_category_section_xml, contentScrollLayout, false);

        TextView tvCategoryTitle = sectionView.findViewById(R.id.tv_category_title);
        RecyclerView rcvBooks = sectionView.findViewById(R.id.rcv_books_by_category);

        tvCategoryTitle.setText(category.getName());
        tvCategoryTitle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("selectedCategory", category.getName());
            startActivity(intent);
        });

        rcvBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        apiCaller.getBookByCategory(category.getName()).enqueue(new Callback<ReponderModel<Book>>() {
            @Override
            public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getDataList();
                    Map<Integer, Book> uniqueBooksMap = new LinkedHashMap<>();
                    for (Book book : books) {
                        uniqueBooksMap.put(book.getId(), book);
                    }
                    List<Book> uniqueBooks = new ArrayList<>(uniqueBooksMap.values());
                    if (!uniqueBooks.isEmpty()) {
                        if (uniqueBooks.size() > 10) uniqueBooks = uniqueBooks.subList(0, 10);
                        BookAdapter adapter = new BookAdapter(requireContext(), uniqueBooks);
                        rcvBooks.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {
                Log.e("BookAPI", "Lỗi gọi sách: " + t.getMessage());
            }
        });

        contentScrollLayout.addView(sectionView);
        categorySectionMap.put(category.getId(), sectionView);
    }

    // Banner auto-scroll runnable
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

    // Helper: Get status bar height
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Update background color based on banner image
    private void updateBackgroundColor(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        Palette.from(bitmap).generate(palette -> {
            if (!isAdded() || palette == null) return;
            Context context = getContext();
            if (context == null) return;

            int defaultColor = ContextCompat.getColor(context, android.R.color.black);
            int dominantColor = palette.getDominantColor(defaultColor);

            int startColor = addAlpha(dominantColor, 200);
            int endColor = Color.parseColor("#101318");

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{startColor, endColor}
            );
            gradientDrawable.setCornerRadius(0f);
            backgroundView.setBackground(gradientDrawable);
        });
    }

    // Helper: Add alpha to color
    private int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    // Optional: Get average color from image (not used)
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
