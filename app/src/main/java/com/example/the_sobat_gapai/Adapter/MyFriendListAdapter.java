package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Fragment.ViewProfileFragment;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendListAdapter extends RecyclerView.Adapter<MyFriendListAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;
    private OnProfileClickListener onProfileClickListener;

    public MyFriendListAdapter(Context context, List<User> users) {
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
        holder.addFriendButton.setText("Mengikuti Anda");
        holder.addFriendButton.setEnabled(false);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onProfileClickListener != null) {
                    onProfileClickListener.onProfileClick(user.getId());
                }
            }
        });

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
