package com.example.iread.Audio;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Comment.ReviewAdapter;
import com.example.iread.Model.Review;
import com.example.iread.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity {
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    ImageButton imgBtnPlay,btnReplay10,btnNext10;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    int duration = 0;
    TextView time;


    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                time.setText(formatTime(currentPosition) + " / " + formatTime(duration));
                handler.postDelayed(this, 1000);
                if(currentPosition >= mediaPlayer.getDuration()) {
                    imgBtnPlay.setImageResource(R.drawable.ic_play);
                    handler.removeCallbacks(updateTime);
                }
            }
        }
    };
    //List<Review> reviewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        makeStatusBarTransparent();
        applyTopPadding();
        LinearLayout bottomSheet = findViewById(R.id.bottomSheetContainer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        ImageView btnReview = findViewById(R.id.btn_review_in_audio);
        btnReview.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetBehavior.setHideable(true);

        recyclerView = findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mediaPlayer = MediaPlayer.create(this, R.raw.audio);
        seekBar = findViewById(R.id.seekBar);
        duration = mediaPlayer.getDuration();
        seekBar.setMax(duration);
        time = findViewById(R.id.time);
        time.setText("00:00 / " + formatTime(duration));
        imgBtnPlay = findViewById(R.id.btnPlayPause);
        btnReplay10 = findViewById(R.id.btnRewind);
        btnNext10 = findViewById(R.id.btnForward);

        imgBtnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imgBtnPlay.setImageResource(R.drawable.ic_play);
                handler.removeCallbacks(updateTime);
            } else {
                mediaPlayer.start();
                imgBtnPlay.setImageResource(R.drawable.ic_pause_circle);
                handler.post(updateTime);
            }
        });

        btnReplay10.setOnClickListener(v -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition - 10000 >= 0) {
                mediaPlayer.seekTo(currentPosition - 10000);
            } else {
                mediaPlayer.seekTo(0);
            }
        });

        btnNext10.setOnClickListener(v -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition + 10000 <= mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(currentPosition + 10000);
            } else {
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        });

        // Khi kéo SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    time.setText(formatTime(progress) + " / " + formatTime(duration));
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //mediaPlayer.start();
//        reviewList = new ArrayList<>();
//        reviewList.add(new Review("Trung Đình Tuấn", "Doc"));
//        reviewList.add(new Review("tel_09409219***", "Truyện tình tiết nhanh..."));
//        reviewList.add(new Review("nnn***", "68 chương như v là full chị ạ"));
//        reviewList.add(new Review("0368948***", "Quá xuất sắc luôn đó. Luy đến giờ"));
//        reviewList.add(new Review("Dolly My", "***"));
//        reviewList.add(new Review("0353598***", "Mê Tiểu Tiểu với Diễn gia lắm luôn..."));
//
//        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);
    }


    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTime);
        if (mediaPlayer != null) {
            mediaPlayer.release();
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