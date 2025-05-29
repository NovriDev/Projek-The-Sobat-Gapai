package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.ActivityUserStatusBinding;
import com.example.the_sobat_gapai.databinding.FormSaldoBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserStatusFragment extends Fragment {
    private ActivityUserStatusBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = ActivityUserStatusBinding.inflate(inflater, container, false);

        // Panggil metode untuk memuat profil pengguna
        fetchProfileUser();

        return binding.getRoot();
    }

    private void fetchProfileUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        RetrofitClient.getApiService().create(ApiService.class).getProfile(token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject userObject = new JSONObject(new Gson().toJson(response.body()))
                                .getJSONObject("user");
                        populateUserProfile(userObject);
                    } catch (JSONException e) {
                        Log.e("PARSE_ERROR", "Kesalahan parsing JSON: " + e.getMessage());
                        showToast("Terjadi kesalahan saat memuat data pengguna.");
                    }
                } else {
                    Log.e("GET_PROFILE_FAILED", "Gagal mendapatkan profil. Kode respons: " + response.code());
                    showToast("Gagal memuat profil pengguna.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e("GET_PROFILE_ERROR", "Kesalahan jaringan: " + t.getMessage());
                showToast("Kesalahan jaringan. Periksa koneksi Anda.");
            }
        });
    }

    private void populateUserProfile(JSONObject userObject) {
        try {
            binding.tvName.setText(userObject.optString("name", "Tidak Diketahui"));

            int level = userObject.optInt("level", 0);
            binding.tvStatus.setText(getStatusByLevel(level));
            binding.imgStatus.setImageResource(getImageByLevel(level));
            int maxXp = userObject.optInt("maxXp", 500);

            binding.progressXp.setMax(maxXp);
            binding.progressXp.setProgress(level);
            binding.tvXpPoints.setText("XP: " + level + "/" + maxXp);

            Glide.with(getContext())
                    .load(userObject.optString("profilePicture"))
                    .error(R.drawable.user_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgProfile);


        } catch (Exception e) {
            Log.e("POPULATE_ERROR", "Kesalahan saat memuat data: " + e.getMessage());
        }
    }

    private String getStatusByLevel(int level) {
        if (level >= 500) return "Pakar";
        else if (level >= 400) return "Profesor";
        else if (level >= 250) return "Ahli";
        else if (level >= 100) return "Mahasiswa";
        else return "Pemula";
    }

    private int getImageByLevel(int level) {
        if (level >= 500) return R.drawable.pakar_ic;
        else if (level >= 400) return R.drawable.profesor_ic;
        else if (level >= 250) return R.drawable.ahli_ic;
        else if (level >= 100) return R.drawable.mahasiswa_ic;
        else return R.drawable.pemula_ic;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
