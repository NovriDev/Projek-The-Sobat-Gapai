package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;
import java.util.List;

public class ViewProfileAdapter extends RecyclerView.Adapter<ViewProfileAdapter.ProfileViewHolder> {
    Context context;
    private List<Tugas> tugasList;
    private OnTugasClickListener onTugasClickListener;


    public ViewProfileAdapter(Context context, List<Tugas> tugasList) {
        this.context = context;
        this.tugasList = tugasList;
    }

    public void setOnTugasClickListener(OnTugasClickListener listener) {
        this.onTugasClickListener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas_user, parent, false);
        return new ProfileViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Tugas tugas = tugasList.get(position);

        // Set post image (tugas.getImageTugas() adalah URL atau path gambar)
        Glide.with(holder.itemView.getContext())
                .load(tugas.getImageTugas()) // Gambar post
                .error(R.drawable.img_mapel_placeholder)
                .into(holder.postImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTugasClickListener != null) {
                    onTugasClickListener.onTugasClick(tugas.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage); // pastikan postImage adalah ImageView
        }
    }

    public interface OnTugasClickListener {
        void onTugasClick(int tugasId);
    }

}