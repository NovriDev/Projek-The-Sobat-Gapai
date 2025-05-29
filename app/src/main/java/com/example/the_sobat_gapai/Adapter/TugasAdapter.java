package com.example.the_sobat_gapai.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Fragment.JawabanFragment;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Request.FavoriteRequest;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.TugasViewHolder> {
    Context context;
    private OnFavoriteClickListener onFavoriteClickListener;
    private List<Tugas> tugasList;
    private OnProfileClickListener onProfileClickListener;

    public TugasAdapter(Context context, List<Tugas> tugasList) {
        this.context = context;
        this.tugasList = tugasList;
    }


    @NonNull
    @Override
    public TugasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new TugasViewHolder(view);
    }
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.onProfileClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull TugasViewHolder holder, int position) {
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String token = "Bearer " + databaseHelper.getToken();
        Tugas tugas = tugasList.get(position);
        holder.tvUsername.setText(tugas.getUser().getName());
        holder.tvKeterangan.setText(tugas.getKeterangan());
        holder.tvMapel.setText(tugas.getMapel());
        holder.tvSubUsername.setText(tugas.getUser().getName());
        holder.tvDeskripsi.setText(tugas.getDeskripsi());
        holder.tvLikeCount.setText(String.valueOf(tugas.getJmlLikes()));

        String createdAt = tugas.getCreatedAt();

        Log.d("CreatedAt", "onBindViewHolder: " + createdAt);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            // Parse tanggal dari server dan ubah formatnya
            Date date = inputFormat.parse(createdAt);
            String formattedDate = outputFormat.format(date);

            // Set hasil ke TextView
            holder.tvWaktu.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvWaktu.setText("Invalid date"); // Tangani error jika parsing gagal
        }

        // Set profile image
        Glide.with(holder.itemView.getContext())
                .load(tugas.getUser().getProfilePicture()) // Gambar profileImage
                .error(R.drawable.user_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage);

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onProfileClickListener != null) {
                    onProfileClickListener.onProfileClick(tugas.getUser().getId());
                }
            }
        });

        // Set post image (tugas.getImageTugas() adalah URL atau path gambar)
        Glide.with(holder.itemView.getContext())
                .load(tugas.getImageTugas()) // Gambar post
                .error(R.drawable.img_mapel_placeholder)
                .into(holder.postImage);

        // Validasi status favorit dari server saat view diinisialisasi
        apiService.getFavoriteStatus(token, tugas.getId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body().get("isFavorite").getAsBoolean();
                    tugas.setFavorite(isFavorite); // Sinkronisasi status favorit di model
                    holder.favorite.setImageResource(isFavorite ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite);

                    // Sinkronisasi jumlah like jika tersedia
                    int serverLikes = response.body().has("jml_likes") ? response.body().get("jml_likes").getAsInt() : tugas.getJmlLikes();
                    tugas.setJmlLikes(serverLikes); // Update jumlah like di model
                    holder.tvLikeCount.setText(String.valueOf(serverLikes));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("FAVORITE_ERROR", "Kesalahan jaringan: " + t.getMessage());
            }
        });

