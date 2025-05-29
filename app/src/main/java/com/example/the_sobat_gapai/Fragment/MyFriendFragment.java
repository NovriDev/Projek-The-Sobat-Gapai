package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_sobat_gapai.Adapter.FriendListAdapter;
import com.example.the_sobat_gapai.Adapter.MyFriendListAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.ActivityMainBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendFragment extends Fragment implements MyFriendListAdapter.OnProfileClickListener{

    private RecyclerView recyclerView;
    private MyFriendListAdapter friendListAdapter;
    private List<User> friendList;
    private ActivityMainBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        binding = ActivityMainBinding.inflate(inflater, container, false);  // Perbaiki binding inflater
        binding.toolbarTitle.setText("Pertemanan");

        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulasi Refresh dengan Delay 2 Detik
            new Handler().postDelayed(() -> {
                getFollowers(); // Perbarui Data
                swipeRefreshLayout.setRefreshing(false); // Hentikan Refreshing
            }, 2000);
        });

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Atur Adapter
        friendList = new ArrayList<>();
        friendListAdapter = new MyFriendListAdapter(getContext(), friendList);
        friendListAdapter.setOnProfileClickListener(this); // Set listener agar callback dipanggil
        recyclerView.setAdapter(friendListAdapter);

        getFollowers();

        return view;
    }


    private void getFollowers() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        int id_user = databaseHelper.getUserId();  // Mendapatkan ID pengguna
        String token = "Bearer " + databaseHelper.getToken();

        if (id_user == -1) {
            Log.e("USER_ID_ERROR", "ID pengguna tidak ditemukan.");
            return; // Jika ID tidak ditemukan, hentikan eksekusi
        }

        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);

        // Panggil API untuk mendapatkan followers
        apiService.getFollowers(token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonResponse = response.body();
                        JsonArray followersArray = jsonResponse.getAsJsonArray("followers");

                        List<User> followersList = new ArrayList<>();
                        for (JsonElement followerElement : followersArray) {
                            JsonObject followerObject = followerElement.getAsJsonObject();

                            // Ambil informasi follower
                            User follower = new User();
                            follower.setId(followerObject.get("id").getAsInt());
                            follower.setName(followerObject.get("name").getAsString());
                            follower.setEmail(followerObject.get("email").getAsString());
                            follower.setProfilePicture(
                                    followerObject.get("profilePicture").isJsonNull()
                                            ? null
                                            : followerObject.get("profilePicture").getAsString()
                            );
                            follower.setProfileBio(
                                    followerObject.get("profileBio").isJsonNull()
                                            ? null
                                            : followerObject.get("profileBio").getAsString()
                            );
                            follower.setLevel(followerObject.get("level").getAsInt());

                            followersList.add(follower);
                        }

                        // Perbarui data di adapter yang sudah ada
                        friendList.clear();
                        friendList.addAll(followersList);
                        friendListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PARSE_ERROR", "Error parsing followers data: " + e.getMessage());
                    }
                } else {
                    Log.e("GET_FOLLOWERS_FAILED", "Gagal mendapatkan followers. Kode respons: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("GET_FOLLOWERS_ERROR", "Kesalahan jaringan: " + t.getMessage());
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

    @Override
    public void onProfileClick(int userId) {
        ViewProfileFragment profileFragment = new ViewProfileFragment();
        // Kirim userId ke ProfileFragment
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", userId);
        profileFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
