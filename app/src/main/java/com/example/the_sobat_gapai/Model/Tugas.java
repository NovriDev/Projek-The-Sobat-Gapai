package com.example.the_sobat_gapai.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tugas {
    private int id;
    private String username;
    private String keterangan;
    private String mapel;
    private String waktu;
    private String deskripsi;
    private String image_tugas;
    private User user;
    private int jml_likes;
    private boolean isFavorite;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("answers")
    private List<Answers> answers = new ArrayList<>();


    public Tugas(int id, String username, String keterangan, String image_tugas, String mapel, String waktu, int jml_likes, User user, String deskripsi, boolean isFavorite,
                 String createdAt, List<Answers> answers) {
        this.id = id;
        this.username = username;
        this.keterangan = keterangan;
        this.image_tugas = image_tugas;
        this.mapel = mapel;
        this.waktu = waktu;
        this.deskripsi = deskripsi;
        this.jml_likes = jml_likes;
        this.user = user;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.answers = answers;
    }

    // Getter dan Setter


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getMapel() {
        return mapel;
    }

    public void setMapel(String mapel) {
        this.mapel = mapel;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getImageTugas() {
        return image_tugas;
    }

    public void setImageTugas(String imageTugas) {
        this.image_tugas = imageTugas;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getJmlLikes() {
        return jml_likes;
    }

    public void setJmlLikes(int jmlLikes) {
        this.jml_likes = jmlLikes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<Answers> getJawaban() {
        return answers;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

