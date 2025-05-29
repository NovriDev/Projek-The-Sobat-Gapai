package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_sobat_gapai.Model.Notifikasi;
import com.example.the_sobat_gapai.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifikasiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private List<Object> notificationList;

    public NotifikasiAdapter(Context context, List<Object> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationList.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification_time, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notifications, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String headerText = (String) notificationList.get(position);
            Log.d("HeaderText", "Header: " + headerText);
            ((HeaderViewHolder) holder).headerTitle.setText((String) notificationList.get(position));
            ((HeaderViewHolder) holder).headerTitle.setText(headerText != null ? headerText : "No Header");
        } else if (holder instanceof NotificationViewHolder) {
            Notifikasi notification = (Notifikasi) notificationList.get(position);

            // Cek jika SourceUser tidak null sebelum mengambil nama
            if (notification.getSourceUser() != null && notification.getSourceUser().getName() != null) {
                ((NotificationViewHolder) holder).username.setText(notification.getSourceUser().getName());
            } else {
                ((NotificationViewHolder) holder).username.setText("Unknown User");
            }

            // Cek jika message ada
            if (getMessage(notification) != null) {
                ((NotificationViewHolder) holder).message.setText(getMessage(notification));
            } else {
                ((NotificationViewHolder) holder).message.setText("No message");
            }

            // Cek jika createdAt tidak null dan valid
            if (notification.getCreatedAt() != null) {
                ((NotificationViewHolder) holder).timestamp.setText(formatDate(notification.getCreatedAt()));
            } else {
                ((NotificationViewHolder) holder).timestamp.setText("Unknown date");
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private String getMessage(Notifikasi notification) {
        Log.d("Type", "getMessage: " + notification.getType());

        // Ambil nilai type sebagai String
        String type = notification.getType();
        if (type != null) {
            switch (type.toLowerCase()) { // Gunakan toLowerCase() untuk menghindari masalah case-sensitive
                case "follow":
                    return " mulai mengikuti Anda.";
                case "like":
                    return " menyukai tugas Anda.";
                default:
                    return "Notifikasi baru.";
            }
        } else {
            return "Notifikasi baru.";
        }
    }


    private String formatDate(Date date) {
        if (date == null) {
            return "Waktu tidak tersedia";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(date);
    }


    // ViewHolder untuk Item
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, timestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.pesan_notifikasi);
            timestamp = itemView.findViewById(R.id.notificationTime);
        }
    }

    // ViewHolder untuk Header
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }
}