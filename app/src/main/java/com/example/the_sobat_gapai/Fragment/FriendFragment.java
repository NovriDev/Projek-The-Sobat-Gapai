package com.example.the_sobat_gapai.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_sobat_gapai.Adapter.FriendListAdapter;
import com.example.the_sobat_gapai.Adapter.MyFriendListAdapter;
import com.example.the_sobat_gapai.Adapter.TugasAdapter;
import com.example.the_sobat_gapai.DB_HELPER.DatabaseHelper;
import com.example.the_sobat_gapai.Model.Tugas;
import com.example.the_sobat_gapai.Model.User;
import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.Retrofit.ApiService;
import com.example.the_sobat_gapai.Retrofit.RetrofitClient;
import com.example.the_sobat_gapai.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFragment extends Fragment implements SearchableFragment, FriendListAdapter.OnProfileClickListener {

    private RecyclerView recyclerView;
    private FriendListAdapter friendListAdapter;
    private List<User> friendList;
    private List<User> filteredFriendList;
    private ActivityMainBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView notFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Fragment
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.toolbarTitle.setText("Pertemanan");
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulasi Refresh dengan Delay 2 Detik
            new Handler().postDelayed(() -> {
                // Perbarui Data
                loadSuggestedFriends();
                // Hentikan Refreshing
                swipeRefreshLayout.setRefreshing(false);
            }, 2000);
        });

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notFound = view.findViewById(R.id.noResultsTextView);


        // Atur Adapter
        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getContext(), friendList);
        friendListAdapter.setOnProfileClickListener(FriendFragment.this); // Set listener agar callback dipanggil
        recyclerView.setAdapter(friendListAdapter);


        loadSuggestedFriends();

        return view;
    }

    private void loadSuggestedFriends() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String token = "Bearer " + databaseHelper.getToken();
//        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getApiService().create(ApiService.class);
        apiService.getFriends(token).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendList.clear();
                    friendList.addAll(response.body());
                    friendListAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Error 1", "onResponse - Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("Error 1", "Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("Error 1", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("Error 1", "Message: " + response.message());
                }
            }


            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("Error 2", "onFailure:" + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }

    @Override
    public void onSearchQuery(String query) {
        // Filter daftar teman berdasarkan nama yang cocok dengan query pencarian
        if (TextUtils.isEmpty(query)) {
            filteredFriendList.clear();
            filteredFriendList.addAll(friendList);
        } else {
            filteredFriendList = friendList.stream()
                    .filter(friend -> friend.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        friendListAdapter.updateFriendList(filteredFriendList);

        if (filteredFriendList.isEmpty()) {
            // Tampilkan pesan jika tidak ada teman yang ditemukan
            notFound.setVisibility(View.VISIBLE);
        } else {
            // Sembunyikan pesan jika ada teman yang ditemukan
            notFound.setVisibility(View.GONE);
        }
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
