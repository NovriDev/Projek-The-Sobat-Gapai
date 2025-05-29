package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.Adapter.TransactionAdapter;
import com.example.the_sobat_gapai.Adapter.TugasAdapter;
import com.example.the_sobat_gapai.CurrencyUtils;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Transaction;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.FormSaldoBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BalanceFragment extends Fragment{
    private FormSaldoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        binding = FormSaldoBinding.inflate(getLayoutInflater());

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = databaseHelper.getToken();
        Log.d("Token e", "token: "+token);
        fetchBalance();
        binding.btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Maaf, untuk saat ini fitur ini belum tersedia", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnTarik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Maaf, untuk saat ini fitur ini belum tersedia", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnLevel.setOnClickListener(new View.OnClickListener() {
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

    private void fetchBalance() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        RetrofitClient.getApiService().create(ApiService.class).getProfile(token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Mengambil data profil user dari response
                        JSONObject userObject = new JSONObject(new Gson().toJson(response.body()))
                                .getJSONObject("user");
                        // Menampilkan data saldo
                        String formattedBalance = CurrencyUtils.formatRupiah(Double.parseDouble(userObject.getString("balance")));
                        binding.totalSaldo.setText(userObject.getString(formattedBalance));
                    } catch (JSONException e) {
                        Log.e("PARSE_ERROR", "Kesalahan parsing JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("GET_PROFILE_FAILED", "Gagal mendapatkan saldo. Kode respons: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e("GET_PROFILE_ERROR", "Kesalahan jaringan: " + t.getMessage());
                Toast.makeText(getContext(), "Masalah Jaringan :(", Toast.LENGTH_LONG).show();
            }
        });
    }/*
    private void fetchTransactions() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        // Retrofit client
        Retrofit retrofit = RetrofitClient.getApiService();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getTransactions(token).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()) {
                    // Set the transaction data to the list
                    transactionList.clear();
                    transactionList.addAll(response.body());
                    transactionAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching transactions: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

}
