package com.satnamsinghmaggo.nitnemgurbani;

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


import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Paath_acitvity extends BaseActivity {

    PDFView pdfView;
    ImageView playButton, skipNext, skipPrev,pause;
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable updateSeekBar;
    LottieAnimationView lottieLoader;
    LinearLayout mediaPlayerLayout;
    MediaPlayer mediaPlayer;
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
        applyControlIconTint();
        loadPdfOnce();
        setupAudioPlayer();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void setupAudioPlayer() {
        File audioFile = new File(getFilesDir(), "japji_sahib.mp3");

        if (audioFile.exists()) {
            initMediaPlayer(audioFile.getAbsolutePath());
        } else {
            downloadAudioAndPlay("https://drive.google.com/uc?export=download&id=1iJbzK8AQ-23xU8nwGF6m7NWK_k_AXkkv", audioFile); // Replace with your actual URL
        }
    }

    private void loadPdfOnce() {
        pdfView.fromAsset("Japji_Sahib.pdf")
                .enableAntialiasing(true)
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
    private void downloadAudioAndPlay(String audioUrl, File destinationFile) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(audioUrl).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(destinationFile);

                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }

                    fos.flush();
                    fos.close();
                    inputStream.close();

                    runOnUiThread(() -> initMediaPlayer(destinationFile.getAbsolutePath()));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Audio download failed", Toast.LENGTH_SHORT).show());
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error downloading audio", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void initMediaPlayer(String audioPath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();

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
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setImageResource(R.drawable.play);
                    handler.removeCallbacks(updateSeekBar);
                } else {
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.pause);
                    handler.post(updateSeekBar);
                }
            });

            skipNext.setOnClickListener(v -> mediaPlayer.seekTo(Math.min(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition() + 5000)));
            skipPrev.setOnClickListener(v -> mediaPlayer.seekTo(Math.max(0, mediaPlayer.getCurrentPosition() - 5000)));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) mediaPlayer.seekTo(progress);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing media", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideLoader() {
        if (lottieLoader != null) {
            lottieLoader.cancelAnimation();
            lottieLoader.setVisibility(View.GONE);
        }
    }

    private void animateUiAfterLoad() {
        hideLoader();
        mediaPlayerLayout.setVisibility(View.VISIBLE);
        seekBar.setAlpha(0f);
        seekBar.animate().alpha(1f).setDuration(600).start();
        mediaPlayerLayout.setAlpha(0f);
        mediaPlayerLayout.animate().alpha(1f).setDuration(600).start();
    }


}
