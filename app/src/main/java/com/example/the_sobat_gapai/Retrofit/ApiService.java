package com.example.the_sobat_gapai.Retrofit;

import com.example.the_sobat_gapai.Model.Notifikasi;
import com.example.the_sobat_gapai.Model.Transaction;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.Request.FavoriteRequest;
import com.example.the_sobat_gapai.Request.LoginRequest;
import com.example.the_sobat_gapai.Request.RegisterRequest;
import com.example.the_sobat_gapai.Request.UpdateProfileRequest;
import com.example.the_sobat_gapai.Request.VoteRequest;
import com.example.the_sobat_gapai.Response.LoginResponse;
import com.example.the_sobat_gapai.Response.RegisterResponse;
import com.example.the_sobat_gapai.Response.UpdateProfileResponse;
import com.example.the_sobat_gapai.Response.VoteResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @POST("api/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    @PUT("api/profile/update")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);
    // Endpoint untuk mendapatkan tugas yang diupload oleh pengguna
    @GET("api/tugas")
    Call<List<Tugas>> getTugas();
    @GET("api/tugas/{tugasId}")
    Call<Tugas> getTugasOther(@Header("Authorization") String token, @Path("tugasId") int tugasId);
    @PUT("api/users/{userId}")
    Call<ResponseBody> updateUser(@Header("Authorization") String token, @Header("Accept") String accept, @Path("userId") int userId, @Body Map<String, Integer> levelData );
    @GET("api/user-tugas")
    Call<List<Tugas>> getUserTugas(@Header("Authorization") String token);
    @GET("api/user-profile-tugas/{userId}")
    Call<List<Tugas>> getUserTugasWithId(@Header("Authorization") String token, @Path("userId") int userId);
    // Endpoint untuk menghapus tugas berdasarkan ID
    @DELETE("api/user-tugas/{id}")
    Call<Void> deleteUserTugas(@Header("Authorization") String token, @Path("id") int tugasId);
    @GET("api/suggested-friends")
    Call<List<User>> getFriends(@Header("Authorization") String token);
    @POST("api/follow/{userId}")
    Call<User> followUser(@Header("Authorization") String token, @Path("userId") int userId);
    @POST("api/unfollow/{userId}")
    Call<User> unfollowUser(@Header("Authorization") String token, @Path("userId") int userId);
    @GET("api/notifications")
    Call<List<Notifikasi>> getNotifications(@Header("Authorization") String token);
    @POST("api/favorites")
    Call<JsonObject> addFavorite(
            @Header("Authorization") String token,
            @Body FavoriteRequest favoriteRequest
    );
    @DELETE("api/favorites")
    Call<JsonObject> removeFavorite(
            @Header("Authorization") String token,
            @Query("tugas_id") int tugasId
    );
    @GET("api/favorites/status/{tugasId}")
    Call<JsonObject> getFavoriteStatus(
            @Header("Authorization") String token,
            @Path("tugasId") int tugasId
    );
    @GET("api/answers/{tugasId}")
    Call<ResponseBody> getAnswers(@Header("Authorization") String token, @Path("tugasId") int tugasId);
    @GET("api/profile")
    Call<Object> getProfile(@Header("Authorization") String token);
    @GET("api/other-profile/{userId}")
    Call<Object> getOtherProfile(@Header("Authorization") String token, @Path("userId") int userId);
    @POST("api/logout")
    Call<Void> logout(@Header("Authorization") String token);
    @GET("api/user/followers")
    Call<JsonObject> getFollowers(@Header("Authorization") String token);
    @POST("api/answers/vote")
    Call<Void> sendVote(@Header("Authorization") String token, @Body VoteRequest voteRequest);
    @GET("api/answers/{answerId}/votes")
    Call<VoteResponse> getVotes(@Header("Authorization") String token, @Path("answerId") int answerId);
    @GET("api/transactions")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String token);
    /*@POST("api/tugas/upload")
    Call<ResponseBody> uploadTugas(
            @Header("Authorization") String token,
            @Header("Content-Type") String contentType,
            @Header("Accept") String accept, // Menambahkan header Accept
            @Body RequestBody body
    );*/
    @Multipart
    @POST("api/tugas/upload")
    Call<ResponseBody>  uploadTugas(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Part("keterangan") RequestBody keteranganField,
            @Part("mapel") RequestBody mapelField,
            @Part("deskripsi") RequestBody descriptionField,
            @Part MultipartBody.Part imageAnswer // Mengirimkan file gambar
    );

//    @FormUrlEncoded
//    @POST("api/{tugasId}/answers")
//    Call<ResponseBody> postAnswer(
//            @Header("Authorization") String token,
//            @Header("Accept") String accept,
//            @Path("tugasId") int tugasId,
//            @Field("tugas_id") int tugasIdField,
//            @Field("user_id") int userId,
//            @Field("description") String description,
//            @Field("imageAnswer") String imageAnswer
//    );

    @Multipart
    @POST("api/{tugasId}/answers")
    Call<ResponseBody> postAnswer(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Path("tugasId") int tugasId,
            @Part("tugas_id") RequestBody tugasIdField,
            @Part("user_id") RequestBody userId,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part imageAnswer // Mengirimkan file gambar
    );
    @Multipart
    @POST("api/{tugasId}/answers")
    Call<ResponseBody> postAnswerNotWithImage(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Path("tugasId") int tugasId,
            @Part("tugas_id") RequestBody tugasIdField,
            @Part("user_id") RequestBody userId,
            @Part("description") RequestBody description
    );

    @Multipart
    @POST("api/laporkan/answer")
    Call<ResponseBody> postReportAnswer(
            @Header("Authorization") String token,
            @Part("user_id") RequestBody userId,
            @Part("tugas_id") RequestBody tugasId,
            @Part("answer_id") RequestBody answerId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );

    @Multipart
    @POST("api/laporkan/tugas")
    Call<ResponseBody> postReportTugas(
            @Header("Authorization") String token,
            @Part("user_id") RequestBody userId,
            @Part("tugas_id") RequestBody tugasId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );
}
