package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_sobat_gapai.Adapter.NotifikasiAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Notifikasi;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.MainFragmentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifikasiFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotifikasiAdapter adapter;
    private MainFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        binding = MainFragmentBinding.inflate(inflater, container, false);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // Memanggil API untuk mendapatkan notifikasi
        getNotifications();

        binding.refresh.setOnRefreshListener(() -> {
            // Simulasi Refresh dengan Delay 2 Detik
            new Handler().postDelayed(() -> {
                // Perbarui Data
                getNotifications();
                // Hentikan Refreshing
                binding.refresh.setRefreshing(false);
            }, 2000);
        });
        return binding.getRoot();
    }

    private void getNotifications() {
        if (binding == null) return;
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        apiService.getNotifications(token).enqueue(new Callback<List<Notifikasi>>() {
            @Override
            public void onResponse(Call<List<Notifikasi>> call, Response<List<Notifikasi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notifikasi> notifications = response.body();

                    if (notifications.isEmpty()) {
                        // Jika tidak ada notifikasi, tampilkan pesan
                        binding.tvNoNotifications.setVisibility(View.VISIBLE);
                        binding.recyclerview.setVisibility(View.GONE);
                    } else {
                        binding.tvNoNotifications.setVisibility(View.GONE);
                        binding.recyclerview.setVisibility(View.VISIBLE);

                        // Persiapkan data untuk adapter
                        List<Object> mixedList = new ArrayList<>();
                        String currentHeader = "";

                        for (Notifikasi notification : notifications) {
                            String header = getHeaderForDate(notification.getCreatedAt());
                            if (!currentHeader.equals(header)) {
                                currentHeader = header;
                                mixedList.add(currentHeader);
                            }
                            mixedList.add(notification);
                        }

                        // Atur adapter dan RecyclerView
                        adapter = new NotifikasiAdapter(getContext(), mixedList);
                        binding.recyclerview.setAdapter(adapter);
                    }
                } else {
                    Log.e("NOTIFICATIONS_ERROR", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Notifikasi>> call, Throwable t) {
                Log.e("NOTIFICATIONS_FAILURE", "Error: " + t.getMessage());
            }
        });
    }


    // Fungsi untuk mendapatkan header berdasarkan tanggal
    private String getHeaderForDate(Date date) {
        if (date == null) return "Tanggal Tidak Valid";

        Calendar now = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(date);

        int nowYear = now.get(Calendar.YEAR);
        int createdYear = createdDate.get(Calendar.YEAR);

        if (nowYear != createdYear) {
            return "Tahun Lalu";
        }

        int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
        int createdDayOfYear = createdDate.get(Calendar.DAY_OF_YEAR);

        int diff = nowDayOfYear - createdDayOfYear;

        if (diff == 0) {
            return "Hari ini";
        } else if (diff == 1) {
            return "Kemarin";
        } else if (diff == 2) {
            return "Kemarin Dulu";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(date); // Format default jika tidak masuk kategori
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }
}
