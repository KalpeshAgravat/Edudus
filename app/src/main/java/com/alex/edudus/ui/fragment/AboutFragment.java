package com.alex.edudus.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alex.edudus.BaseFragment;
import com.alex.edudus.databinding.FragmentDashboardBinding;
import com.alex.edudus.databinding.FrgAboutBinding;

public class AboutFragment extends BaseFragment {

    private FrgAboutBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FrgAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}