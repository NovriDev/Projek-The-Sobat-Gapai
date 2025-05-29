package com.example.the_sobat_gapai.Model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notifikasi {
    private int id;                 // ID notifikasi
    private int userId;             // ID user yang menerima notifikasi
    private int sourceUserId;       // ID user yang menyebabkan notifikasi
    private int taskId;             // ID tugas (bisa null jika type 'follow')
    private boolean isRead;         // Status apakah notifikasi sudah dibaca
    @SerializedName("created_at")
    private Date createdAt;         // Waktu notifikasi dibuat
    private Date updatedAt;         // Waktu notifikasi diperbarui
    @SerializedName("source_user")
    private SourceUser sourceUser;  // Informasi user yang menyebabkan notifikasi
    @SerializedName("type")
    private String type;



    // Subclass untuk SourceUser
    public static class SourceUser {
        private int id;
        private String name;

        public SourceUser(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Konstruktor
    public Notifikasi(int id, int userId, String type, int sourceUserId, int taskId, boolean isRead, String createdAt, String updatedAt, SourceUser sourceUser) throws ParseException {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.sourceUserId = sourceUserId;
        this.taskId = taskId;
        this.isRead = isRead;
        this.createdAt = parseDate(createdAt);
        this.updatedAt = parseDate(updatedAt);
        this.sourceUser = sourceUser; // Menambahkan informasi SourceUser
    }

    // Method untuk parsing tanggal
    private Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return sdf.parse(dateStr);
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(int sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SourceUser getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(SourceUser sourceUser) {
        this.sourceUser = sourceUser;
    }
}
