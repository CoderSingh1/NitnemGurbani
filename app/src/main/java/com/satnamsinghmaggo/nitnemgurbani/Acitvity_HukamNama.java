package com.satnamsinghmaggo.nitnemgurbani;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    ProgressBar progressBar;
    String pdfURL = "https://hs.sgpc.net/pdfdownload.php?id=261";
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitvity_hukam_nama);


        pdfview = findViewById(R.id.pdfview);
        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar.setVisibility(ProgressBar.VISIBLE);
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
                    progressBar.setVisibility(View.GONE);
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
                            progressBar.setVisibility(View.GONE);
                        });
                        Log.d(TAG, "PDF downloaded and loaded successfully");
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(Acitvity_HukamNama.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });
    }
}
