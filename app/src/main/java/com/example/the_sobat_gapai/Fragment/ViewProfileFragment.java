package com.example.the_sobat_gapai.Fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.Adapter.TugasAdapter;
import com.example.the_sobat_gapai.Adapter.ViewProfileAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.FormViewProfileBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileFragment extends Fragment{

    private RecyclerView recyclerView;
    private ViewProfileAdapter profileAdapter;
    private List<Tugas> profileAdapterList;
    private int userId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FormViewProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        binding = FormViewProfileBinding.inflate(getLayoutInflater());
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_corner);
        binding.level.setBackground(drawable);
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.parseColor("#262627"), Color.parseColor("#1B47FA"));
        colorAnimator.setDuration(2000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.addUpdateListener(animation -> drawable.setColor((int) animation.getAnimatedValue()));
        colorAnimator.start();

        if (getArguments() != null) {
            userId = getArguments().getInt("user_id");
            Log.d("userId", "onCreateView: " + String.valueOf(userId));
        }

        // Inisialisasi RecyclerView
        recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Atur Adapter
        profileAdapterList = new ArrayList<>();
        profileAdapter = new ViewProfileAdapter(getContext(), profileAdapterList);
        recyclerView.setAdapter(profileAdapter);

        loadProfile();
        loadTugasData();

//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            // Simulasi Refresh dengan Delay 2 Detik
//            new Handler().postDelayed(() -> {
//                // Perbarui Data
//                loadTugasData();
//                // Hentikan Refreshing
//                swipeRefreshLayout.setRefreshing(false);
//            }, 2000);
//        });
        profileAdapter.setOnTugasClickListener(tugasId -> {
            ViewTugasFragment profileFragment = new ViewTugasFragment();

            // Kirim userId ke ProfileFragment
            Bundle bundle = new Bundle();
            bundle.putInt("tugas_id", tugasId);
            profileFragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity) getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, profileFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        return binding.getRoot();
    }

    private void loadProfile() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        apiService.getOtherProfile(token, userId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Buat JSONObject dari response menggunakan Gson untuk mengkonversi Object ke JSON string
                        JSONObject responseObject = new JSONObject(new Gson().toJson(response.body()));

                        // Ambil objek user
                        JSONObject userObject = responseObject.getJSONObject("user");

                        // Menampilkan nama pengguna dan bio
                        binding.tvUsername.setText(userObject.getString("name"));
                        binding.tvBio.setText(userObject.getString("profileBio"));

                        // Mengambil level dan menampilkannya
                        double level = userObject.getDouble("level");
                        int levelInt = (int) Math.round(level); // Pembulatan level
                        binding.level.setText("Level " + levelInt);

                        // Menampilkan gambar profil
                        Glide.with(getContext())
                                .load(userObject.getString("profilePicture"))
                                .error(R.drawable.user_placeholder)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.profileImage);

                        // Menampilkan tanggal bergabung
                        String createdAt = userObject.optString("created_at", null);
                        if (createdAt != null && !createdAt.isEmpty()) {
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                        .parse(createdAt);
                                String year = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
                                binding.createdAccount.setText("Bergabung pada : " + year);
                            } catch (ParseException e) {
                                binding.createdAccount.setText("Tidak Diketahui");
                            }
                        } else {
                            binding.createdAccount.setText("Tidak Diketahui");
                        }

                        // Mengambil count pengikut, yang diikuti, dan tugas dari response
                        int followersCount = responseObject.getInt("followers_count");
                        int followingCount = responseObject.getInt("following_count");
                        int tasksCount = responseObject.getInt("tugas_count");

                        // Menampilkan count pada UI
                        binding.tvTugasCount.setText(String.valueOf(tasksCount));
                        binding.tvFollowingCount.setText(String.valueOf(followingCount));
                        binding.tvFollowerCount.setText(String.valueOf(followersCount));

                    } catch (JSONException e) {
                        Log.e("PARSE_ERROR", "Kesalahan parsing JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("GET_PROFILE_FAILED", "Gagal mendapatkan profil. Kode respons: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getContext(), "Masalah Jaringan :(", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loadTugasData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        apiService.getUserTugasWithId(token, userId).enqueue(new Callback<List<Tugas>>() {
            @Override
            public void onResponse(Call<List<Tugas>> call, Response<List<Tugas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileAdapterList.clear();
                    profileAdapterList.addAll(response.body());
                    profileAdapter.notifyDataSetChanged();
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

}
