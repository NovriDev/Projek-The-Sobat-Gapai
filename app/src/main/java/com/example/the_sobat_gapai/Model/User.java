package com.example.the_sobat_gapai.Model;


import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String name;
    private String email;
    private String profilePicture;
    private String profileBio;
    private int level;
    @SerializedName("created_at")
    private String created_at;
    private boolean isFollowed;

    public User(int id, String name, String email, String profilePicture, String profileBio, boolean isFollowed, String created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.profileBio = profileBio;
        this.isFollowed = isFollowed;
        this.created_at = created_at;
    }

    public User() {
    }

    // Getter dan Setter
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
