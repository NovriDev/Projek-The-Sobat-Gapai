package com.example.the_sobat_gapai.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.Adapter.TugasAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.FileUtils;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Request.UpdateProfileRequest;
import com.example.the_sobat_gapai.Response.UpdateProfileResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.FormEditProfileBinding;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private FormEditProfileBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
//    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        binding = FormEditProfileBinding.inflate(getLayoutInflater());

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setEnabled(false);

//        binding.ppUser.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, 100); // Memulai Activity untuk memilih gambar
//        });


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = databaseHelper.getToken();
        Log.d("Token e", "token: "+token);
        fetchProfileUser();

        binding.btnUpdate.setOnClickListener(v -> {
            updateProfile();
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        return binding.getRoot();
    }

//    private void uploadImage() {
//        String name = binding.username.getText().toString();
//        String email = binding.email.getText().toString();
//        String password = binding.password.getText().toString();
//        String profileBio = binding.bio.getText().toString();
//
//        if (imageUri == null) {
//            Toast.makeText(getContext(), "Gambar belum dipilih", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        MultipartBody.Part profilePicture = null;
//        if (imageUri != null) {
//            String filePath = FileUtils.getPath(getContext(), imageUri);
//            if (filePath != null) {
//                File file = new File(filePath);
//                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//                profilePicture = MultipartBody.Part.createFormData("profilePicture", file.getName(), requestFile);
//            } else {
//                Toast.makeText(getContext(), "Gagal memproses file", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        // Menyiapkan RequestBody untuk data selain gambar (teks)
//        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
//        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
//        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
//        RequestBody bioBody = RequestBody.create(MediaType.parse("text/plain"), profileBio);
//
//        // Menyiapkan token untuk autentikasi
//        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
//        String token = "Bearer " + databaseHelper.getToken();
//
//        // Membuat API service
//        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
//
//        // Menjalankan request API untuk update profil dengan gambar
//        Call<UpdateProfileResponse> call = apiService.updateProfileWithImage(token, "application/json", nameBody, emailBody, passwordBody, bioBody, profilePicture);
//
//        call.enqueue(new Callback<UpdateProfileResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<UpdateProfileResponse> call, @NonNull Response<UpdateProfileResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.d("UPLOAD_SUCCESS", "Gambar berhasil dikirim ke server");
//                    String responseJson = new Gson().toJson(response.body());
//                    Log.d("UPLOAD_RESPONSE_JSON", "Response JSON: " + responseJson);
//
//                    // Menangani response tanpa JSON parsing
//                    UpdateProfileResponse updateProfileResponse = response.body();
//                    if (updateProfileResponse != null && updateProfileResponse.getUser() != null) {
//                        binding.username.setText(updateProfileResponse.getUser().getName());
//                        binding.email.setText(updateProfileResponse.getUser().getEmail());
//                        binding.bio.setText(updateProfileResponse.getUser().getProfileBio());
//
//                        // Menampilkan URL gambar jika ada
//                        String profilePicture = updateProfileResponse.getUser().getProfilePicture();
//                        if (profilePicture != null) {
//                            Log.d("UPLOAD_IMAGE_URL", "URL gambar di server: " + profilePicture);
//                        }
//                    }
//                } else {
//                    Log.e("UPLOAD_FAILED", "Gagal upload gambar. Kode respons: " + response.code());
//                    try {
//                        Log.e("ERROR_BODY", "Error Body: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
//                Log.e("UPLOAD_ERROR", "Kesalahan jaringan: " + t.getMessage());
//                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void startUCrop(Uri sourceUri) {
//        // Tentukan lokasi dan nama file untuk gambar yang sudah dipotong
//        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));
//
//        // Konfigurasi uCrop
//        UCrop.Options options = new UCrop.Options();
//        options.setCompressionQuality(90); // Kualitas kompresi gambar (0-100)
//        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.white)); // Warna toolbar
//        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.black)); // Warna status bar
//        options.setToolbarTitle("Potong Gambar"); // Judul toolbar
//
//        // Mulai uCrop
//        UCrop.of(sourceUri, destinationUri)
//                .withAspectRatio(1, 1) // Rasio aspek (1:1 untuk persegi)
//                .withMaxResultSize(1080, 1080) // Ukuran maksimal hasil
//                .withOptions(options)
//                .start(requireContext(), this);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
//            Uri selectedImageUri = data.getData();
//            if (selectedImageUri != null) {
//                Log.d("UploadFragment", "URI gambar dipilih: " + selectedImageUri);
//                startUCrop(selectedImageUri);
//            } else {
//                Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
//            final Uri resultUri = UCrop.getOutput(data);
//            if (resultUri != null) {
//                Log.d("UploadFragment", "URI gambar setelah dipotong: " + resultUri);
//                Glide.with(requireContext())
//                        .load(resultUri)
//                        .into(binding.ppUser);
//                imageUri = resultUri;
//            } else {
//                Toast.makeText(requireContext(), "Gagal memotong gambar", Toast.LENGTH_SHORT).show();
//            }
//        } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
//            final Throwable cropError = UCrop.getError(data);
//            if (cropError != null) {
//                Log.e("UploadFragment", "uCrop Error: " + cropError.getMessage());
//                Toast.makeText(requireContext(), "Gagal memotong gambar: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    private void fetchProfileUser() {
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

                        // Menampilkan data profil
                        binding.username.setText(userObject.getString("name"));
                        binding.email.setText(userObject.getString("email"));
                        binding.bio.setText(userObject.getString("profileBio"));

                        Glide.with(getContext())
                                .load(userObject.getString("profilePicture")) // Gambar profileImage
                                .error(R.drawable.user_placeholder)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.ppUser); // Menampilkan gambar profil

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
    private void updateProfile() {
        String username = binding.username.getText().toString();
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String bio = binding.bio.getText().toString();
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        UpdateProfileRequest request = new UpdateProfileRequest(username, email, password, bio);
        request.setName(username);
        request.setEmail(email);
        if (password.isEmpty()) {
            request.setPassword(null);
        }
        request.setProfileBio(bio);


        Call<UpdateProfileResponse> call = apiService.updateProfile(token, request);
        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    Toast.makeText(getContext(), "Profile Berhasil Di-update", Toast.LENGTH_SHORT).show();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new ProfileFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("TAG", "Error Response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                // Handle failure
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

}
