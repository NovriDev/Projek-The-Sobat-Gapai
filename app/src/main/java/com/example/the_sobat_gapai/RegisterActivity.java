package com.example.the_sobat_gapai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.the_sobat_gapai.Request.RegisterRequest;
import com.example.the_sobat_gapai.Response.RegisterResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.RegisterActBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private RegisterActBinding binding;
    private boolean isPasswordVisible = false, isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterActBinding.inflate(getLayoutInflater());
        int statusBarColor = ContextCompat.getColor(this, R.color.toolbar_dark);
        Window window = getWindow();
        window.setStatusBarColor(statusBarColor);
        setContentView(binding.getRoot());

        // Handle password visibility toggle
        binding.showPassword.setOnClickListener(v -> togglePasswordVisibility());
        binding.showConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        binding.login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        // Handle register button click
        binding.register.setOnClickListener(v -> registerUser());
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

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            binding.confirmPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            binding.showConfirmPassword.setImageResource(R.drawable.ic_eye_off);
        } else {
            binding.confirmPassword.setTransformationMethod(null);
            binding.showConfirmPassword.setImageResource(R.drawable.ic_eye_on);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    private void registerUser() {
        String email = binding.email.getText().toString();
        String name = binding.username.getText().toString();
        String password = binding.password.getText().toString();
        String confirmPassword = binding.confirmPassword.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            binding.email.setError("Email wajib diisi");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            binding.username.setError("Nama wajib diisi");
            return;
        }if (TextUtils.isEmpty(password)) {
            binding.password.setError("Password wajib diisi");
            return;
        }
        if (password.length() < 8) {
            binding.password.setError("Password harus lebih dari 8 karakter");
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.confirmPassword.setError("Password harus sama");
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(email, name, password, confirmPassword);

        // Create APIService
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        // Make the API call
        Call<RegisterResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse != null && registerResponse.isSuccess()) {
                        // Show success message
                        Toast.makeText(RegisterActivity.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // Show error message
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle API call failure
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Handle network failure
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}