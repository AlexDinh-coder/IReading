package com.example.iread.Audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.bumptech.glide.Glide;
import com.example.iread.Comment.ReviewAdapter;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.SummaryTime;
import com.example.iread.OpenBook.OpenBookActivity;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


public class AudioActivity extends AppCompatActivity {
    BottomSheetBehavior bottomSheetBehavior;
    OkHttpClient client;
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    ImageButton imgBtnPlay,btnReplay10,btnNext10,btnPrevious, btnNext;
    SeekBar seekBar;
    ExoPlayer exoPlayer;

    private List<BookChapter> chapterList = new ArrayList<>();

    private int currentChapterIndex = 0;

    Handler handler = new Handler();
    int duration = 0;
    TextView time;
    private IAppApiCaller iAppApiCaller;

    private List<SummaryTime> summaryTimes = new ArrayList<>();

    private TextView txtContentDisplay;

    private int bookId;

    ImageView imgPoster, btnHome;


    @SuppressLint("MissingInflatedId")
    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        makeStatusBarTransparent();
        applyTopPadding();
        LinearLayout bottomSheet = findViewById(R.id.bottomSheetContainer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        ImageView btnReview = findViewById(R.id.btn_review_in_audio);
        btnReview.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetBehavior.setHideable(true);

        recyclerView = findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        client = RetrofitClient.GetOkHttpClient(this);
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(
                this,
                new OkHttpDataSource.Factory(client)
        );

        exoPlayer = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .build();

        String chapterId = getIntent().getStringExtra("chapterId");
        fetchChapterAudio(chapterId);

        time = findViewById(R.id.time);
        imgBtnPlay = findViewById(R.id.btnPlayPause);
        btnReplay10 = findViewById(R.id.btnRewind);
        btnNext10 = findViewById(R.id.btnForward);
        seekBar = findViewById(R.id.seekBar);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnHome = findViewById(R.id.btnBook);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(AudioActivity.this, OpenBookActivity.class);
            intent.putExtra("bookId", bookId); // Truyền bookId sang
            startActivity(intent);
        });

        txtContentDisplay = findViewById(R.id.txtAudioTitle);

        bookId = getIntent().getIntExtra("bookId", -1);
        imgPoster = findViewById(R.id.image_characters_in_detail);

        if (bookId != -1) {
            getBookPoster(bookId);
        }


        // SeekBar setup
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    duration = (int) exoPlayer.getDuration();
                    seekBar.setMax(duration);
                    time.setText("00:00 / " + formatTime(duration));
                    updateSeekBar();
                }
                if(state == Player.STATE_ENDED) {
                    imgBtnPlay.setImageResource(R.drawable.ic_play);
                    exoPlayer.seekTo(0);
                    exoPlayer.pause();
                    seekBar.setProgress(0);
                    time.setText("00:00 / " + formatTime(duration));
                }
            }
        });

        imgBtnPlay.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                imgBtnPlay.setImageResource(R.drawable.ic_play);
                //handler.removeCallbacks(updateTime);
            } else {
                exoPlayer.play();
                imgBtnPlay.setImageResource(R.drawable.ic_pause_circle);
                updateSeekBar();
            }
        });
        btnReplay10.setOnClickListener(v -> {
            int currentPosition = (int) exoPlayer.getCurrentPosition();
            if (currentPosition - 10000 >= 0) {
                exoPlayer.seekTo(currentPosition - 10000);
            } else {
                exoPlayer.seekTo(0);
            }
        });

        btnNext10.setOnClickListener(v -> {
            int currentPosition = (int) exoPlayer.getCurrentPosition();
            if (currentPosition + 10000 <= exoPlayer.getDuration()) {
                exoPlayer.seekTo(currentPosition + 10000);
            } else {
                exoPlayer.seekTo(exoPlayer.getDuration());
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                BookChapter previousChapter = chapterList.get(currentChapterIndex);
                txtContentDisplay.setText(previousChapter.getChapterName());
                fetchChapterAudio(previousChapter.getId());
            } else {
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                BookChapter nextChapter = chapterList.get(currentChapterIndex);
                txtContentDisplay.setText(nextChapter.getChapterName());
                fetchChapterAudio(nextChapter.getId());
            } else {
                Toast.makeText(this, "Đây là chương cuối cùng", Toast.LENGTH_SHORT).show();
            }
        });


        // Khi kéo SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                    time.setText(formatTime(progress) + " / " + formatTime(duration));
                }
            }


            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    private void getBookPoster(int bookId) {
        iAppApiCaller.getBookById(bookId).enqueue(new Callback<ReponderModel<Book>>() {
            @Override
            public void onResponse(Call<ReponderModel<Book>> call, Response<ReponderModel<Book>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Book book = response.body().getData();
                    if (book.getPoster() != null && !book.getPoster().isEmpty()) {
                        Glide.with(AudioActivity.this)
                                .load(book.getPoster())
                                .placeholder(R.drawable.loading_placeholder)
                                .error(R.drawable.error_image)
                                .into(imgPoster);
                    }

                }
            }

            @Override
            public void onFailure(Call<ReponderModel<Book>> call, Throwable t) {

            }
        });
    }

    private void fetchChapterAudio(String chapterId) {
        iAppApiCaller = RetrofitClient.getInstance(Utils.BASE_URL, this).create(IAppApiCaller.class);
        iAppApiCaller.getBookChapterWithVoice(chapterId).enqueue(new Callback<ReponderModel<BookChapter>>() {


            @Override
            public void onResponse(Call<ReponderModel<BookChapter>> call, Response<ReponderModel<BookChapter>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    BookChapter bookChapter = response.body().getData();

                    if (bookChapter.getContentWithTime() != null) {
                        summaryTimes.clear();
                        summaryTimes.addAll(bookChapter.getContentWithTime());
                    }
                    // Gán danh sách chapter từ intent nếu có
                    List<BookChapter> intentList = (List<BookChapter>) getIntent().getSerializableExtra("chapterList");
                    if (intentList != null && !intentList.isEmpty()) {
                        chapterList = intentList;
                        currentChapterIndex = findChapterIndex(bookChapter.getId());
                    } else {
                        chapterList = new ArrayList<>();
                        chapterList.add(bookChapter);
                        currentChapterIndex = 0;
                    }
                    if (bookChapter.getFileName() != null && !bookChapter.getFileName().isEmpty()) {
                        String audioUrl = "https://ireading.store/api/Book/Audio/" + bookChapter.getFileName();
                        // Reset ExoPlayer trước khi set media mới
                        exoPlayer.stop();
                        exoPlayer.clearMediaItems(); // Xóa media cũ
                        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                       // exoPlayer.play();
                    } else {
                        Toast.makeText(AudioActivity.this, "Audio URL is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AudioActivity.this, "Failed to fetch audio URL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<BookChapter>> call, Throwable t) {

            }
        });
    }

    private int findChapterIndex(String chapterId) {
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId().equals(chapterId)) {
                return i;
            }
        }
        return 0;
    }

    private void updateSeekBar() {
        long currentPosition = exoPlayer.getCurrentPosition();
        seekBar.setProgress((int) currentPosition);
        if (exoPlayer.isPlaying()) {
            time.setText(formatTime(currentPosition) + " / " + formatTime(duration));
            updateDisplayContent(currentPosition);
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void updateDisplayContent(long currentPosition) {
        //long delayTime = currentPosition + 500;
        for (SummaryTime summaryTime : summaryTimes) {
          try {
              long startTime = Long.parseLong(summaryTime.getStartTime());
              long endTime = Long.parseLong(summaryTime.getEndTime());

              if(currentPosition >= startTime && currentPosition <= endTime) {
                  txtContentDisplay.setText(summaryTime.getText());
                  return;
              }
          } catch (NumberFormatException e) {
                Log.e("AudioActivity", "Error parsing time: " + e.getMessage());
          }
        }
    }


    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();

        }
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }
    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.audio_container);

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