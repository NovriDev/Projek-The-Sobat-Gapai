package com.example.the_sobat_gapai;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.the_sobat_gapai.Fragment.FriendFragment;
import com.example.the_sobat_gapai.Fragment.HomeFragment;
import com.example.the_sobat_gapai.Fragment.NotifikasiFragment;
import com.example.the_sobat_gapai.Fragment.ProfileFragment;
import com.example.the_sobat_gapai.Fragment.UploadFragment;
import com.example.the_sobat_gapai.databinding.ActivityMainBinding;
import com.example.the_sobat_gapai.databinding.FormAppealBinding;

public class AppealActivity extends AppCompatActivity {
    FormAppealBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = FormAppealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSelesai.setOnClickListener(v -> {
            // Ambil data dari inputan pengguna
            String username = binding.username.getText().toString().trim();
            String emailPengguna = binding.email.getText().toString().trim();
            String isiAjuan = binding.ajuan.getText().toString().trim();

            // Pastikan email pengguna tidak kosong
            if (emailPengguna.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format isi email
            String subject = "Permohonan Banding Akun - " + username;
            String message = "Halo,\n\nSaya ingin mengajukan banding atas akun saya.\n\n" +
                    "Nama Pengguna: " + username + "\n" +
                    "Email: " + emailPengguna + "\n" +
                    "Deskripsi: \n" + isiAjuan + "\n\n" +
                    "Terima kasih.";

            // Intent untuk membuka Gmail dengan semua data otomatis terisi
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // Memastikan hanya aplikasi email yang menangani intent ini
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tnpanama.0123@gmail.com"}); // Ganti dengan email tujuan
            intent.putExtra(Intent.EXTRA_SUBJECT, subject); // Subjek email
            intent.putExtra(Intent.EXTRA_TEXT, message); // Isi email

            try {
                startActivity(Intent.createChooser(intent, "Kirim email dengan"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Tidak ada aplikasi email yang terpasang!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
