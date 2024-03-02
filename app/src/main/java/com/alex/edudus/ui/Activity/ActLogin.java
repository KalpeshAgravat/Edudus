package com.alex.edudus.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alex.edudus.R;
import com.alex.edudus.databinding.ActLoginBinding;
import com.alex.edudus.databinding.ActSplaceBinding;

public class ActLogin extends AppCompatActivity {

    ActLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();

    }

    private void  initialize(){
        // TODO perform login
        binding.btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}