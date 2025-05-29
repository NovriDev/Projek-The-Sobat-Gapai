package com.example.the_sobat_gapai.Request;

public class UpdateProfileRequest {
    private String name;
    private String email;
    private String password;
    private String profileBio;

    // Constructor, getters, and setters

    public UpdateProfileRequest(String name, String email, String password, String profileBio) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileBio = profileBio;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }
}