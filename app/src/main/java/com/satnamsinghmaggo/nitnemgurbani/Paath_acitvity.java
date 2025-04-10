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

        fetchMp3AndPlay();

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    private void mp3AudioPlayer() throws IOException {
        new Thread(() -> {
            try {
                // Network operation
                Document doc = Jsoup.connect("https://hs.sgpc.net/")
                        .userAgent("Mozilla/5.0")
                        .get();

                Element audioElement = doc.select("audio.audio-player[src]").first();
                String audioUrl = null;

                if (audioElement != null) {
                    audioUrl = audioElement.attr("src");
                } else {
                    // Fallback to Regex
                    Pattern pattern = Pattern.compile("https://hs\\.sgpc\\.net/audiohukamnama/audio-[a-zA-Z0-9.]+\\.mp3");
                    Matcher matcher = pattern.matcher(doc.html());
                    if (matcher.find()) {
                        audioUrl = matcher.group();
                    }
                }

                String finalAudioUrl = audioUrl;

                if (finalAudioUrl != null) {
                    runOnUiThread(() -> {
                        // Call your play function here
                        Toast.makeText(getApplicationContext(),finalAudioUrl,Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Paath_acitvity.this, "Audio not found", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Paath_acitvity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();

    }
    private void fetchMp3AndPlay() {
        new Thread(() -> {
            try {
                // Replace with your real Hukamnama page URL
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();

                // Find the <audio> tag and get the 'src' attribute
                Element audioElement = doc.select("div.audio-card audio.audio-player[src]").first();

                if (audioElement != null) {
                    String audioUrl = audioElement.attr("src");

                    runOnUiThread(() -> {
                        Toast.makeText(this, audioUrl, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Audio link not found", Toast.LENGTH_SHORT).show());
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching audio", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
