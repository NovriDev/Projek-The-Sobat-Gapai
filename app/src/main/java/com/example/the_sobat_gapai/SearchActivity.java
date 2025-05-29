package com.example.the_sobat_gapai;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.the_sobat_gapai.Adapter.ViewPagerAdapter;
import com.example.the_sobat_gapai.Fragment.SearchableFragment;
import com.example.the_sobat_gapai.databinding.SearchActivityBinding;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    SearchActivityBinding binding;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = SearchActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());

        // Inisialisasi ViewPager dan TabLayout
        viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Atur SearchView
        searchView = binding.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sendQueryToActiveFragment(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sendQueryToActiveFragment(newText);
                return false;
            }
        });

        // Ubah warna teks SearchView
        try {
            Field field = SearchView.class.getDeclaredField("mSearchSrcTextView");
            field.setAccessible(true);
            TextView searchText = (TextView) field.get(searchView);
            searchText.setTextColor(Color.WHITE);
            searchText.setHintTextColor(Color.GRAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kirim query ke fragment yang sedang aktif
    private void sendQueryToActiveFragment(String query) {
        int currentItem = viewPager.getCurrentItem();
        Fragment activeFragment = adapter.getItem(currentItem);

        if (activeFragment instanceof SearchableFragment) {
            ((SearchableFragment) activeFragment).onSearchQuery(query);
        }
    }

}
