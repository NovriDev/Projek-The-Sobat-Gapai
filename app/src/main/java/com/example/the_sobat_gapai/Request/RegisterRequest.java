package com.example.the_sobat_gapai.Request;

public class RegisterRequest {

    private String email;
    private String name;
    private String password;
    private String password_confirmation;

    public RegisterRequest(String email, String name, String password, String password_confirmation) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }
}
