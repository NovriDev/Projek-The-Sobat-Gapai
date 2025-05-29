package com.example.the_sobat_gapai.Fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.LoginActivity;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.FormProfileBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FormProfileBinding binding;
    private SpinKitView spinKitView;
    private User user = new User();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FormProfileBinding.inflate(inflater, container, false);

        // Atur UI awal
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_corner);
        binding.levelUser.setBackground(drawable);
        spinKitView = binding.spinner;

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.parseColor("#262627"), Color.parseColor("#1B47FA"));
        colorAnimator.setDuration(2000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.addUpdateListener(animation -> drawable.setColor((int) animation.getAnimatedValue()));
        colorAnimator.start();

        fetchProfileUser();

        binding.btnTtgApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TentangAppFragment fragmentA = new TentangAppFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment fragmentA = new EditProfileFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        binding.btnFriendFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFriendFragment fragmentA = new MyFriendFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnMyTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTugasFragment fragmentA = new MyTugasFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalanceFragment fragmentA = new BalanceFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserStatusFragment fragmentA = new UserStatusFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentA)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return binding.getRoot();
    }

    private void fetchProfileUser() {
        spinKitView.setVisibility(View.VISIBLE); // Menampilkan spinner loading
        binding.btnTtgApp.setEnabled(false);
        binding.btnLogout.setEnabled(false);
        binding.btnFriendFollowers.setEnabled(false);
        binding.btnBalance.setEnabled(false);
        binding.btnRecord.setEnabled(false);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        RetrofitClient.getApiService().create(ApiService.class).getProfile(token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                spinKitView.setVisibility(View.GONE); // Menyembunyikan spinner loading
                binding.btnTtgApp.setEnabled(true);
                binding.btnLogout.setEnabled(true);
                binding.btnFriendFollowers.setEnabled(true);
                binding.btnBalance.setEnabled(true);
                binding.btnRecord.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Mengambil data profil user dari response
                        JSONObject userObject = new JSONObject(new Gson().toJson(response.body()))
                                .getJSONObject("user");

                        // Menampilkan data profil
                        binding.username.setText(userObject.getString("name"));
                        double level = userObject.getDouble("level"); // Ambil nilai level sebagai double
                        int levelInt = (int) Math.round(level); // Konversi ke integer (pembulatan)

                        binding.levelUser.setText("Level " + levelInt);
                        Glide.with(getContext())
                                .load(userObject.getString("profilePicture")) // Gambar profileImage
                                .error(R.drawable.user_placeholder)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.ppUser); // Menampilkan gambar profil

                        // Menampilkan tanggal bergabung
                        String createdAt = userObject.getString("created_at");
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
                    } catch (JSONException e) {
                        Log.e("PARSE_ERROR", "Kesalahan parsing JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("GET_PROFILE_FAILED", "Gagal mendapatkan profil. Kode respons: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e("GET_PROFILE_ERROR", "Kesalahan jaringan: " + t.getMessage());
                Toast.makeText(getContext(), "Masalah Jaringan :(", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void logout() {
        // Membuat Dialog Konfirmasi
        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                    String token = "Bearer " + databaseHelper.getToken();

                    ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
                    apiService.logout(token).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Hapus token dari SharedPreferences
                                DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
                                databaseHelper.clearToken();
                                // Arahkan ke halaman login
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish(); // Menutup aktivitas saat ini
                            } else {
                                Log.e("LOGOUT_FAILED", "Gagal logout. Kode respons: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Log.e("LOGOUT_ERROR", "Kesalahan jaringan: " + t.getMessage());
                        }
                    });
                    // Arahkan ke halaman login
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish(); // Menutup aktivitas saat ini
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Jika No, tutup dialog
                    dialog.dismiss();
                })
                .setCancelable(false) // Tidak bisa keluar tanpa memilih Yes/No
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }
}
