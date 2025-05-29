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
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements TugasAdapter.OnFavoriteClickListener, SearchableFragment {

    private RecyclerView recyclerView;
    private TugasAdapter tugasAdapter;
    private List<Tugas> tugasList;
    private List<Tugas> filteredTugasList;
    private TextView notFound;
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

        // Atur Adapter
        tugasList = new ArrayList<>();
        tugasAdapter = new TugasAdapter(getContext(), tugasList);
        recyclerView.setAdapter(tugasAdapter);

        notFound = view.findViewById(R.id.noResultsTextView);

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

        tugasAdapter.setOnProfileClickListener(userId -> {
            ViewProfileFragment profileFragment = new ViewProfileFragment();

            // Kirim userId ke ProfileFragment
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", userId);
            profileFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, profileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
    @Override
    public void onFavoriteClicked() {
        // Panggil ulang API saat tombol favorite diklik
        loadTugasData();
    }

    private void loadTugasData() {
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        apiService.getTugas().enqueue(new Callback<List<Tugas>>() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

    @Override
    public void onSearchQuery(String query) {
        // Filter daftar tugas berdasarkan deskripsi yang cocok dengan query pencarian
        if (TextUtils.isEmpty(query)) {
            filteredTugasList.clear();
            filteredTugasList.addAll(tugasList);
        } else {
            filteredTugasList = tugasList.stream()
                    .filter(tugas -> tugas.getDeskripsi().toLowerCase().contains(query.toLowerCase()) || tugas.getKeterangan().toLowerCase().contains(query.toLowerCase()) || tugas.getMapel().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        tugasAdapter.updateTugasList(filteredTugasList);

        if (filteredTugasList.isEmpty()) {
            // Tampilkan pesan jika tidak ada tugas yang ditemukan
            notFound.setVisibility(View.VISIBLE);
        } else {
            // Sembunyikan pesan jika ada tugas yang ditemukan
            notFound.setVisibility(View.GONE);
        }
    }

}
