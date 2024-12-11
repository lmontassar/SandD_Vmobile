package com.example.sandd_vmobile.fragments;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sandd_vmobile.LogInActivity;
import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;

public class ProfileFragment extends Fragment {
    private Button logout;
    private TextView firstname;
    private TextView lastname;
    private TextView amount;
    private ImageView image;
    private User user;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout = view.findViewById(R.id.Logout);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        amount = view.findViewById(R.id.amount);
        image = view.findViewById(R.id.profile);
        user = (User) UserSerializer.loadUser(getContext());
        if (user != null) {
            firstname.setText(user.getFirstname());
            lastname.setText(user.getLastname());
            String imageUrl = user.getImageUrl();
            System.out.println(imageUrl);
            if(imageUrl.charAt(0)=='/'){
                imageUrl = "http://192.168.1.5:8089"+imageUrl;
            }

            // Set the amount
            amount.setText((int) user.getAmount() + " TND");

            // Load the profile image
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                    .placeholder(R.drawable.defaul_profile) // Optional: Add a placeholder image
                    .error(R.drawable.defaul_profile) // Optional: Add an error image
                    .into(image); // Load into ImageView
        }
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