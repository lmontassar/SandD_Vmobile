package com.example.sandd_vmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.sandd_vmobile.LogInActivity;
import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.util.UserSerializer;

public class ProfileFragment extends Fragment {
    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout = view.findViewById(R.id.Logout);

        logout.setOnClickListener(v -> {
            if (getActivity() != null) {
                UserSerializer.clearUser(getActivity());
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}