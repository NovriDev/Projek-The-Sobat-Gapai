package com.example.the_sobat_gapai.Response;

public class LoginResponse {
    private String status;
    private String access_token;
    private User user;

    public String getStatus() {
        return status;
    }

    public String getAccess_token() {
        return access_token;
    }

    public User getUser() {
        return user;
    }

    public class User {
        private int id;
        private String name;
        private String email;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}