package com.example.the_sobat_gapai.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Answers {
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("tugas_id")
    private int tugasId;
    private String description;
    private String imageAnswer;
    private int likes;
    private int dislikes;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    private User user;
    private JsonObject userVote;
    // Getters and Setters
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

    public int getTugasId() {
        return tugasId;
    }

    public void setTugasId(int tugasId) {
        this.tugasId = tugasId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageAnswer() {
        return imageAnswer;
    }

    public void setImageAnswer(String imageAnswer) {
        this.imageAnswer = imageAnswer;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JsonObject getUserVote() {
        return userVote;
    }

    public void setUserVote(JsonObject userVote) {
        this.userVote = userVote;
    }
}