package com.example.the_sobat_gapai;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Request.LoginRequest;
import com.example.the_sobat_gapai.Response.LoginResponse;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.databinding.LoginActBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    LoginActBinding binding;
    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActBinding.inflate(getLayoutInflater());
        int statusBarColor = ContextCompat.getColor(this, R.color.toolbar_dark);
        Window window = getWindow();
        window.setStatusBarColor(statusBarColor);
        setContentView(binding.getRoot());

        binding.showPassword.setOnClickListener(v -> togglePasswordVisibility());


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String token = databaseHelper.getToken();
        Log.d("Token e", "token: "+token);
        if (token != null) {
            // Token ditemukan, langsung ke MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
        }

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.formAjuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AppealActivity.class);
                startActivity(intent);
            }
        });

        binding.login.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Periksa apakah kedua input sudah terisi
                String email = binding.email.getText().toString().trim();
                String password = binding.password.getText().toString().trim();

                // Aktifkan atau nonaktifkan tombol berdasarkan input
                binding.login.setEnabled(!email.isEmpty() && !password.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        // Pasang TextWatcher ke EditText
        binding.email.addTextChangedListener(textWatcher);
        binding.password.addTextChangedListener(textWatcher);

        binding.login.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Simpan token ke SQLite
                    DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);
                    databaseHelper.clearToken(); // Bersihkan token lama jika ada
                    databaseHelper.saveToken(loginResponse.getAccess_token());
                    Log.d("Token masuk", "onResponse: " + loginResponse.getAccess_token());

                    Toast.makeText(LoginActivity.this, "Welcome " + loginResponse.getUser().getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 403) {
                    // Jika user dibanned (403 Forbidden)
                    Toast.makeText(LoginActivity.this, "Akun Anda telah dibanned. Silakan hubungi admin.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", "onResponse: " + response.message() + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.password.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            binding.showPassword.setImageResource(R.drawable.ic_eye_off);
        } else {
            binding.password.setTransformationMethod(null);
            binding.showPassword.setImageResource(R.drawable.ic_eye_on);
        }
        isPasswordVisible = !isPasswordVisible;
    }
}
