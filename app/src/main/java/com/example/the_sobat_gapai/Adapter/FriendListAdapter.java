package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;
    private OnProfileClickListener onProfileClickListener;

    public FriendListAdapter(Context context,List<User> users) {
        this.users = users;
        this.context = context;
    }

    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.onProfileClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameTextView.setText(user.getName());
        holder.bioTextView.setText(user.getProfileBio());
        Glide.with(holder.itemView.getContext())
                .load(user.getProfilePicture()) // Gambar post
                .error(R.drawable.user_placeholder)
                .placeholder(R.drawable.user_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage);

        if (user.isFollowed() == true) {
            holder.addFriendButton.setText("Berhenti Ikuti");
            holder.addFriendButton.setEnabled(true);
        } else {
            holder.addFriendButton.setText("Ikuti");
            holder.addFriendButton.setEnabled(true);
        }

        holder.addFriendButton.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            String token = "Bearer " + databaseHelper.getToken();

            ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
            int userIdToFollow = user.getId();

            // Perbarui teks tombol langsung setelah klik
            if (user.isFollowed()) {
                // Jika sudah mengikuti, ubah tombol menjadi "Follow" dan set status isFollowed ke false
                holder.addFriendButton.setText("Ikuti");
                user.setFollowed(false);
            } else {
                // Jika belum mengikuti, ubah tombol menjadi "Unfollow" dan set status isFollowed ke true
                holder.addFriendButton.setText("Berhenti Ikuti");
                user.setFollowed(true);
            }

            // Nonaktifkan tombol sementara selama request API
            holder.addFriendButton.setEnabled(false);

            // Lakukan follow atau unfollow berdasarkan status
            if (user.isFollowed()) {
                // Jika belum mengikuti, lakukan follow
                apiService.followUser(token, userIdToFollow).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            // Jika gagal, kembalikan teks tombol ke "Follow"
                            holder.addFriendButton.setText("Ikuti");
                            user.setFollowed(false); // Update status lokal
                            Log.e("FOLLOW_FAILED", "Gagal mengikuti. Kode respons: " + response.code());
                        }
                        holder.addFriendButton.setEnabled(true); // Aktifkan kembali tombol
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Jika gagal, kembalikan teks tombol ke "Follow"
                        holder.addFriendButton.setText("Ikuti");
                        user.setFollowed(false); // Update status lokal
                        Log.e("FOLLOW_ERROR", "Kesalahan jaringan: " + t.getMessage());
                        holder.addFriendButton.setEnabled(true); // Aktifkan kembali tombol
                    }
                });
            } else {
                // Jika sudah mengikuti, lakukan unfollow
                apiService.unfollowUser(token, userIdToFollow).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            // Jika gagal, kembalikan teks tombol ke "Unfollow"
                            holder.addFriendButton.setText("Berhenti Ikuti");
                            user.setFollowed(true); // Update status lokal
                            Log.e("UNFOLLOW_FAILED", "Gagal berhenti mengikuti. Kode respons: " + response.code());
                        }
                        holder.addFriendButton.setEnabled(true); // Aktifkan kembali tombol
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Jika gagal, kembalikan teks tombol ke "Unfollow"
                        holder.addFriendButton.setText("Berhenti Ikuti");
                        user.setFollowed(true); // Update status lokal
                        Log.e("UNFOLLOW_ERROR", "Kesalahan jaringan: " + t.getMessage());
                        holder.addFriendButton.setEnabled(true); // Aktifkan kembali tombol
                    }
                });
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onProfileClickListener != null) {
                    onProfileClickListener.onProfileClick(user.getId());
                }
            }
        });

    }

    public void updateFriendList(List<User> newFriendList) {
        users = newFriendList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, bioTextView;
        ImageView profileImage;
        LinearLayout linearLayout;
        Button addFriendButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_username);
            bioTextView = itemView.findViewById(R.id.tv_bio);
            linearLayout = itemView.findViewById(R.id.LL1);
            profileImage = itemView.findViewById(R.id.profile_image);
            addFriendButton = itemView.findViewById(R.id.action);
        }
    }

    public interface OnProfileClickListener {
        void onProfileClick(int userId);
    }
}
