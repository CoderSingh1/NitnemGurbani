package com.satnamsinghmaggo.nitnemgurbani;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.slider.Slider;

import java.io.IOException;

public class Paath_acitvity extends AppCompatActivity {

    PDFView pdfView;
    ImageView playButton, skipNext, skipPrev,pause;
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable updateSeekBar;
    MediaPlayer mediaPlayer;
    String audioUrl = "https://hs.sgpc.net/audiohukamnama/audio-67f5b8ec202106.27442149.mp3";

    private boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paath_acitvity);

        pdfView = findViewById(R.id.pdfview1);
        playButton = findViewById(R.id.play);
        skipNext = findViewById(R.id.skipnext);
        skipPrev = findViewById(R.id.skipprev);
        seekBar = findViewById(R.id.seekBar);
        //pause = findViewById(R.drawable.pause);

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



        // Skip to next page
        /*skipNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                pdfView.jumpTo(currentPage, true);
            } else {
                Toast.makeText(this, "Last Page", Toast.LENGTH_SHORT).show();
            }
        });

        // Skip to previous page
        skipPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                pdfView.jumpTo(currentPage, true);
            } else {
                Toast.makeText(this, "First Page", Toast.LENGTH_SHORT).show();
            }
        });*/



        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
