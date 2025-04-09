package com.satnamsinghmaggo.nitnemgurbani;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.github.barteksc.pdfviewer.PDFView;

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
    PDFView pdfview;

    LottieAnimationView lottieLoader;
    ImageView playButton, skipNext, skipPrev,pause;

    String pdfURL = "https://hs.sgpc.net/pdfdownload.php?id=261";
    String audioUrl = "https://hs.sgpc.net/audiohukamnama/audio-67f5b8ec202106.27442149.mp3";
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable updateSeekBar;
    private boolean isPlaying = false;
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitvity_hukam_nama);

        pdfview = findViewById(R.id.pdfview);
        lottieLoader = findViewById(R.id.lottieLoader);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.play);

       // progressBar = findViewById(R.id.progressBar);
        // Set the visibility of the progressBar.bringToFront();
        lottieLoader.bringToFront();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync(); // Prepare asynchronously to not block the UI
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Audio file error", Toast.LENGTH_SHORT).show();
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
        playButton.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                playButton.setImageResource(R.drawable.pause);
                handler.post(updateSeekBar);
                Toast.makeText(this, "Playing Audio", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setImageResource(R.drawable.play);
                handler.removeCallbacks(updateSeekBar);
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });


        //progressBar.setVisibility(ProgressBar.VISIBLE);
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.playAnimation();
        downloadAndDisplayPdf(pdfURL);
        Log.d(TAG, "Views initialized successfully");
    }


    private void downloadAndDisplayPdf(String url) {
        Request request = new Request.Builder().url(url).build();
        Log.d(TAG, "Downloading PDF started");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Downloading failed");
                runOnUiThread(() -> {
                    Toast.makeText(Acitvity_HukamNama.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                    lottieLoader.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    File pdfFile = new File(getCacheDir(), "downloaded.pdf");
                    assert response.body() != null;
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();
                        runOnUiThread(() -> {
                            pdfview.fromFile(pdfFile).load();
                           // progressBar.setVisibility(View.GONE);
                            lottieLoader.setVisibility(View.GONE);
                        });
                        Log.d(TAG, "PDF downloaded and loaded successfully");
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(Acitvity_HukamNama.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                       // progressBar.setVisibility(View.GONE);
                        lottieLoader.setVisibility(View.GONE);
                    });
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
