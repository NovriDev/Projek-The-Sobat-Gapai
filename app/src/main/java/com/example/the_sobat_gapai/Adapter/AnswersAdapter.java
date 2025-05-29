package com.example.the_sobat_gapai.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Answers;
import com.example.the_sobat_gapai.Model.Gift;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Request.VoteRequest;
import com.example.the_sobat_gapai.Response.VoteResponse;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    private final Context context;
    private OnReplyClickListener replyClickListener;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private final List<Answers> answersList;
    public static String subReply;
    // Constructor
    public AnswersAdapter(Context context, List<Answers> answersList) {
        this.context = context;
        this.answersList = answersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Answers item = answersList.get(position);


        // Set user profile image
        if (item.getUser() != null && item.getUser().getProfilePicture() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getUser().getProfilePicture()) // Gambar profileImage
                    .error(R.drawable.user_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ppUser);
        }


        holder.likes.setText(String.valueOf(item.getLikes()));

        // Set username
        if (item.getUser() != null && item.getUser().getName() != null) {
            holder.username.setText(item.getUser().getName());
        } else {
            holder.username.setText("Anonim");
        }

        holder.reply.setOnClickListener(v -> {
            String username = item.getUser().getName();
            if (replyClickListener != null) {
                replyClickListener.onReplyClicked(username);
            }
        });

        holder.cancelReply.setOnClickListener(v -> {
            holder.llReply.setVisibility(View.GONE);
        });

        // Set comment/message

        holder.gift.setOnClickListener(v -> showCustomGiftDialog());

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
                    String userId = String.valueOf(item.getUserId());
                    String answerId = String.valueOf(item.getId());
                    String tugasId = String.valueOf(item.getTugasId());
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
                            uploadReport(token, userId, tugasId, answerId, title, description);
                            Log.d("ReportDialog", "Data sent to server: Token=" + token + ", UserId=" + userId + ", AnswerId=" + answerId + ", Title=" + title + ", Description=" + description);
                        }
                    });
                } catch (Exception e) {
                    Log.e("ReportDialog", "Error in dialog setup: " + e.getMessage(), e);
                }
            }
        });


        if (item.getImageAnswer() != null && !item.getImageAnswer().isEmpty()) {
            holder.contentImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImageAnswer())
                    .apply(RequestOptions.centerCropTransform())
                    .error(R.drawable.error_answer_img)
                    .into(holder.contentImage);

            // Tambahkan klik listener untuk memperbesar gambar
            holder.contentImage.setOnClickListener(v -> showImageDialog(item.getImageAnswer()));
        } else {
            holder.contentImage.setVisibility(View.GONE);
        }


        holder.textMessage.setText(formatMessage(item.getDescription() != null ? item.getDescription() : "Tidak ada deskripsi"));

        // Set time
        holder.textTime.setText(item.getCreatedAt() != null ? item.getCreatedAt() : "-");

        // Get userVote as JsonObject
        JsonObject userVote = item.getUserVote(); // Assumes this method returns a JsonObject

        // Cek apakah user sudah memberikan vote
        if (userVote != null && "like".equals(userVote.get("userVote").getAsString())) {
            holder.btnLike.setEnabled(false); // Disable like button
            holder.btnLike.setAlpha(0.5f); // Optionally, set the button's opacity to indicate it's disabled
        }

        // Handle like button click
        holder.btnLike.setOnClickListener(v -> {
            sendVote(item.getUserId(), item.getId(), "like");
        });
        getVotes(holder, item.getId());
    }

    private void showCustomGiftDialog() {
        // Buat daftar gift
        List<Gift> giftList = new ArrayList<>();
        giftList.add(new Gift("Gift 1", "Rp 100.000", R.drawable.gift1)); // Gambar dari resource
        giftList.add(new Gift("Gift 2", "Rp 200.000", R.drawable.gift2));
        giftList.add(new Gift("Gift 3", "Rp 300.000", R.drawable.gift3));

        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_gift, null);

        // Temukan ListView dan set adapter
        ListView listViewGifts = dialogView.findViewById(R.id.listViewGifts);
        GiftAdapter adapter = new GiftAdapter(context, giftList);
        listViewGifts.setAdapter(adapter);

        // Buat AlertDialog dengan custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Buat dialog
        AlertDialog dialog = builder.create();

        // Handle klik pada item ListView
        listViewGifts.setOnItemClickListener((parent, view, position, id) -> {
            Gift selectedGift = giftList.get(position);
            Toast.makeText(context, "Anda memilih: " + selectedGift.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Tutup dialog setelah memilih
        });

        // Handle tombol Batal
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Tampilkan dialog
        dialog.show();
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_image);

        ImageView imageView = dialog.findViewById(R.id.fullImageView);
        Button downloadButton = dialog.findViewById(R.id.downloadButton);
        Log.d("Gambar", "showImageDialog: " + imageUrl);

        // Tampilkan gambar dalam ukuran penuh
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.centerInsideTransform())
                .error(R.drawable.error_answer_img) // Pastikan drawable ini ada
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GlideError", "Error loading image: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideSuccess", "Image loaded successfully.");
                        return false;
                    }
                })
                .into(imageView);


        // Tambahkan logika untuk tombol unduh
        downloadButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadImage(imageUrl);
                } else {
                    requestStoragePermission();
                }
            } else {
                downloadImage(imageUrl);
            }
        });

        dialog.show();
    }

    private void downloadImage(String imageUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Downloaded_Image.jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                Toast.makeText(context, "Gambar berhasil diunduh", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Gunakan DownloadManager untuk Android 9 atau lebih rendah
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                Uri uri = Uri.parse(imageUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                downloadManager.enqueue(request);

                Toast.makeText(context, "Gambar sedang diunduh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadReport(String token, String userId, String answerId, String tugasId, String title, String description) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        token = "Bearer " + databaseHelper.getToken();

        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody tugasIdPart = RequestBody.create(MediaType.parse("text/plain"), tugasId);
        RequestBody answerIdPart = RequestBody.create(MediaType.parse("text/plain"), answerId);
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        Call<ResponseBody> call = apiService.postReportAnswer(token, userIdPart, tugasIdPart, answerIdPart,titlePart, descriptionPart);

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

    private CharSequence formatMessage(String message) {
        SpannableString spannableString = new SpannableString(message);

        // Cari bagian "Membalas jawaban dari" dan beri style bold jika ditemukan
        String target = "Membalas jawaban dari";
        int startIndex = message.indexOf(target);

        if (startIndex != -1) {
            // Jika "Membalas jawaban dari" ditemukan, beri style bold
            int endIndex = startIndex + target.length();
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // Jika tidak ditemukan, kembalikan message asli tanpa perubahan
            return message;
        }

        return spannableString;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
        }
    }



    private void sendVote(int userId, int answerId, String type) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String token = "Bearer " + databaseHelper.getToken();
        // Retrofit Client
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        // Membuat request body
        VoteRequest voteRequest = new VoteRequest(userId, answerId, type);

        // Memanggil API
        apiService.sendVote(token, voteRequest).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Vote berhasil dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    // Menggunakan Gson untuk log response error
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Log.e("VoteError", "Error Response: " + errorJson);

                            // Parsing dengan Gson untuk melihat lebih detail (opsional)
                            Gson gson = new Gson();
                            JsonObject errorObject = gson.fromJson(errorJson, JsonObject.class);
                            Log.e("ParsedError", "Parsed Response: " + errorObject);
                        }
                    } catch (Exception e) {
                        Log.e("VoteError", "Gagal membaca response error: " + e.getMessage());
                    }
                    Toast.makeText(context, "Anda sudah memberi Like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VoteFailure", "Terjadi kesalahan: " + t.getMessage());
                Toast.makeText(context, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyClickListener = listener;
    }

    public void updateData(List<Answers> newAnswers) {
        this.answersList.clear();
        this.answersList.addAll(newAnswers);
        notifyDataSetChanged();
    }


    private void getVotes(ViewHolder holder, int tugasId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String token = "Bearer " + databaseHelper.getToken();

        // Retrofit Client
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        apiService.getVotes(token, tugasId).enqueue(new Callback<VoteResponse>() {
            @Override
            public void onResponse(Call<VoteResponse> call, Response<VoteResponse> response) {
                if (response.isSuccessful()) {
                    // Data berhasil diterima
                    VoteResponse voteResponse = response.body();

                    // Menampilkan likes dan dislikes pada TextView
                    holder.likes.setText(String.valueOf(voteResponse.getLikes()));

                    // Kembalikan status awal pada tombol atau elemen lainnya, misalnya:
                    holder.btnLike.setEnabled(true);  // Mengaktifkan tombol like
                    holder.btnLike.setAlpha(1.0f);  // Mengembalikan opacity tombol

                } else {
                    // Menangani error pada respons
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();

                            // Log seluruh error response untuk debugging
                            Log.e("GetVotesError", "Full Error Response: " + errorJson);

                            // Mengatur Gson untuk menerima JSON yang malformat
                            Gson gson = new GsonBuilder()
                                    .setLenient()  // Enable lenient mode
                                    .create();

                            // Menggunakan JsonReader untuk debugging isi JSON
                            JsonReader jsonReader = new JsonReader(new StringReader(errorJson));
                            jsonReader.setLenient(true);

                            try {
                                JsonObject errorObject = gson.fromJson(String.valueOf(jsonReader), JsonObject.class);
                                Log.e("ParsedError", "Parsed Response (JsonObject): " + errorObject);
                            } catch (Exception e) {
                                Log.e("ParsedError", "Error parsing JSON: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("GetVotesError", "Error while parsing response: " + e.getMessage());
                    }

                    // Menampilkan pesan gagal mendapatkan data
                    Toast.makeText(context, "Failed to get votes: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VoteResponse> call, Throwable t) {
                Log.e("GetVotesError", "Error fetching votes: " + t.getMessage());
                Toast.makeText(context, "Error fetching votes", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView fotoProfile;
        ImageView ppUser, contentImage, btnLike, cancelReply, gift;
        LinearLayout llReply;
        TextView username, textMessage, textTime, likes, report, reply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoProfile = itemView.findViewById(R.id.fotoProfile);
            ppUser = itemView.findViewById(R.id.pp_user);
            username = itemView.findViewById(R.id.username);
            llReply = itemView.findViewById(R.id.LL_reply);
            gift = itemView.findViewById(R.id.gift);
            cancelReply = itemView.findViewById(R.id.cancel_reply);
            textMessage = itemView.findViewById(R.id.textMessage);
            contentImage = itemView.findViewById(R.id.imageAnswer);
            likes = itemView.findViewById(R.id.tv_like_count);
            btnLike = itemView.findViewById(R.id.btnLike);
            report = itemView.findViewById(R.id.report);
            reply = itemView.findViewById(R.id.reply);
            textTime = itemView.findViewById(R.id.textTime);
        }
    }

    // Interface for item click events
    public interface OnReplyClickListener {
        void onReplyClicked(String username);
    }
}
