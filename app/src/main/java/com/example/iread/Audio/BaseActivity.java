package com.example.iread.Audio;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iread.R;

public abstract class BaseActivity extends AppCompatActivity {
    protected FrameLayout miniAudioContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupMiniAudio() {
        miniAudioContainer = findViewById(R.id.miniAudioContainer);
        // showMiniAudio logic can be extracted to a utility if needed
    }

    protected void showMiniAudio(String title, Drawable posterDrawable, View.OnClickListener onPlayClick) {
        if (miniAudioContainer.getChildCount() == 0) {
            View miniAudioView = LayoutInflater.from(this).inflate(R.layout.item_audio_track, miniAudioContainer, false);
            TextView txtTitle = miniAudioView.findViewById(R.id.txtMiniTitle);
            ImageView poster = miniAudioView.findViewById(R.id.imgMiniPoster);
            ImageView btnMiniPlay = miniAudioView.findViewById(R.id.btnMiniPlay);

            txtTitle.setText(title);
            txtTitle.setSelected(true);
            poster.setImageDrawable(posterDrawable);
            btnMiniPlay.setOnClickListener(onPlayClick);

            miniAudioContainer.addView(miniAudioView);
        }

        miniAudioContainer.setVisibility(View.VISIBLE);
    }

    protected void hideMiniAudio() {
        if (miniAudioContainer != null) {
            miniAudioContainer.setVisibility(View.GONE);
        }
    }
}
