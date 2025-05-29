package com.example.the_sobat_gapai;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        setContentView(R.layout.splash_screen);

        // Inisialisasi VideoView
        VideoView videoView = findViewById(R.id.splash_video);

        // Set video dari resource raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        videoView.setVideoURI(videoUri);

        // Mulai memutar video
        videoView.start();

        // Listener untuk mengetahui kapan video selesai diputar
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Pindah ke MainActivity setelah video selesai
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish(); // Tutup SplashActivity
            }
        });
    }
}