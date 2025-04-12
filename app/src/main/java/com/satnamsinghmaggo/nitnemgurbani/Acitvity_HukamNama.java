package com.satnamsinghmaggo.nitnemgurbani;

import android.animation.Animator;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Acitvity_HukamNama extends AppCompatActivity {

    private static final String TAG = "Acitvity_HukamNama";
    private PDFView pdfView;
    private LottieAnimationView lottieLoader;
    private ImageView playButton, skipNext, skipPrev;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private LinearLayout mediaPlayerLayout;
    private boolean isPlaying = false;
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitvity_hukam_nama);

        initViews();
        applyControlIconTint();
        setupLottieAnimation();
        applyWindowInsets();

        fetchDailyPdfLink();
        fetchMp3AndPlay();
    }

    private void initViews() {
        pdfView = findViewById(R.id.pdfview);
        lottieLoader = findViewById(R.id.lottieLoader);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.play);
        skipNext = findViewById(R.id.skipnext);
        skipPrev = findViewById(R.id.skipprev);
        mediaPlayerLayout = findViewById(R.id.linearLayout1);
    }

    private void setupLottieAnimation() {
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.bringToFront();
        lottieLoader.playAnimation();

        lottieLoader.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                mediaPlayerLayout.setVisibility(View.VISIBLE);
                seekBar.setAlpha(0f);
                seekBar.animate().alpha(1f).setDuration(600).start();
                mediaPlayerLayout.setAlpha(0f);
                mediaPlayerLayout.animate().alpha(1f).setDuration(600).start();
            }

            @Override public void onAnimationEnd(@NonNull Animator animation) {}
            @Override public void onAnimationCancel(@NonNull Animator animation) {}
            @Override public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchDailyPdfLink() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();
                Element link = doc.select("div.pdf-section a.pdf-button[href]").first();
                if (link != null) {
                    String pdfUrl = "https://hs.sgpc.net/" + link.attr("href");
                    runOnUiThread(() -> downloadAndDisplayPdf(pdfUrl));
                } else {
                    showToast("No PDF link found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error fetching PDF link");
            }
        }).start();
    }

    private void downloadAndDisplayPdf(String url) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                showToast("Failed to download PDF");
                hideLoader();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    File pdfFile = new File(getCacheDir(), "downloaded.pdf");
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        runOnUiThread(() -> {
                            pdfView.fromFile(pdfFile).load();
                            hideLoader();
                        });
                    }
                } else {
                    showToast("Failed to download PDF");
                    hideLoader();
                }
            }
        });
    }

    private void fetchMp3AndPlay() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();
                Element audioElement = doc.select("div.audio-card audio.audio-player[src]").first();
                if (audioElement != null) {
                    String audioUrl = audioElement.attr("src");
                    runOnUiThread(() -> initializeMediaPlayer(audioUrl));
                } else {
                    showToast("Audio link not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error fetching audio");
            }
        }).start();
    }

    private void initializeMediaPlayer(String audioUrl) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Audio file error");
            return;
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mp.getDuration());
            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(this, 500);
                    }
                }
            };
        });

        playButton.setOnClickListener(v -> togglePlayback());
        skipNext.setOnClickListener(v -> seekBy(5000));
        skipPrev.setOnClickListener(v -> seekBy(-5000));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void togglePlayback() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                playButton.setImageResource(R.drawable.pause);
                handler.post(updateSeekBar);
                showToast("Playing Audio");
            } else {
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setImageResource(R.drawable.play);
                handler.removeCallbacks(updateSeekBar);
                showToast("Paused");
            }
        }
    }

    private void seekBy(int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() + milliseconds;
            newPosition = Math.max(0, Math.min(newPosition, mediaPlayer.getDuration()));
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoader() {
        runOnUiThread(() -> lottieLoader.setVisibility(View.GONE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
    private void applyControlIconTint() {
        TypedValue typedValue = new TypedValue();
        // Use your app's theme attribute instead of androidx.appcompat
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);

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
