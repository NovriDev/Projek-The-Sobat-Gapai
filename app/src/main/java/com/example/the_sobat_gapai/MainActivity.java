package com.example.the_sobat_gapai;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Fragment.FriendFragment;
import com.example.the_sobat_gapai.Fragment.HomeFragment;
import com.example.the_sobat_gapai.Fragment.NotifikasiFragment;
import com.example.the_sobat_gapai.Fragment.ProfileFragment;
import com.example.the_sobat_gapai.Fragment.UploadFragment;
import com.example.the_sobat_gapai.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Atur video background
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video2);
        binding.videoBackground.setVideoURI(videoUri);
        binding.videoBackground.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            binding.videoBackground.start();
        });
        binding.toolbarTitle.setText("Beranda");
        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Listener untuk navigasi bottom
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
                binding.toolbarTitle.setText("Beranda");
            } else if (item.getItemId() == R.id.navigation_fl) {
                selectedFragment = new FriendFragment();
                binding.toolbarTitle.setText("Pertemanan");
            } else if (item.getItemId() == R.id.navigation_upload) {
                selectedFragment = new UploadFragment();
                binding.toolbarTitle.setText("Unggah");
            } else if (item.getItemId() == R.id.navigation_notifications) {
                selectedFragment = new NotifikasiFragment();
                binding.toolbarTitle.setText("Notifikasi");
            } else if (item.getItemId() == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
                binding.toolbarTitle.setText("Profil");
            }

            if (selectedFragment != null) {
                // Ganti fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });


        // Tampilkan HomeFragment secara default
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}
