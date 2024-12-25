package com.example.sandd_vmobile.fragments;

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
import com.example.sandd_vmobile.EditProfileActivity;
import com.example.sandd_vmobile.LogInActivity;
import com.example.sandd_vmobile.R;
import com.example.sandd_vmobile.controllers.UserController;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;

public class ProfileFragment extends Fragment {
    private Button logout;
    private TextView fullName;
    private TextView email;
    private TextView amount;
    private ImageView image;
    private Button edit;
    private User user;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        UserController userController = new UserController(getContext());
        logout = view.findViewById(R.id.logout);
        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.email);
        amount = view.findViewById(R.id.amount);
        image = view.findViewById(R.id.profile);
        edit = view.findViewById(R.id.editLink);

        Long id = ((User) UserSerializer.loadUser(getContext())).getId();
        user = new User();

        // Fetch user data asynchronously
        userController.getUser(id, new UserController.UserFetchCallback() {
            @Override
            public void onSuccess(User u) {
                // Update the UI with the user data
                user = u;
                UserSerializer.saveUser(getContext(),u);
                requireActivity().runOnUiThread(() -> updateUI(user));
            }

            @Override
            public void onFailure(String message) {
                requireActivity().runOnUiThread(() ->
                        email.setText("Error fetching user: " + message)
                );
            }
        });

        // Edit profile button listener
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout button listener
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

    private void updateUI(User user) {
        if (user != null) {
            fullName.setText(user.getFirstname() + " " + user.getLastname());
            email.setText(user.getEmail());

            // Handle the image URL
            String imageUrl = user.getImageUrl();
            if (imageUrl != null && imageUrl.charAt(0) == '/') {
                String baseUrl = getContext().getString(R.string.baseUrl);
                imageUrl = baseUrl + imageUrl;
            }

            // Set the amount
            amount.setText(user.getAmount() + " TND");

            // Load the profile image
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                    .placeholder(R.drawable.defaul_profile) // Optional: Add a placeholder image
                    .error(R.drawable.defaul_profile) // Optional: Add an error image
                    .into(image);
        }
    }
}
