package com.example.the_sobat_gapai.Request;

public class VoteRequest {
    private int user_id;
    private int answer_id;
    private String type;

    public VoteRequest(int user_id, int answer_id, String type) {
        this.user_id = user_id;
        this.answer_id = answer_id;
        this.type = type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
