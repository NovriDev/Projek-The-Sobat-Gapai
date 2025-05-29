package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_sobat_gapai.Adapter.TugasAdapter;
import com.example.the_sobat_gapai.Adapter.ViewTugasAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewTugasFragment extends Fragment implements TugasAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ViewTugasAdapter tugasAdapter;
    private List<Tugas> tugasList;
    private int tugasId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private com.example.the_sobat_gapai.databinding.ActivityMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        binding = com.example.the_sobat_gapai.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        binding.toolbarTitle.setText("Beranda");

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.refresh);

        if (getArguments() != null) {
            tugasId = getArguments().getInt("tugas_id");
            Log.d("userId", "onCreateView: " + String.valueOf(tugasId));
        }

        // Atur Adapter
        tugasList = new ArrayList<>();
        tugasAdapter = new ViewTugasAdapter(getContext(), tugasList);
        recyclerView.setAdapter(tugasAdapter);


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = databaseHelper.getToken();
        Log.d("Token e", "token: "+token);

        loadTugasData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulasi Refresh dengan Delay 2 Detik
            new Handler().postDelayed(() -> {
                // Perbarui Data
                loadTugasData();
                // Hentikan Refreshing
                swipeRefreshLayout.setRefreshing(false);
            }, 2000);
        });

        return view;
    }
    @Override
    public void onFavoriteClicked() {
        // Panggil ulang API saat tombol favorite diklik
        loadTugasData();
    }

    private void loadTugasData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        apiService.getTugasOther(token, tugasId).enqueue(new Callback<Tugas>() {
            @Override
            public void onResponse(Call<Tugas> call, Response<Tugas> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Raw JSON: " + response.body().toString());
                    tugasList.clear();
                    tugasList.add(response.body());
                    tugasAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Log.d("Error 1", "onResponse Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<Tugas> call, Throwable t) {
                Log.d("Error 2", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

}
