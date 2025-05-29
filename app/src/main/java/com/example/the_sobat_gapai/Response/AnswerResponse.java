package com.example.the_sobat_gapai.Response;

import com.example.the_sobat_gapai.Model.Answers;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnswerResponse {
    @SerializedName("answers")
    private List<Answers> answers;

    public List<Answers> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }
}
