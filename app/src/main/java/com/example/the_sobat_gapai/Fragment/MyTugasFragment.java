package com.example.the_sobat_gapai.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_sobat_gapai.Adapter.MyTugasAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Response.ErrorResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTugasFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyTugasAdapter tugasAdapter;
    private List<Tugas> tugasList;
    private com.example.the_sobat_gapai.databinding.ActivityMainBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        binding = com.example.the_sobat_gapai.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        binding.toolbarTitle.setText("Beranda");
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulasi Refresh dengan Delay 2 Detik
            new Handler().postDelayed(() -> {
                // Perbarui Data
                loadTugasData();
                // Hentikan Refreshing
                swipeRefreshLayout.setRefreshing(false);
            }, 2000);
        });

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Atur Adapter
        tugasList = new ArrayList<>();
        tugasAdapter = new MyTugasAdapter(getContext(), tugasList, tugasId -> {
            // Handle delete task
            showDeleteConfirmationDialog(tugasId);
        });
        recyclerView.setAdapter(tugasAdapter);

        loadTugasData();

        return view;
    }

    private void showDeleteConfirmationDialog(int tugasId) {
        // Buat AlertDialog
        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus tugas ini? Semua jawaban akan juga ikut terhapus")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    // Jika pengguna memilih untuk menghapus
                    deleteUserTask(tugasId);
                })
                .setNegativeButton("Batal", (dialog, which) -> {
                    // Tutup dialog jika pengguna membatalkan
                    dialog.dismiss();
                })
                .create()
                .show();
    }
    private void loadTugasData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        apiService.getUserTugas(token).enqueue(new Callback<List<Tugas>>() {
            @Override
            public void onResponse(Call<List<Tugas>> call, Response<List<Tugas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tugasList.clear();
                    tugasList.addAll(response.body());
                    tugasAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Error 1", "onResponse: " + response.errorBody() + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Tugas>> call, Throwable t) {
                Log.d("Error 2", "onFailure: " + t.getMessage());
            }
        });
    }

    private void deleteUserTask(int tugasId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<Void> call = apiService.deleteUserTugas(token, tugasId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadTugasData(); // Reload list
                } else {
                    Log.d("TAG", "onResponse: " + response.errorBody() + response.message());
                    Toast.makeText(getContext(), "Gagal menghapus tugas", Toast.LENGTH_SHORT).show();
                    // Tangkap error response body
                    try {
                        ResponseBody errorBody = response.errorBody();
                        String errorJson = errorBody.string(); // Convert error body ke String
                        Log.d("TAG", "Error body: " + errorJson);

                        // Gunakan Gson untuk parse error JSON jika formatnya sesuai
                        ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
                        Log.d("TAG", "Error Message: " + errorResponse.getMessage());
                    } catch (Exception e) {
                        Log.e("TAG", "Error parsing error response: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

}
