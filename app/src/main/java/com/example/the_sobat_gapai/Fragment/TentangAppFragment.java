package com.example.the_sobat_gapai.Fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.the_sobat_gapai.R;
import com.example.the_sobat_gapai.databinding.FormProfileBinding;
import com.example.the_sobat_gapai.databinding.TentangTheSobatGapaiActBinding;

public class TentangAppFragment extends Fragment {
    TentangTheSobatGapaiActBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TentangTheSobatGapaiActBinding.inflate(inflater, container, false);
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Terapkan animasi
        binding.judul.startAnimation(slideUp);
        binding.isi.startAnimation(slideUp);
        return binding.getRoot();
    }
}