package com.alex.edudus.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alex.edudus.R;
import com.alex.edudus.databinding.ActSplaceBinding;
import com.alex.edudus.databinding.ActivityMainBinding;

import java.util.Objects;

public class ActSplace extends AppCompatActivity {

    ActSplaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActSplaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply the animation to the TextView
        binding.tvCaption.startAnimation(fadeInAnimation);
        binding.tvAppName.startAnimation(fadeInAnimation);

        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActSplace.this,ActLogin.class);
                startActivity(intent);
            }
        },6000);
    }
}