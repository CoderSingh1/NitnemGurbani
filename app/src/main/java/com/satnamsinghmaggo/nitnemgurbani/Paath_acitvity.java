package com.satnamsinghmaggo.nitnemgurbani;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.slider.Slider;

public class Paath_acitvity extends AppCompatActivity {

    PDFView pdfView;
    ImageView playButton, skipNext, skipPrev;
    Slider slider;

    private boolean isPlaying = false;
    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paath_acitvity);

        pdfView = findViewById(R.id.pdfview1);
        playButton = findViewById(R.id.play);
        skipNext = findViewById(R.id.skipnext);
        skipPrev = findViewById(R.id.skipprev);
        slider = findViewById(R.id.slider);

        // Load PDF and setup slider
        pdfView.fromAsset("presentation.pdf")
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onLoad(nbPages -> {
                    totalPages = nbPages;
                    slider.setValueTo(nbPages - 1);
                    slider.setValue(0);
                })
                .onPageChange((page, pageCount) -> {
                    currentPage = page;
                    slider.setValue(page);
                })
                .load();

        // Set play/pause functionality
        playButton.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            playButton.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);

            if (isPlaying) {
                startAutoScroll();
            }
        });

        // Skip to next page
        skipNext.setOnClickListener(v -> {
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
        });

        // Sync slider with PDF
        slider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                currentPage = (int) value;
                pdfView.jumpTo(currentPage, true);
            }
        });

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startAutoScroll() {
        new Thread(() -> {
            while (isPlaying && currentPage < totalPages - 1) {
                try {
                    Thread.sleep(3000); // Scroll every 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentPage++;
                runOnUiThread(() -> pdfView.jumpTo(currentPage, true));
            }

            runOnUiThread(() -> {
                isPlaying = false;
                playButton.setImageResource(R.drawable.play);
            });
        }).start();
    }
}