// Listener untuk klik tombol favorit
        holder.favorite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !tugas.isFavorite(); // Toggle status
            tugas.setFavorite(newFavoriteStatus); // Update status di model

            // Update ikon favorit berdasarkan status baru
            holder.favorite.setImageResource(newFavoriteStatus ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite);

            // Update jumlah like secara lokal
            int updatedLikes = newFavoriteStatus ? tugas.getJmlLikes() + 1 : tugas.getJmlLikes() - 1;
            tugas.setJmlLikes(updatedLikes); // Perbarui jumlah like di model
            holder.tvLikeCount.setText(String.valueOf(updatedLikes)); // Perbarui UI

            // Kirim permintaan ke server untuk sinkronisasi
            if (newFavoriteStatus) {
                apiService.addFavorite(token, new FavoriteRequest(tugas.getId())).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e("FAVORITE_ERROR", "Gagal menambah favorit: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("FAVORITE_ERROR", "Kesalahan jaringan: " + t.getMessage());
                    }
                });
            } else {
                apiService.removeFavorite(token, tugas.getId()).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e("UNLIKE_ERROR", "Gagal menghapus favorit: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("UNLIKE_ERROR", "Kesalahan jaringan: " + t.getMessage());
                    }
                });
            }
        });

        holder.btnJawab.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;

                // Menghilangkan Toolbar dan BottomNavigationView
                Toolbar toolbar = activity.findViewById(R.id.toolbar);
                View bottomNavigation = activity.findViewById(R.id.bottom_navigation);

                if (toolbar != null) {
                    toolbar.setVisibility(View.GONE);
                }
                if (bottomNavigation != null) {
                    bottomNavigation.setVisibility(View.GONE);
                }

                // Inisialisasi FrameLayout
                FrameLayout frameLayout = activity.findViewById(R.id.jawaban_container);
                if (frameLayout == null) {
                    Log.e("TugasAdapter", "FrameLayout dengan ID 'jawaban_container' tidak ditemukan.");
                    return; // Jangan lanjutkan jika frameLayout null
                }

                frameLayout.setVisibility(View.VISIBLE);

                // Membuat instance Fragment tujuan
                JawabanFragment jawabanFragment = new JawabanFragment();

                // Anda bisa mengirim data melalui Bundle jika diperlukan
                Bundle bundle = new Bundle();
                bundle.putInt("tugas_id", tugas.getId()); // Menyimpan ID jawaban
                jawabanFragment.setArguments(bundle);

                // Pindahkan ke fragment dengan FragmentTransaction
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.jawaban_container, jawabanFragment) // Pastikan ID jawaban_container benar
                        .addToBackStack(null) // Menambahkan ke back stack agar bisa kembali
                        .commit();
            }
        });

        holder.tvShare.setOnClickListener(v -> {
            String shareText = tugas.getDeskripsi();
            String imageUrl = tugas.getImageTugas();
            shareContent(context, shareText, imageUrl);
        });

        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                String token = "Bearer " + databaseHelper.getToken();
                Log.d("ReportDialog", "Report button clicked.");

                try {
                    // Buat dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_report_task, null);
                    String userId = String.valueOf(tugas.getUser().getId());
                    String tugasId = String.valueOf(tugas.getId());
                    EditText titleReport = dialogView.findViewById(R.id.etReportTitle);
                    RadioButton radioButton = dialogView.findViewById(R.id.rbReason1);
                    radioButton.setText("Gambar tugas tidak sesuai dengan materi");
                    titleReport.setText("Laporkan Tugas");
                    dialogView.findViewById(R.id.etReportTitle).setEnabled(false);

                    builder.setView(dialogView);
                    Log.d("ReportDialog", "Dialog view inflated successfully.");

                    // Inisialisasi view dari dialog
                    EditText etReportTitle = dialogView.findViewById(R.id.etReportTitle);
                    RadioGroup radioGroupReasons = dialogView.findViewById(R.id.radioGroupReasons);
                    ImageView cancel = dialogView.findViewById(R.id.cancel_button_dialog);
                    Button btnSubmitReport = dialogView.findViewById(R.id.btnSubmitReport);


                    // Buat dan tampilkan dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.setCancelable(false);
                    Log.d("ReportDialog", "Dialog displayed successfully.");

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Log.d("ReportDialog", "Dialog dismissed by user.");
                        }
                    });

                    // Atur aksi tombol Laporkan
                    btnSubmitReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("ReportDialog", "Submit button clicked.");

                            String title = etReportTitle.getText().toString().trim();
                            Log.d("ReportDialog", "Report title: " + title);
                            etReportTitle.setEnabled(false);

                            // Ambil alasan dari RadioGroup
                            int selectedId = radioGroupReasons.getCheckedRadioButtonId();
                            if (selectedId == -1) {
                                Log.d("ReportDialog", "No reason selected.");
                                Toast.makeText(context, "Harap pilih alasan laporan!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Ambil teks alasan
                            RadioButton selectedReason = dialogView.findViewById(selectedId);
                            String description = selectedReason.getText().toString();
                            Log.d("ReportDialog", "Report reason: " + description);

                            // Kirim data ke server menggunakan API
                            uploadReport(token, userId, tugasId, title, description);
                            Log.d("ReportDialog", "Data sent to server: Token=" + token + ", UserId=" + userId + ", Title=" + title + ", Description=" + description);
                        }
                    });
                } catch (Exception e) {
                    Log.e("ReportDialog", "Error in dialog setup: " + e.getMessage(), e);
                }
            }
        });
    }

    private void uploadReport(String token, String userId, String tugasId, String title, String description) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        token = "Bearer " + databaseHelper.getToken();

        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody tugasIdPart = RequestBody.create(MediaType.parse("text/plain"), tugasId);
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<ResponseBody> call = apiService.postReportTugas(token, userIdPart, tugasIdPart,titlePart, descriptionPart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Laporan berhasil dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ReportDialog", "API error: " + response.code() + " " + response.message());
                    Toast.makeText(context, "Gagal mengirim laporan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ReportDialog", "API call failed: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTugasList(List<Tugas> newTugasList) {
        tugasList = newTugasList;
        notifyDataSetChanged();
    }

    public static void shareContent(Context context, String text, String imageUrl) {
        // Menyimpan gambar terlebih dahulu
        new DownloadImageTask(context, text).execute(imageUrl);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Uri> {
        private Context context;
        private String text;

        public DownloadImageTask(Context context, String text) {
            this.context = context;
            this.text = text;
        }

        @Override
        protected Uri doInBackground(String... params) {
            String imageUrl = params[0];
            return saveImageToInternalStorage(context, imageUrl);
        }

        @Override
        protected void onPostExecute(Uri imageUri) {
            // Menyelesaikan proses berbagi setelah gambar diunduh
            if (imageUri != null) {
                shareImage(context, text, imageUri);
            } else {
                Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Uri saveImageToInternalStorage(Context context, String imageUrl) {
        try {
            // Mendapatkan InputStream dari URL gambar
            InputStream inputStream = new URL(imageUrl).openStream();

            // Membuat file di penyimpanan internal
            FileOutputStream fos = context.openFileOutput("shared_image.jpg", Context.MODE_PRIVATE);

            // Membaca gambar dari InputStream dan menulisnya ke FileOutputStream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            inputStream.close();

            // Mendapatkan file yang telah disimpan
            File file = new File(context.getFilesDir(), "shared_image.jpg");

            // Membuat URI untuk file tersebut menggunakan FileProvider
            return FileProvider.getUriForFile(context, "com.example.the_sobat_gapai.provider", file);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void shareImage(Context context, String text, Uri imageUri) {
        // Membuat Intent untuk membagikan teks dan gambar
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        // Jika gambar tersedia, tambahkan ke Intent
        if (imageUri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*"); // Tipe MIME gambar
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Berikan izin membaca file
        }

        // Menampilkan dialog untuk memilih aplikasi untuk berbagi
        Intent chooser = Intent.createChooser(shareIntent, "Bagikan tugas");
        context.startActivity(chooser);
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    static class TugasViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvKeterangan, tvLikeCount, tvWaktu, tvMapel, tvSubUsername, tvDeskripsi, tvShare, report;
        ImageView profileImage, favorite, postImage;
        Button btnJawab;


        public TugasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
            btnJawab = itemView.findViewById(R.id.btnJawab);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            tvMapel = itemView.findViewById(R.id.tv_mapel);
            profileImage = itemView.findViewById(R.id.profile_image);
            tvDeskripsi = itemView.findViewById(R.id.tv_deskripsi);
            tvSubUsername = itemView.findViewById(R.id.tv_sub_username);
            postImage = itemView.findViewById(R.id.post_image); // pastikan postImage adalah ImageView
            favorite = itemView.findViewById(R.id.favorite);
            tvShare = itemView.findViewById(R.id.tv_share);
            report = itemView.findViewById(R.id.tv_lapor);
        }
    }
    public interface OnFavoriteClickListener {
        void onFavoriteClicked();
    }

    // Interface untuk menangani klik foto profil
    public interface OnProfileClickListener {
        void onProfileClick(int userId);
    }

}