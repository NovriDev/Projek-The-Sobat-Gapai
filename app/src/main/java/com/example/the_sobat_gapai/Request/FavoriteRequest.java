package com.example.the_sobat_gapai.Request;

import com.google.gson.JsonObject;

public class FavoriteRequest {
    private int tugas_id;

    public FavoriteRequest(int tugas_id) {
        this.tugas_id = tugas_id;
    }

    // Getter
    public int getTugasId() {
        return tugas_id;
    }

    // Setter
    public void setTugasId(int tugas_id) {
        this.tugas_id = tugas_id;
    }
}
