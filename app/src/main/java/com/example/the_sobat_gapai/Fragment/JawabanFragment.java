package com.example.the_sobat_gapai.Fragment;

import static com.example.the_sobat_gapai.Adapter.AnswersAdapter.subReply;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_sobat_gapai.Adapter.AnswersAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.FileUtils;
import com.example.the_sobat_gapai.Model.Answers;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Response.AnswerResponse;
import com.example.the_sobat_gapai.Response.VoteResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.TimeUtils;
import com.example.the_sobat_gapai.databinding.ActivityMainBinding;
import com.example.the_sobat_gapai.databinding.FormJawabanActBinding;
import com.example.the_sobat_gapai.databinding.MainFragmentBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JawabanFragment extends Fragment {

    private FormJawabanActBinding binding;
    private ActivityMainBinding binding_main;
    private RecyclerView recyclerView;
    private AnswersAdapter adapter;
    private List<Answers> answersList;
    private List<User> userList;
    private String subReply = null;
    private int tugasId; // ID tugas yang dikirim melalui arguments
    private Uri imageUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan View Binding
        binding = FormJawabanActBinding.inflate(inflater, container, false);
        binding_main = ActivityMainBinding.inflate(inflater, container, false);

        // Periksa izin untuk akses penyimpanan
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Jika izin belum diberikan, minta izin
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        // Ambil tugasId dari arguments (dikirim dari fragment sebelumnya)
        if (getArguments() != null) {
            tugasId = getArguments().getInt("tugas_id", -1);
        }

        binding.uploadImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100); // Memulai Activity untuk memilih gambar
        });

        binding.send.setEnabled(false);

        binding.deskripsi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu implementasi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cek apakah teks kosong
                binding.send.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu implementasi
            }
        });

        binding_main.toolbarTitle.setText("Jawaban");
        binding_main.bottomNavigation.setVisibility(View.GONE);
        // Inisialisasi RecyclerView
        recyclerView = binding.recyclerview.recyclerview;


        // Inisialisasi Adapter dan Data
        answersList = new ArrayList<>();
        adapter = new AnswersAdapter(getContext(), answersList);

        adapter.setOnReplyClickListener(username -> {
            binding.LLReply.setVisibility(View.VISIBLE);
            binding.subjectReply.setText("Balas Jawaban " + "@" + username + " ");
            subReply = username;
            binding.deskripsi.requestFocus();
        });

        binding.cancelReply.setOnClickListener(v -> {
            subReply = null;
            binding.LLReply.setVisibility(View.GONE);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Fetch data dari API
        fetchJawabanData();

        binding.send.setOnClickListener(v -> {
            if(subReply != null) {
                String description = "Membalas jawaban dari @" + subReply + " | " + binding.deskripsi.getText().toString().trim();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Panggil fungsi untuk mengirim jawaban
                postJawaban(description, imageUri);
                subReply = null;
            }else {
                String description =  binding.deskripsi.getText().toString().trim();

                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Panggil fungsi untuk mengirim jawaban
                postJawaban(description, imageUri);
            }

        });

        return binding.getRoot();
    }

    private void fetchJawabanData() {
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();

        Log.d("id", "tugasId: " + tugasId);

        Call<ResponseBody> call = apiService.getAnswers(token, tugasId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Convert response body to string
                        String jsonString = response.body().string();
                        Log.d("JawabanFragment", "Response body: " + jsonString);

                        // Parse JSON using JSONObject
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject tugasObject = jsonObject.getJSONObject("tugas");
                        JSONArray answersArray = tugasObject.getJSONArray("answers");

                        // Convert JSONArray to List<Answer>
                        List<Answers> answersList = new ArrayList<>();
                        for (int i = 0; i < answersArray.length(); i++) {
                            JSONObject answerObject = answersArray.getJSONObject(i);
                            Answers answer = new Answers();

                            answer.setId(answerObject.getInt("id"));
                            answer.setUserId(answerObject.getInt("user_id"));
                            answer.setTugasId(answerObject.getInt("tugas_id"));
                            answer.setDescription(answerObject.getString("description"));
                            answer.setImageAnswer(answerObject.getString("imageAnswer"));
                            answer.setLikes(Integer.parseInt(answerObject.getString("likes")));
                            answer.setLikes(Integer.parseInt(answerObject.getString("dislikes")));
                            String createdAt = answerObject.getString("created_at");
                            answer.setCreatedAt(TimeUtils.getRelativeTime(createdAt));
                            answer.setUpdatedAt(answerObject.getString("updated_at"));

                            // Parse user object
                            if (answerObject.has("user")) {
                                JSONObject userObject = answerObject.getJSONObject("user");
                                // Inisialisasi objek User
                                User user = new User();
                                user.setId(userObject.getInt("id")); // Menambahkan ID pengguna
                                user.setName(userObject.getString("name")); // Nama pengguna
                                user.setEmail(userObject.getString("email")); // Email pengguna
                                user.setProfilePicture(userObject.getString("profilePicture")); // Gambar profil pengguna
                                user.setProfileBio(userObject.getString("profileBio")); // Bio pengguna
                                user.setFollowed(false); // Misalnya, default tidak diikuti (bisa diatur dari data server jika tersedia)
                                user.setCreated_at(userObject.getString("created_at")); // Tanggal pembuatan


                                // Set user ke answer
                                answer.setUser(user);
                            }

                            answersList.add(answer);
                        }

                        if (adapter != null) {
                            adapter.updateData(answersList);
                        }


                    } catch (Exception e) {
                        Log.e("JawabanFragment", "Error parsing JSON: " + e.getMessage());
                        Context context = getContext();
                        if (context != null) {
                            Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("JawabanFragment", "Context is null, unable to show Toast");
                        }
                    }
                } else {
                    Context context = getContext();
                    if (context != null) {
                        Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("JawabanFragment", "Context is null, unable to show Toast");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Context context = getContext();
                if (context != null) {
                    Toast.makeText(getActivity(), "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("JawabanFragment", "Context is null, unable to show Toast");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Hindari memory leak
    }


    @Override
    public void onResume() {
        super.onResume();

        // Atur kembali tombol "Back" jika pengguna kembali ke Fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Tampilkan kembali Toolbar dan BottomNavigationView
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    Toolbar toolbar = activity.findViewById(R.id.toolbar);
                    View bottomNavigation = activity.findViewById(R.id.bottom_navigation);

                    if (toolbar != null) {
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    if (bottomNavigation != null) {
                        bottomNavigation.setVisibility(View.VISIBLE);
                    }
                }

                // Pop back stack
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void postJawaban(String description, Uri imageUri) {
        binding.LLReply.setVisibility(View.GONE);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
        int userId = databaseHelper.getUserId();

        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        // Validasi deskripsi
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek apakah gambar disertakan
        MultipartBody.Part imagePart = null; // Default null untuk kasus tanpa gambar
        if (imageUri != null) {
            try {
                String filePath = FileUtils.getPathFromURI(getContext(), imageUri);
                if (filePath != null) {
                    File file = new File(filePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(imageUri)), file);
                    imagePart = MultipartBody.Part.createFormData("imageAnswer", file.getName(), requestBody);
                    displayUploadedImage(imageUri); // Tampilkan gambar yang akan diunggah
                } else {
                    throw new IOException("Gagal mendapatkan jalur file");
                }
            } catch (IOException e) {
                Log.e("JawabanFragment", "Error mendapatkan file: " + e.getMessage());
                Toast.makeText(getContext(), "Gagal memproses file", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Log.d("JawabanFragment", "Mengirim tanpa gambar...");
        }

        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody tugasIdField = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(tugasId));
        RequestBody userIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        Log.d("TAG", "Mengirim permintaan ke API...");

        // Panggil API dengan atau tanpa gambar
        Call<ResponseBody> call;
        if (imagePart != null) {
            call = apiService.postAnswer(token, "application/json", tugasId, tugasIdField, userIdRequestBody, descriptionPart, imagePart);
        } else {
            call = apiService.postAnswerNotWithImage(token, "application/json", tugasId, tugasIdField, userIdRequestBody, descriptionPart);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "Jawaban berhasil diunggah");
                    Toast.makeText(getContext(), "Jawaban Berhasil Diupload", Toast.LENGTH_SHORT).show();
                    binding.uploadedImage.setVisibility(View.GONE);
                    binding.deskripsi.setText("");
                    fetchJawabanData();

                    updateUserLevel();
                } else {
                    Log.d("TAG", "Gagal mengunggah jawaban: " + response.code());
                    Toast.makeText(getContext(), "Jawaban Gagal Diupload", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserLevel() {
        // Ambil userId dari DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        int userId = databaseHelper.getUserId();

        if (userId == -1) {
            Log.e("TAG", "User ID tidak ditemukan");
            return;
        }

        // Ambil token dari DatabaseHelper
        String token = "Bearer " + databaseHelper.getToken();

        // Buat data level yang diperbarui
        Map<String, Integer> levelData = new HashMap<>();
        levelData.put("level", 25); // Tambahkan level sebesar 25

        // Panggil API untuk update level
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<ResponseBody> call = apiService.updateUser(token, "application/json", userId, levelData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "Level pengguna berhasil diperbarui di server");
                    Toast.makeText(getContext(), "Level XP Anda bertambah 25!", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("TAG", "Gagal memperbarui level pengguna di server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "Error: " + t.getMessage());
            }
        });
    }

    // Fungsi untuk menampilkan gambar yang diupload
    private void displayUploadedImage(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(getContext())
                    .load(imageUri)
                    .into(binding.uploadedImage);  // Binding untuk ImageView yang kamu tambahkan di XML layout
            binding.uploadedImage.setVisibility(View.VISIBLE); // Pastikan gambar muncul setelah diupload
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        if (contentUri == null) {
            Log.e("JawabanFragment", "URI null");
            return null;
        }
        Cursor cursor = null;
        String filePath = null;
        try {
            // Pastikan context tidak null
            Context context = getContext();
            if (context == null) {
                Log.e("JawabanFragment", "Context null");
                return null;
            }

            // Query content resolver untuk mendapatkan path file
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            } else {
                Log.e("JawabanFragment", "Cursor null atau tidak ada data");
            }
        } catch (Exception e) {
            Log.e("JawabanFragment", "Error mendapatkan path: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Tutup cursor untuk menghindari memory leak
            }
        }
        return filePath;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Tampilkan gambar di ImageView
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .into(binding.uploadedImage);

                binding.uploadedImage.setVisibility(View.VISIBLE);

                // Dapatkan path file menggunakan getRealPathFromURI
                String filePath = getRealPathFromURI(selectedImageUri);
                if (filePath != null) {
                    Log.d("JawabanFragment", "File path: " + filePath);
                    imageUri = selectedImageUri;
                } else {
                    Toast.makeText(requireContext(), "Gagal mendapatkan path gambar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }


}