package com.example.the_sobat_gapai.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.FileUtils;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Response.UploadResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.FormUploadBinding;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFragment extends Fragment {
    private FormUploadBinding binding;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        binding = FormUploadBinding.inflate(inflater, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Jika izin belum diberikan, minta izin
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        // Listener untuk RadioGroup
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadio = binding.getRoot().findViewById(checkedId);
            if (selectedRadio != null) {
                Toast.makeText(getContext(), "Terpilih: " + selectedRadio.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // Listener untuk ImageView (pilih gambar)
        binding.imgTugas.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100); // Memulai Activity untuk memilih gambar
        });

        binding.btnRemoveImage.setOnClickListener(v -> {
            // Hapus gambar dari ImageView
            Glide.with(requireContext()).clear(binding.imgTugas); // Hapus cache Glide
            binding.imgTugas.setImageResource(R.drawable.ic_add_tugas); // Gambar default

            // Hapus file gambar dari cache
            if (imageUri != null) {
                File file = new File(imageUri.getPath());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    Log.d("UploadFragment", "File gambar dihapus: " + deleted);
                }
            }

            imageUri = null; // Reset URI gambar

            // Hapus cache Glide
            new Thread(() -> {
                Glide.get(requireContext()).clearDiskCache(); // Hapus cache disk Glide
                requireActivity().runOnUiThread(() -> {
                    Glide.get(requireContext()).clearMemory(); // Hapus cache memori Glide
                });
            }).start();

            // Sembunyikan tombol Hapus Gambar
            binding.btnRemoveImage.setVisibility(View.GONE);

            Toast.makeText(getContext(), "Gambar dihapus", Toast.LENGTH_SHORT).show();
        });

        // Listener untuk tombol Upload
        binding.btnUpload.setOnClickListener(v -> uploadData());

        return binding.getRoot();
    }

    private void uploadData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        // Validasi input
        int selectedId = binding.radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Pilih salah satu mata pelajaran", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = binding.getRoot().findViewById(selectedId);
        String selectedSubject = selectedRadio.getText().toString();
        String deskripsi = binding.etDeskripsi.getText().toString();
        String keterangan = binding.etMateri.getText().toString();

        if (selectedSubject.isEmpty() || deskripsi.isEmpty() || keterangan.isEmpty()) {
            Toast.makeText(getContext(), "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat RequestBody untuk data teks
        RequestBody keteranganField = RequestBody.create(MediaType.parse("text/plain"), keterangan);
        RequestBody mapelField = RequestBody.create(MediaType.parse("text/plain"), selectedSubject);
        RequestBody descriptionField = RequestBody.create(MediaType.parse("text/plain"), deskripsi);

        // Menyiapkan file gambar (jika ada)
        MultipartBody.Part imageTugas = null;
        if (imageUri != null) {
            String filePath = FileUtils.getPath(getContext(), imageUri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                imageTugas = MultipartBody.Part.createFormData("image_tugas", file.getName(), requestFile);
            } else {
                Toast.makeText(getContext(), "Gagal memproses file", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Menjalankan request ke server
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadTugas(
                token,                      // Token authorization
                "application/json",         // Accept header
                keteranganField,            // Keterangan
                mapelField,                 // Mata pelajaran
                descriptionField,           // Deskripsi
                imageTugas                 // Gambar (jika ada)
        );

        // Progress Dialog untuk menampilkan status
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Mengunggah tugas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Menjalankan request ke server
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("Upload Response", "Response: " + responseBody);
                        Toast.makeText(getContext(), "Tugas berhasil diunggah", Toast.LENGTH_SHORT).show();
                        updateUserLevel();
                        Glide.with(requireContext()).clear(binding.imgTugas); // Hapus cache Glide
                        binding.imgTugas.setImageResource(R.drawable.ic_add_tugas); // Gambar default
                        // Hapus file gambar dari cache
                        if (imageUri != null) {
                            File file = new File(imageUri.getPath());
                            if (file.exists()) {
                                boolean deleted = file.delete();
                                Log.d("UploadFragment", "File gambar dihapus: " + deleted);
                            }
                        }
                        imageUri = null; // Reset URI gambar
                        // Hapus cache Glide
                        new Thread(() -> {
                            Glide.get(requireContext()).clearDiskCache(); // Hapus cache disk Glide
                            requireActivity().runOnUiThread(() -> {
                                Glide.get(requireContext()).clearMemory(); // Hapus cache memori Glide
                            });
                        }).start();
                        navigateToHomeFragment();
                    } catch (Exception e) {
                        Log.e("Upload Error", "Gagal membaca respons: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal membaca respons dari server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Upload Error", "Error: " + errorBody);
                        Toast.makeText(getContext(), "Gagal mengunggah tugas", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Upload Error", "Gagal membaca error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Upload Failure", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Gagal mengunggah tugas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserLevel() {
        // Ambil userId dari DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        int userId = databaseHelper.getUserId();

        if (userId == -1) {
            Log.e("TAG", "User ID tidak ditemukan");
            return;
        }

        // Ambil token dari DatabaseHelper
        String token = "Bearer " + databaseHelper.getToken();

        // Buat data level yang diperbarui
        Map<String, Integer> levelData = new HashMap<>();
        levelData.put("level", 15); // Tambahkan level sebesar 25

        // Panggil API untuk update level
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<ResponseBody> call = apiService.updateUser(token, "application/json", userId, levelData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "Level pengguna berhasil diperbarui di server");
                    Toast.makeText(getContext(), "Level XP Anda bertambah 15!", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("TAG", "Gagal memperbarui level pengguna di server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "Error: " + t.getMessage());
            }
        });
    }

    private void navigateToHomeFragment() {
        // Buat instance Fragment Home
        HomeFragment homeFragment = new HomeFragment();

        // Lakukan transaksi Fragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, homeFragment) // `R.id.fragment_container` adalah ID container
                    .addToBackStack(null) // Jika ingin bisa kembali, gunakan addToBackStack
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

    private void startUCrop(Uri sourceUri) {
        // Tentukan lokasi dan nama file untuk gambar yang sudah dipotong
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));

        // Konfigurasi uCrop
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(90); // Kualitas kompresi gambar (0-100)
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.white)); // Warna toolbar
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.black)); // Warna status bar
        options.setToolbarTitle("Potong Gambar"); // Judul toolbar

        // Mulai uCrop
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // Rasio aspek (1:1 untuk persegi)
                .withMaxResultSize(1080, 1080) // Ukuran maksimal hasil
                .withOptions(options)
                .start(requireContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Log.d("UploadFragment", "URI gambar dipilih: " + selectedImageUri);
                startUCrop(selectedImageUri);
            } else {
                Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Log.d("UploadFragment", "URI gambar setelah dipotong: " + resultUri);
                Glide.with(requireContext())
                        .load(resultUri)
                        .into(binding.imgTugas);
                imageUri = resultUri;
                binding.btnRemoveImage.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "Gagal memotong gambar", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UploadFragment", "uCrop Error: " + cropError.getMessage());
                Toast.makeText(requireContext(), "Gagal memotong gambar: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
