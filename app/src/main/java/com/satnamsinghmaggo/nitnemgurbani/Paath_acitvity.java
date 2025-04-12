package com.satnamsinghmaggo.nitnemgurbani;

import android.animation.Animator;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.slider.Slider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Paath_acitvity extends AppCompatActivity {

    PDFView pdfView;
    ImageView playButton, skipNext, skipPrev,pause;
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable updateSeekBar;
    LottieAnimationView lottieLoader;
    LinearLayout mediaPlayerLayout;
    MediaPlayer mediaPlayer;
    String audioUrl = "https://hs.sgpc.net/audiohukamnama/audio-67f5b8ec202106.27442149.mp3";

    private boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paath);

        pdfView = findViewById(R.id.pdfview1);
        playButton = findViewById(R.id.play);
        skipNext = findViewById(R.id.skipnext);
        skipPrev = findViewById(R.id.skipprev);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayerLayout = findViewById(R.id.linearLayout);
        //pause = findViewById(R.drawable.pause);
       // lottieLoader = findViewById(R.id.lottieLoader1);
        applyControlIconTint();
      //  setupLottieAnimation();
        loadLocalPdf();
        setupAudioPlayer();

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void setupAudioPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.japji_sahib);
        seekBar.setMax(mediaPlayer.getDuration());

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        };

        playButton.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause);
                handler.post(updateSeekBar);
            } else {
                mediaPlayer.pause();
                playButton.setImageResource(R.drawable.play);
                handler.removeCallbacks(updateSeekBar);
            }
        });

        skipNext.setOnClickListener(v -> {
            int pos = mediaPlayer.getCurrentPosition() + 5000;
            mediaPlayer.seekTo(Math.min(pos, mediaPlayer.getDuration()));
        });

        skipPrev.setOnClickListener(v -> {
            int pos = mediaPlayer.getCurrentPosition() - 5000;
            mediaPlayer.seekTo(Math.max(pos, 0));
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadLocalPdf() {
        pdfView.fromAsset("Japji[Gurmukhi].pdf")
                .enableAntialiasing(true)
                .onLoad(nbPages -> {
                    // PDF loaded, stop and hide loader
                    if (lottieLoader != null) {
                        lottieLoader.cancelAnimation();
                        lottieLoader.setVisibility(View.GONE);
                    }

                    // Show media controls with animation
                    mediaPlayerLayout.setVisibility(View.VISIBLE);
                    seekBar.setAlpha(0f);
                    seekBar.animate().alpha(1f).setDuration(600).start();
                    mediaPlayerLayout.setAlpha(0f);
                    mediaPlayerLayout.animate().alpha(1f).setDuration(600).start();
                })
                .onError(t -> {
                    Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
                    if (lottieLoader != null) {
                        lottieLoader.cancelAnimation();
                        lottieLoader.setVisibility(View.GONE);
                    }
                })
                .load();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
        handler.removeCallbacks(updateSeekBar);
    }
    private void setupLottieAnimation() {
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.bringToFront();
        lottieLoader.playAnimation();
    }
    private void applyControlIconTint() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorControlNormal, typedValue, true);

        int color;
        if (typedValue.resourceId != 0) {
            color = ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            color = typedValue.data;
        }

        playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        skipNext.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        skipPrev.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
