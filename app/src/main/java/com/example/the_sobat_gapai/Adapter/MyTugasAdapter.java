package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyTugasAdapter extends RecyclerView.Adapter<MyTugasAdapter.TugasViewHolder> {
    private Context context;
    private List<Tugas> tugasList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int tugasId);
    }

    public MyTugasAdapter(Context context, List<Tugas> tugasList, OnDeleteClickListener listener) {
        this.context = context;
        this.tugasList = tugasList;
        this.onDeleteClickListener = listener;
    }

    @Override
    public TugasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new TugasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TugasViewHolder holder, int position) {
        Tugas tugas = tugasList.get(position);
        holder.keterangan.setText(tugas.getKeterangan());
        holder.mapel.setText(tugas.getMapel());
        holder.jmlLikes.setText(String.valueOf(tugas.getJmlLikes()));
        holder.deskripsi.setText(tugas.getDeskripsi());

        String createdAt = tugas.getCreatedAt();
        // Format tanggal
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Pastikan input UTC

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            // Parse tanggal dari server dan ubah formatnya
            Date date = inputFormat.parse(createdAt);
            String formattedDate = outputFormat.format(date);

            // Set hasil ke TextView
            holder.waktu.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.waktu.setText("Invalid date"); // Tangani error jika parsing gagal
        }

        Glide.with(holder.itemView.getContext())
                .load(tugas.getImageTugas()) // Gambar post
                .error(R.drawable.img_mapel_placeholder)
                .into(holder.imageView);

        // Handle delete click
        holder.delete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(tugas.getId()));
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }


    public class TugasViewHolder extends RecyclerView.ViewHolder {
        TextView waktu, deskripsi, keterangan, mapel, jmlLikes, delete;
        ImageView imageView;

        public TugasViewHolder(View itemView) {
            super(itemView);
            keterangan = itemView.findViewById(R.id.tv_keterangan);
            mapel = itemView.findViewById(R.id.tv_mapel);
            waktu = itemView.findViewById(R.id.tv_waktu);
            deskripsi = itemView.findViewById(R.id.tv_deskripsi);
            imageView = itemView.findViewById(R.id.post_image);
            jmlLikes = itemView.findViewById(R.id.tv_like_count);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
